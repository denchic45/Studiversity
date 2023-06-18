package com.denchic45.studiversity.ui.navigator

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.admindashboard.AdminDashboardComponent
import com.denchic45.studiversity.ui.course.CourseComponent
import com.denchic45.studiversity.ui.courseeditor.CourseEditorComponent
import com.denchic45.studiversity.ui.coursework.CourseWorkComponent
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.studiversity.ui.navigation.ChildrenContainerChild
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.ui.yourstudygroups.YourStudyGroupsComponent
import com.denchic45.studiversity.ui.yourtimetables.YourTimetablesComponent
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class RootNavigatorComponent(
    yourTimetablesComponent: (onStudyGroupOpen: (UUID) -> Unit, ComponentContext) -> YourTimetablesComponent,
    yourStudyGroupsComponent: (
        onCourseOpen: (UUID) -> Unit,
        onStudyGroupOpen: (UUID) -> Unit, ComponentContext,
    ) -> YourStudyGroupsComponent,
    courseComponent: (
        onFinish: () -> Unit,
        onStudyGroupOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> CourseComponent,
    studyGroupComponent: (
        onCourseOpen: (UUID) -> Unit,
        onStudyGroupOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupComponent,
    adminDashboardComponent: (StackNavigation<RootConfig>, ComponentContext) -> AdminDashboardComponent,
    courseEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID?,
        ComponentContext
    ) -> CourseEditorComponent,
    courseWorkComponent: (
        onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
        onFinish: () -> Unit,
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
    @Assisted
    initialConfiguration: RootConfig,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, RootStackChildrenContainer {
    override val navigation = StackNavigation<RootConfig>()

    override val childStack: Value<ChildStack<RootConfig, RootChild>> = childStack(
        source = navigation,
        handleBackButton = true,
        initialConfiguration = initialConfiguration,
        childFactory = { config, context ->
            when (config) {
                RootConfig.YourTimetables -> RootChild.YourTimetables(
                    yourTimetablesComponent(
                        { navigation.bringToFront(RootConfig.StudyGroup(it)) },
                        context
                    )
                )

                RootConfig.YourStudyGroups -> RootChild.YourStudyGroups(
                    yourStudyGroupsComponent(
                        { navigation.bringToFront(RootConfig.Course(it)) },
                        { navigation.bringToFront(RootConfig.StudyGroup(it)) },
                        context
                    )
                )

                is RootConfig.Course -> RootChild.Course(
                    courseComponent(
                        navigation::pop,
                        { navigation.bringToFront(RootConfig.StudyGroup(it)) },
                        config.courseId,
                        context
                    )
                )

                is RootConfig.StudyGroup -> RootChild.StudyGroup(
                    studyGroupComponent(
                        { navigation.bringToFront(RootConfig.Course(it)) },
                        { navigation.bringToFront(RootConfig.StudyGroup(it)) },
                        config.studyGroupId,
                        context
                    )
                )

                RootConfig.Works -> TODO()
                RootConfig.AdminDashboard -> RootChild.AdminDashboard(
                    adminDashboardComponent(navigation, context)
                )

                is RootConfig.CourseEditor -> RootChild.CourseEditor(
                    courseEditorComponent(
                        navigation::pop, config.courseId, context
                    )
                )

                is RootConfig.CourseWork -> RootChild.CourseWork(
                    courseWorkComponent(
                        { _, workId -> navigation.push(RootConfig.CourseWorkEditor(workId)) },
                        navigation::pop,
                        config.courseId,
                        config.workId,
                        context
                    )
                )

                is RootConfig.CourseWorkEditor -> TODO()
            }
        }
    )

    override fun hasChildrenFlow(): Flow<Boolean> {
        return super.hasChildrenFlow()
    }
}

@Parcelize
sealed interface RootConfig : Parcelable {
    object YourTimetables : RootConfig

    object YourStudyGroups : RootConfig

    object Works : RootConfig

    data class StudyGroup(val studyGroupId: UUID) : RootConfig

    data class Course(val courseId: UUID) : RootConfig

    object AdminDashboard : RootConfig

    data class CourseEditor(val courseId: UUID?) : RootConfig

    data class CourseWork(val courseId: UUID, val workId: UUID) : RootConfig

    data class CourseWorkEditor(val workId: UUID?) : RootConfig
}

sealed interface RootChild {
    class YourTimetables(
        override val component: YourTimetablesComponent,
    ) : RootChild, ChildrenContainerChild

    class YourStudyGroups(
        override val component: YourStudyGroupsComponent,
    ) : RootChild, ChildrenContainerChild

    class Works(
        override val component: RootStackChildrenContainer,
    ) : RootChild, ChildrenContainerChild

    class StudyGroup(
        override val component: StudyGroupComponent,
    ) : RootChild, ChildrenContainerChild

    class Course(
        override val component: CourseComponent,
    ) : RootChild, ChildrenContainerChild

    class AdminDashboard(
        override val component: AdminDashboardComponent
    ) : RootChild, ChildrenContainerChild

    class CourseEditor(val component: CourseEditorComponent) : RootChild

    class CourseWork(val component: CourseWorkComponent) : RootChild

    class CourseWorkEditor(val component: CourseWorkEditorComponent) : RootChild
}