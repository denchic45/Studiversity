package com.denchic45.kts.ui.yourstudygroups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.ui.DropdownMenuItem
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.onString
import com.denchic45.kts.ui.studygroup.StudyGroupComponent
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class YourStudyGroupsComponent(
    private val appPreferences: AppPreferences,
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    private val _studyGroupComponent:(UUID,ComponentContext)->StudyGroupComponent,
//    @Assisted
//    private val onStudyGroupEditClick: (UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val openStudyGroupEditor = MutableSharedFlow<String>()

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

    val studyGroups = flow { emit(findYourStudyGroupsUseCase()) }.stateInResource(componentScope)

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
        }.stateInResource(componentScope)

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
}