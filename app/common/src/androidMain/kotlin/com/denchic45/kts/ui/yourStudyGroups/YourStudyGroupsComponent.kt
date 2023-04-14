package com.denchic45.kts.ui.yourStudyGroups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.lifecycle.subscribe
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.ui.DropdownMenuItem
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.onString
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class YourStudyGroupsComponent(
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    @Assisted
    private val onGroupEditClick: (UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val openGroupEditor = MutableSharedFlow<String>()

    val appBarState = MutableStateFlow(
        AppBarState(
            title = uiTextOf("Ваши группы"),
            onDropdownMenuItemClick = {
                it.title.onString {
                    when (it) {
                        "Редактировать" -> {
                            onGroupEditClick(selectedStudyGroup.value?.id!!)
                        }
                    }
                }
            })
    )

    val studyGroups = flow { emit(findYourStudyGroupsUseCase()) }.stateInResource(componentScope)

    val selectedStudyGroup = MutableStateFlow<StudyGroupResponse?>(null)

    init {
        componentScope.launch {
            studyGroups.collect {
                it.onSuccess { groups ->
                    selectedStudyGroup.update {
                        groups.firstOrNull()
                    }
                }
            }
        }

        lifecycle.subscribe(
            onCreate = { println("LIFECYCLE: create") },
            onStart = { println("LIFECYCLE: start") },
            onResume = { println("LIFECYCLE: resume") },
            onPause = { println("LIFECYCLE: pause") },
            onStop = { println("LIFECYCLE: stop") },
            onDestroy = { println("LIFECYCLE: destroy") }
        )

        lifecycle.subscribe(onResume = {
            selectedStudyGroup.onEach { selectedStudyGroup ->
                appBarState.update {
                    it.copy(
                        title = uiTextOf(selectedStudyGroup?.name ?: "Выберите группу"),
                        dropdown = selectedStudyGroup?.let {
                            listOf(DropdownMenuItem("edit", uiTextOf("Редактировать")))
                        } ?: emptyList(),
                        onDropdownMenuItemClick = {
                            when (it.id) {
                                "edit" -> componentScope.launch {
                                    openGroupEditor.emit(
                                        selectedStudyGroup!!.id.toString()
                                    )
                                }
                            }
                        })
                }
            }.launchIn(componentScope)
        })
    }

    fun onGroupSelect(index: Int) {
        studyGroups.value.onSuccess {
            selectedStudyGroup.value = it[index]
        }
    }
}