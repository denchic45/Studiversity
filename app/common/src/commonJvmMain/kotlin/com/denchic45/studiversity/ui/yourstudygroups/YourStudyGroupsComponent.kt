package com.denchic45.studiversity.ui.yourstudygroups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.studiversity.ui.navigation.ChildrenContainer
import com.denchic45.studiversity.ui.scopemembereditor.ScopeMemberEditorComponent
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.studiversity.util.asFlow
import com.denchic45.studiversity.util.componentScope
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
        onStudyGroupOpen: (UUID) -> Unit,
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
    onStudyGroupOpen: (UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ChildrenContainer {

    private val componentScope = componentScope()

    private val studyGroupNavigation = OverlayNavigation<StudyGroupConfig>()

    val childStudyGroup = childOverlay(
        source = studyGroupNavigation,
        childFactory = { config, componentContext ->
            _studyGroupComponent(
                onCourseOpen,
                onStudyGroupOpen,
                config.studyGroupId,
                componentContext
            )
        },
        key = "StudyGroup"
    )

//    val overlayNavigation = OverlayNavigation<OverlayConfig>()
//    val childOverlay = childOverlay(
//        source = overlayNavigation,
//        childFactory = { config, context ->
//            when (config) {
//                is OverlayConfig.ScopeMemberEditor -> {
//                    OverlayChild.ScopeMemberEditor()
//                }
//            }
//        }
//    )

    @Parcelize
    data class StudyGroupConfig(val studyGroupId: UUID) : Parcelable

    val studyGroups = findYourStudyGroupsUseCase().stateInResource(componentScope)

    private val selectedStudyGroupId = appPreferences.selectedStudyGroupIdFlow.filterNotNull()
        .shareIn(componentScope, SharingStarted.Lazily)

    @OptIn(ExperimentalCoroutinesApi::class)
    val selectedStudyGroup = selectedStudyGroupId
        .flatMapLatest { selectedStudyGroupId ->
            studyGroups.mapResource { groups ->
                groups.first { it.id == selectedStudyGroupId.toUUID() }
            }
        }.onEach { resource ->
            resource.onSuccess { selectedStudyGroup ->
                withContext(Dispatchers.Main.immediate) {
                    studyGroupNavigation.activate(StudyGroupConfig(selectedStudyGroup.id))
                }
            }
        }.stateInResource(componentScope)

    @OptIn(ExperimentalCoroutinesApi::class)
    val allowEditSelected = selectedStudyGroupId.flatMapLatest { id ->
        checkUserCapabilitiesInScopeUseCase(
            scopeId = id.toUUID(),
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

    fun onAddMemberClick() {
        childStudyGroup.value.overlay?.instance?.onAddMemberClick()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun hasChildrenFlow(): Flow<Boolean> {
        return childStudyGroup.asFlow()
            .flatMapLatest { it.overlay?.instance?.hasChildrenFlow() ?: flowOf(false) }
    }

    @Parcelize
    sealed interface OverlayConfig : Parcelable {
        data class ScopeMemberEditor(val memberId: UUID) : OverlayConfig

    }

    sealed interface OverlayChild {
        class ScopeMemberEditor(val component: ScopeMemberEditorComponent) : OverlayChild
    }

}