package com.denchic45.kts.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
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
    private val profileComponent: (UUID, ComponentContext) -> ProfileComponent,
    private val userEditorComponent: (
        onFinish: () -> Unit,
        userId: UUID?,
        ComponentContext,
    ) -> UserEditorComponent,
    private val studyGroupEditorComponent: (
        onFinish: () -> Unit,
        UUID?,
        ComponentContext,
    ) -> StudyGroupEditorComponent,
    private val studyGroupMembersComponent: (
        onCourseOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupMembersComponent,
    private val studyGroupCoursesComponent: (UUID, ComponentContext) -> StudyGroupCoursesComponent,
    private val studyGroupTimetableComponent: (UUID, ComponentContext) -> StudyGroupTimetableComponent,
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
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

    private val overlayNavigation = OverlayNavigation<OverlayConfig>()

    val childOverlay = childOverlay(
        source = overlayNavigation,
        childFactory = { config, componentContext ->
            when (config) {
                is OverlayConfig.StudyGroupEditor -> {
                    OverlayChild.StudyGroupEditor(
                        studyGroupEditorComponent(
                            overlayNavigation::dismiss,
                            config.studyGroupId,
                            componentContext
                        )
                    )
                }
                is OverlayConfig.Member -> {
                    OverlayChild.Member(profileComponent(config.memberId, componentContext))
                }

                is OverlayConfig.UserEditor -> {
                    OverlayChild.UserEditor(
                        userEditorComponent(
                            overlayNavigation::dismiss,
                            config.userId,
                            componentContext
                        )
                    )
                }
            }
        }
    )

    val childTabs = listOf(
        TabChild.Members(
            studyGroupMembersComponent(
                {},
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

    fun onTabSelect(position: Int) {
        selectedTab.value = position
    }

    fun onEditClick() {
        overlayNavigation.activate(OverlayConfig.StudyGroupEditor(studyGroupId))
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class StudyGroupEditor(val studyGroupId: UUID) : OverlayConfig()

        data class Member(val memberId: UUID) : OverlayConfig()

        data class UserEditor(val userId: UUID) : OverlayConfig()
    }

    sealed class OverlayChild {

        class StudyGroupEditor(val component: StudyGroupEditorComponent) : OverlayChild()

        class Member(val component: ProfileComponent) : OverlayChild()

        class UserEditor(val component: UserEditorComponent) : OverlayChild()
    }

    sealed class TabChild(val title: String) {
        class Members(val component: StudyGroupMembersComponent) : TabChild("Участники")
        class Courses(val component: StudyGroupCoursesComponent) : TabChild("Курсы")
        class Timetable(val component: StudyGroupTimetableComponent) : TabChild("Расписание")
    }
}