package com.denchic45.studiversity.ui.navigator

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.*
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
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.ui.yourstudygroups.YourStudyGroupsComponent
import com.denchic45.studiversity.ui.yourtimetables.YourTimetablesComponent
import com.denchic45.studiversity.ui.yourworks.YourCourseWorksComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class RootNavigatorComponent(
    yourTimetablesComponent: (onStudyGroupOpen: (UUID) -> Unit, ComponentContext) -> YourTimetablesComponent,
    yourStudyGroupsComponent: (rootNavigator: RootNavigator, ComponentContext) -> YourStudyGroupsComponent,
    profileComponent: (RootNavigator, UUID, ComponentContext) -> ProfileComponent,
    courseComponent: (
        onFinish: () -> Unit,
        rootNavigator: RootNavigator,
        UUID,
        ComponentContext,
    ) -> CourseComponent,
    studyGroupComponent: (
        UUID,
        rootNavigator: RootNavigator,
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

                is RootConfig.Profile -> RootChild.Profile(
                    profileComponent(
                        navigation,
                        config.userId,
                        context
                    )
                )

                RootConfig.YourStudyGroups -> RootChild.YourStudyGroups(
                    yourStudyGroupsComponent(navigation, context)
                )

                is RootConfig.Course -> RootChild.Course(
                    courseComponent(
                        navigation::pop,
                        navigation,
                        config.courseId,
                        context
                    )
                )

                is RootConfig.StudyGroup -> RootChild.StudyGroup(
                    studyGroupComponent(
                        config.studyGroupId,
                        navigation,
                        context
                    )
                )

                RootConfig.YourCourseWorks -> TODO()
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
}

@Parcelize
sealed interface RootConfig : Parcelable {
    data object YourTimetables : RootConfig {
        private fun readResolve(): Any = YourTimetables
    }

    data object YourStudyGroups : RootConfig {
        private fun readResolve(): Any = YourStudyGroups
    }

    data object YourCourseWorks : RootConfig {
        private fun readResolve(): Any = YourCourseWorks
    }

    data class Profile(val userId: UUID) : RootConfig

    data class StudyGroup(val studyGroupId: UUID) : RootConfig

    data class Course(val courseId: UUID) : RootConfig

    data object AdminDashboard : RootConfig {
        private fun readResolve(): Any = AdminDashboard
    }

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

    class YourCourseWorks(
        override val component: YourCourseWorksComponent,
    ) : RootChild, ChildrenContainerChild

    class Profile(
        override val component: ProfileComponent,
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

typealias RootNavigator = StackNavigator<RootConfig>