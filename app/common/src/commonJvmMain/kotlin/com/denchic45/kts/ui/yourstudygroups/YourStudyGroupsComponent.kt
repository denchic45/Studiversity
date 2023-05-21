package com.denchic45.kts.ui.yourstudygroups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.domain.flatMapResourceFlow
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.ui.studygroup.StudyGroupComponent
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class YourStudyGroupsComponent(
    private val appPreferences: AppPreferences,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    private val _studyGroupComponent: (
        onCourseOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupComponent,
    private val studyGroupEditorComponent: (
        onFinish: () -> Unit,
        UUID?,
        ComponentContext,
    ) -> StudyGroupEditorComponent,
//    @Assisted
//    private val onStudyGroupEditorOpen: (UUID) -> Unit,
    @Assisted
    onCourseOpen: (UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    object Config
    object Child

    private val componentScope = componentScope()


    private val studyGroupNavigation = OverlayNavigation<StudyGroupConfig>()

    val childStudyGroup = childOverlay(
        source = studyGroupNavigation,
        childFactory = { config, componentContext ->
            _studyGroupComponent(onCourseOpen, config.studyGroupId, componentContext)
        },
        key = "StudyGroup"
    )

    @Parcelize
    data class StudyGroupConfig(val studyGroupId: UUID) : Parcelable


//    val appBarState = MutableStateFlow(
//        AppBarState(
//            title = uiTextOf("Ваши группы"),
//            onDropdownMenuItemClick = {
//                it.title.onString { action ->
//                    when (action) {
//                        "Редактировать" -> {
//                            selectedStudyGroup.value.onSuccess {
//                                onStudyGroupEditClick(it.id)
//                            }
//                        }
//                    }
//                }
//            })
//    )

    val studyGroups = findYourStudyGroupsUseCase().stateInResource(componentScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedStudyGroup = appPreferences.selectedStudyGroupIdFlow
        .flatMapLatest { id ->
            studyGroups.mapResource { groups ->
                id?.apply {
                    appPreferences.selectedStudyGroupId = groups.first().id.toString()
                }?.let { id ->
                    groups.first { it.id == id.toUUID() }
                } ?: groups.first()
            }
        }.onEach { resource ->
            resource.onSuccess {
                withContext(Dispatchers.Main) {
                    studyGroupNavigation.activate(StudyGroupConfig(it.id))
                }
            }
        }.stateInResource(componentScope)

    val allowEditSelected = selectedStudyGroup.flatMapResourceFlow { response ->
        checkUserCapabilitiesInScopeUseCase(
            scopeId = response.id,
            capabilities = listOf(Capability.WriteStudyGroup)
        )
    }.mapResource { it.hasCapability(Capability.WriteStudyGroup) }
        .stateInResource(componentScope)

//    init {
//        lifecycle.subscribe(onResume = {
//            selectedStudyGroup.onEach { resource ->
//                resource.onSuccess { selectedStudyGroup ->
//                    appBarState.update {
//                        it.copy(
//                            title = uiTextOf(selectedStudyGroup.name),
//                            dropdown = resource.let {
//                                listOf(DropdownMenuItem("edit", uiTextOf("Редактировать")))
//                            },
//                            onDropdownMenuItemClick = {
//                                when (it.id) {
//                                    "edit" -> componentScope.launch {
//                                        openStudyGroupEditor.emit(
//                                            selectedStudyGroup.id.toString()
//                                        )
//                                    }
//                                }
//                            })
//                    }
//                }
//            }.launchIn(componentScope)
//        })
//    }

    fun onGroupSelect(id: UUID) {
        appPreferences.selectedStudyGroupId = id.toString()
    }

    fun onEditStudyGroupClick() {
        childStudyGroup.value.overlay?.instance?.onEditClick()
    }

}