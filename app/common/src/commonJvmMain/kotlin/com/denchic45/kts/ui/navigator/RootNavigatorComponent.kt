package com.denchic45.kts.ui.navigator

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.admindashboard.AdminDashboardComponent
import com.denchic45.kts.ui.course.CourseComponent
import com.denchic45.kts.ui.navigation.ChildrenContainerChild
import com.denchic45.kts.ui.navigation.RootStackChildrenContainer
import com.denchic45.kts.ui.studygroup.StudyGroupComponent
import com.denchic45.kts.ui.yourstudygroups.YourStudyGroupsComponent
import com.denchic45.kts.ui.yourtimetables.YourTimetablesComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class RootNavigatorComponent(
    yourTimetablesComponent: (ComponentContext) -> YourTimetablesComponent,
    yourStudyGroupsComponent: (
        onCourseOpen: (UUID) -> Unit,

        onStudyGroupOpen: (UUID) -> Unit, ComponentContext,
    ) -> YourStudyGroupsComponent,
    courseComponent: (
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
                    yourTimetablesComponent(context)
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
                    adminDashboardComponent(
                        navigation,
                        context
                    )
                )
            }
        }
    )
}

@Parcelize
sealed interface RootConfig : Parcelable {
    object YourTimetables : RootConfig

    object YourStudyGroups : RootConfig

    object Works : RootConfig

    data class StudyGroup(val studyGroupId: UUID) : RootConfig

    data class Course(val courseId: UUID) : RootConfig

    object AdminDashboard : RootConfig
}

sealed interface RootChild : ChildrenContainerChild {
    class YourTimetables(
        override val component: YourTimetablesComponent,
    ) : RootChild

    class YourStudyGroups(
        override val component: YourStudyGroupsComponent,
    ) : RootChild

    class Works(
        override val component: RootStackChildrenContainer,
    ) : RootChild

    class StudyGroup(
        override val component: StudyGroupComponent,
    ) : RootChild

    class Course(
        override val component: CourseComponent,
    ) : RootChild


    class AdminDashboard(
        override val component: AdminDashboardComponent,
    ) : RootChild
}