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
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.MainInteractor
import com.denchic45.kts.domain.ifSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourCoursesUseCase
import com.denchic45.kts.ui.admindashboard.AdminDashboardComponent
import com.denchic45.kts.ui.course.CourseComponent
import com.denchic45.kts.ui.navigation.OverlayChild
import com.denchic45.kts.ui.navigation.OverlayConfig
import com.denchic45.kts.ui.navigation.RootStackChildrenContainer
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.root.YourStudyGroupsRootStackChildrenContainer
import com.denchic45.kts.ui.root.YourTimetablesRootComponent
import com.denchic45.kts.ui.settings.SettingsComponent
import com.denchic45.kts.ui.studygroup.StudyGroupComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class MainComponent(
    yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesRootComponent,
    yourStudyGroupsRootComponent: (ComponentContext) -> YourStudyGroupsRootStackChildrenContainer,
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
        onStudyGroupOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> CourseComponent,
    adminDashboardComponent: (StackNavigation<RootConfig>, ComponentContext) -> AdminDashboardComponent,
    settingsComponent: (ComponentContext) -> SettingsComponent,
    interactor: MainInteractor,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val navigation = StackNavigation<RootConfig>()

    val stack: Value<ChildStack<RootConfig, RootChild>> = childStack(
        source = navigation,
        handleBackButton = true,
        initialConfiguration = RootConfig.YourTimetables,
        childFactory = { config, context ->
            when (config) {
                is RootConfig.YourTimetables -> RootChild.YourTimetables(
                    yourTimetablesRootComponent(context)
                )

                is RootConfig.YourStudyGroups -> RootChild.YourStudyGroups(
                    yourStudyGroupsRootComponent(context)
                )

                RootConfig.Works -> TODO()
                is RootConfig.StudyGroup -> RootChild.StudyGroup(
                    studyGroupComponent(
                        { navigation.bringToFront(RootConfig.Course(it)) },
                        { navigation.bringToFront(RootConfig.StudyGroup(it)) },
                        config.studyGroupId,
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

                is RootConfig.YourCourse -> RootChild.YourCourse(
                    courseComponent(
                        { navigation.bringToFront(RootConfig.StudyGroup(it)) },
                        config.courseId,
                        context
                    )
                )

                RootConfig.AdminDashboard -> RootChild.AdminDashboard(
                    adminDashboardComponent(navigation, context)
                )
            }
        })

    private val overlayNavigation: OverlayNavigation<OverlayConfig> = OverlayNavigation()

    val childOverlay = childOverlay(
        source = overlayNavigation,
        handleBackButton = true
    ) { config, context ->
        when (config) {
            is OverlayConfig.Confirm -> OverlayChild.Confirm(config)
            OverlayConfig.YourProfile -> OverlayChild.YourProfile(
                profileComponent(
                    {
                        navigation.bringToFront(RootConfig.StudyGroup(it))
                        overlayNavigation.dismiss()
                    },
                    userInfo.value.ifSuccess { it.id } ?: error("Id required"),
                    context
                )
            )

            is OverlayConfig.Settings -> OverlayChild.Settings(settingsComponent(context))
        }
    }

    // TODO listen root role of current user to show works screen
    val availableScreens = combine(
        interactor.observeHasGroup(),
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
        navigation.bringToFront(RootConfig.YourTimetables)
    }

    fun onGroupClick() {
        navigation.bringToFront(RootConfig.YourStudyGroups)
    }

    fun onOverlayDismiss() {
        overlayNavigation.dismiss()
    }

    fun onProfileClick() {
        overlayNavigation.activate(OverlayConfig.YourProfile)
    }

    fun onCourseClick(courseId: UUID) {
        navigation.bringToFront(RootConfig.Course(courseId))
    }

    fun onAdminDashboardClick() {
        navigation.bringToFront(RootConfig.AdminDashboard)
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
    sealed interface RootConfig : Parcelable {

        object YourTimetables : RootConfig

        object YourStudyGroups : RootConfig

        object Works : RootConfig

        data class StudyGroup(val studyGroupId: UUID) : RootConfig

        data class Course(val courseId: UUID) : RootConfig

        data class YourCourse(val courseId: UUID) : RootConfig

        object AdminDashboard : RootConfig
    }

    sealed interface PrimaryChild {
        val component: RootStackChildrenContainer<*, *>
    }

    sealed interface ExtraChild

    sealed interface RootChild {

        class YourTimetables(
            override val component: YourTimetablesRootComponent,
        ) : RootChild, PrimaryChild

        class YourStudyGroups(
            override val component: YourStudyGroupsRootStackChildrenContainer,
        ) : RootChild, PrimaryChild

        class Works(
            val component: RootStackChildrenContainer<*, *>,
        ) : RootChild, ExtraChild


        class StudyGroup(
            val component: StudyGroupComponent,
        ) : RootChild, ExtraChild

        class Course(
            val component: CourseComponent,
        ) : RootChild, ExtraChild

        class YourCourse(
            val component: CourseComponent,
        ) : RootChild, ExtraChild

        class AdminDashboard(
            val component: AdminDashboardComponent,
        ) : RootChild, ExtraChild
    }
}
