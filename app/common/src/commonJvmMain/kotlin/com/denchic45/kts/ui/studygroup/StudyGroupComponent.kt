package com.denchic45.kts.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.studygroup.courses.StudyGroupCoursesComponent
import com.denchic45.kts.ui.studygroup.members.StudyGroupMembersComponent
import com.denchic45.kts.ui.studygroup.timetable.StudyGroupTimetableComponent
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.kts.ui.usereditor.UserEditorComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class StudyGroupComponent(
    private val profileComponent: (UUID,ComponentContext) -> ProfileComponent,
    private val userEditorComponent: (
        onFinish: () -> Unit,
        userId: UUID?,
        ComponentContext
    ) -> UserEditorComponent,
    private val studyGroupEditorComponent: (UUID,ComponentContext)->StudyGroupEditorComponent,
    private val studyGroupMembersComponent: (UUID, ComponentContext) -> StudyGroupMembersComponent,
    private val studyGroupCoursesComponent: (UUID, ComponentContext) -> StudyGroupCoursesComponent,
    private val studyGroupTimetableComponent: (UUID, ComponentContext) -> StudyGroupTimetableComponent,
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    @Assisted
    private val onStudyGroupEditClick: (UUID) -> Unit,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

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

    val childTabs = listOf(
        TabChild.Members(
            studyGroupMembersComponent(
                studyGroupId,
                componentContext.childContext("Members")
            )
        ),
        TabChild.Courses(
            studyGroupCoursesComponent(
                studyGroupId,
                componentContext.childContext(" Курсы")
            )
        ),
        TabChild.Timetable(
            studyGroupTimetableComponent(
                studyGroupId,
                componentContext.childContext("Timetable")
            )
        )
    )

    val studyGroup = findStudyGroupByIdUseCase(studyGroupId).stateInResource(componentScope)

    val selectedTab = MutableStateFlow(0)

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
//                                        selectedStudyGroup.id.toString()
//                                    }
//                                }
//                            })
//                    }
//                }
//            }.launchIn(componentScope)
//        })
//    }

    fun onTabSelect(position:Int) {
        selectedTab.value = position
    }

    fun onEditClick() {
        onStudyGroupEditClick(studyGroupId)
    }

    sealed class TabChild(val title: String) {
        class Members(val component: StudyGroupMembersComponent) : TabChild("Участники")
        class Courses(val component: StudyGroupCoursesComponent) : TabChild("Курсы")
        class Timetable(val component: StudyGroupTimetableComponent) : TabChild("Расписание")
    }
}