package com.denchic45.kts.ui

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.MainInteractor
import com.denchic45.kts.domain.ifSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourCoursesUseCase
import com.denchic45.kts.ui.admindashboard.AdminDashboardRootComponent
import com.denchic45.kts.ui.course.CourseComponent
import com.denchic45.kts.ui.navigation.ChildrenContainerChild
import com.denchic45.kts.ui.navigation.OverlayChild
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.navigation.RootStackChildrenContainer
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.root.YourStudyGroupsRootComponent
import com.denchic45.kts.ui.root.YourTimetablesRootComponent
import com.denchic45.kts.ui.settings.SettingsComponent
import com.denchic45.kts.ui.studygroup.StudyGroupComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class MainComponent(
    yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesRootComponent,
    yourStudyGroupsRootComponent: (ComponentContext) -> YourStudyGroupsRootComponent,
    findYourCoursesUseCase: FindYourCoursesUseCase,
    profileComponent: (
        onStudyGroupOpen: (UUID) -> Unit,
        UUID, ComponentContext,
    ) -> ProfileComponent,
    studyGroupComponent: (
        onCourseOpen: (UUID) -> Unit,
        onStudyGroupOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupComponent,
    courseComponent: (
        onFinish: () -> Unit,
        onStudyGroupOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> CourseComponent,
    adminDashboardRootComponent: (ComponentContext) -> AdminDashboardRootComponent,
    settingsComponent: (ComponentContext) -> SettingsComponent,
    interactor: MainInteractor,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val navigation = StackNavigation<Config>()

    val stack: Value<ChildStack<Config, Child>> = childStack(
        source = navigation,
        handleBackButton = true,
        initialConfiguration = Config.YourTimetables,
        childFactory = { config, context ->
            when (config) {
                is Config.YourTimetables -> Child.YourTimetables(
                    yourTimetablesRootComponent(context)
                )

                is Config.YourStudyGroups -> Child.YourStudyGroups(
                    yourStudyGroupsRootComponent(context)
                )

                Config.Works -> TODO()
                is Config.StudyGroup -> Child.StudyGroup(
                    studyGroupComponent(
                        { navigation.bringToFront(Config.Course(it)) },
                        { navigation.bringToFront(Config.StudyGroup(it)) },
                        config.studyGroupId,
                        context
                    )
                )

                is Config.Course -> Child.Course(
                    courseComponent(
                        navigation::pop,
                        { navigation.bringToFront(Config.StudyGroup(it)) },
                        config.courseId,
                        context
                    )
                )

                is Config.YourCourse -> Child.YourCourse(
                    courseComponent(
                        navigation::pop,
                        { navigation.bringToFront(Config.StudyGroup(it)) },
                        config.courseId,
                        context
                    )
                )

                Config.AdminDashboard -> Child.AdminDashboard(
                    adminDashboardRootComponent(context)
                )
            }
        })

    private val overlayNavigation: OverlayNavigation<OverlayConfig> = OverlayNavigation()

    val childOverlay = childOverlay(
        source = overlayNavigation,
        handleBackButton = true,
        key = "MainChildOverlay"
    ) { config, context ->
        when (config) {
            is OverlayConfig.Confirm -> OverlayChild.Confirm(config)
            OverlayConfig.YourProfile -> OverlayChild.YourProfile(
                profileComponent(
                    {
                        navigation.bringToFront(Config.StudyGroup(it))
                        overlayNavigation.dismiss()
                    },
                    userInfo.value.ifSuccess { it.id } ?: error("Id required"),
                    context
                )
            )

            is OverlayConfig.Settings -> OverlayChild.Settings(settingsComponent(context))
        }
    }

    private val hasStudyGroupsFlow = interactor.observeHasStudyGroups().onEach { hasStudyGroups ->
        if (!hasStudyGroups)
        /* remove StudyGroup config if exists */
            navigation.navigate { stack ->
                stack.firstOrNull { it is Config.StudyGroup }?.let { stack - it } ?: stack
            }
    }

    // TODO listen root role of current user to show works screen
    val availableScreens = combine(
        hasStudyGroupsFlow,
        flowOf(true), // is teacher
        flowOf(true), // is student
        flowOf(true) // is moderator
    ) { hasStudyGroups, isTeacher, isStudent, isModerator ->
        AvailableScreens(
            yourStudyGroups = hasStudyGroups,
            yourWorks = isStudent,
            adminDashboard = isModerator
        )
    }.stateIn(componentScope, SharingStarted.Lazily, AvailableScreens())

    val userInfo = interactor.observeThisUser().filterNotNull().stateInResource(componentScope)

    val yourCourses = flow { emit(findYourCoursesUseCase()) }
        .stateInResource(componentScope)

    init {
        componentScope.launch { interactor.startListeners() }
//        coroutineScope.launch { mainInteractor.observeHasGroup() }
    }

    fun onTimetableClick() {
        onOverlayDismiss()
        navigation.bringToFront(Config.YourTimetables)
    }

    fun onStudyGroupsClick() {
        onOverlayDismiss()
        navigation.bringToFront(Config.YourStudyGroups)
    }

    fun onOverlayDismiss() {
        overlayNavigation.dismiss()
    }

    fun onProfileClick() {
        overlayNavigation.activate(OverlayConfig.YourProfile)
    }

    fun onCourseClick(courseId: UUID) {
        onOverlayDismiss()
        navigation.bringToFront(Config.Course(courseId))
    }

    fun onAdminDashboardClick() {
        navigation.bringToFront(Config.AdminDashboard)
    }

    fun onSettingsClick() {
        overlayNavigation.activate(OverlayConfig.Settings)
    }

    data class AvailableScreens(
        val yourStudyGroups: Boolean = false,
        val yourWorks: Boolean = false,
        val adminDashboard: Boolean = false,
    )

    @Parcelize
    sealed interface Config : Parcelable {

        object YourTimetables : Config

        object YourStudyGroups : Config

        object Works : Config

        data class StudyGroup(val studyGroupId: UUID) : Config

        data class Course(val courseId: UUID) : Config

        data class YourCourse(val courseId: UUID) : Config

        object AdminDashboard : Config
    }

    sealed interface PrimaryChild {
        val component: RootStackChildrenContainer
    }

    sealed interface ExtraChild

    sealed interface Child {

        class YourTimetables(
            override val component: YourTimetablesRootComponent,
        ) : Child, ChildrenContainerChild, PrimaryChild

        class YourStudyGroups(
            override val component: YourStudyGroupsRootComponent,
        ) : Child, ChildrenContainerChild, PrimaryChild

        class Works(
            override val component: RootStackChildrenContainer,
        ) : Child, ChildrenContainerChild, ExtraChild


        class StudyGroup(
            override val component: StudyGroupComponent,
        ) : Child, ChildrenContainerChild, ExtraChild

        class Course(
            val component: CourseComponent,
        ) : Child, ExtraChild

        class YourCourse(
            override val component: CourseComponent,
        ) : Child, ChildrenContainerChild, ExtraChild

        class AdminDashboard(
            val component: AdminDashboardRootComponent,
        ) : Child, ExtraChild
    }
}
