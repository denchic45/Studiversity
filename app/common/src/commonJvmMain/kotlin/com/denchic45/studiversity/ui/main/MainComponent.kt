package com.denchic45.studiversity.ui.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.*
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.MainInteractor
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.domain.usecase.FindAssignedUserRolesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindYourCoursesUseCase
import com.denchic45.studiversity.ui.navigation.ChildrenContainerChild
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigation.SlotChild
import com.denchic45.studiversity.ui.navigation.SlotConfig
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.root.AdminDashboardRootComponent
import com.denchic45.studiversity.ui.root.YourCourseWorksRootComponent
import com.denchic45.studiversity.ui.root.YourStudyGroupsRootComponent
import com.denchic45.studiversity.ui.root.YourTimetablesRootComponent
import com.denchic45.studiversity.ui.schedule.ScheduleComponent
import com.denchic45.studiversity.ui.settings.SettingsComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class MainComponent(
    findYourCoursesUseCase: FindYourCoursesUseCase,
    findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
    yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesRootComponent,
    yourStudyGroupsRootComponent: (ComponentContext) -> YourStudyGroupsRootComponent,
    scheduleComponent: (ComponentContext) -> ScheduleComponent,
    adminDashboardRootComponent: (ComponentContext) -> AdminDashboardRootComponent,
    yourCourseWorksRootComponent: (ComponentContext) -> YourCourseWorksRootComponent,
    settingsComponent: (ComponentContext) -> SettingsComponent,
    interactor: MainInteractor,
    private val navigation: AppNavigation,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

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

                Config.YourCourseWorks -> Child.YourWorks(yourCourseWorksRootComponent(context))

                Config.AdminDashboard -> Child.AdminDashboard(
                    adminDashboardRootComponent(context)
                )
            }
        })

    private val slotNavigation: SlotNavigation<SlotConfig> = SlotNavigation()

    val childSlot = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        key = "MainChildSlot"
    ) { config, context ->
        when (config) {
            is SlotConfig.Confirm -> SlotChild.Confirm(config)

            is SlotConfig.Settings -> SlotChild.Settings(
                settingsComponent(context)
            )

            SlotConfig.Schedule -> SlotChild.Schedule(
                scheduleComponent(componentContext)
            )
        }
    }

    private val assignedRolesInOrganization = findAssignedUserRolesInScopeUseCase()
        .stateInResource(componentScope)

    private val hasStudyGroupsFlow = interactor.observeHasStudyGroups().onEach { hasStudyGroups ->
        /* remove StudyGroup config if exists */
//        if (!hasStudyGroups)
//            withContext(Dispatchers.Main) {
//                navigation.navigate { stack ->
//                    stack.firstOrNull { it is Config.StudyGroup }?.let { stack - it } ?: stack
//                }
//            }
    }

    val availableScreens = combine(
        hasStudyGroupsFlow,
        assignedRolesInOrganization.map {
            it.takeValueIfSuccess()?.roles?.contains(Role.TeacherPerson) ?: false
        }, // is teacher
        assignedRolesInOrganization.map {
            it.takeValueIfSuccess()?.roles?.contains(Role.StudentPerson) ?: false
        }, // is student
        assignedRolesInOrganization.map {
            it.takeValueIfSuccess()?.roles?.contains(Role.Moderator) ?: false
        },// is moderator
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
    }

    fun onTimetableClick() {
        onDialogClose()
        navigation.bringToFront(Config.YourTimetables)
    }

    fun onStudyGroupsClick() {
        onDialogClose()
        navigation.bringToFront(Config.YourStudyGroups)
    }

    fun onDialogClose() {
        slotNavigation.dismiss()
    }

    private val currentStackNavigation: StackNavigation<RootConfig>
        get() = stack.active.instance.component.navigation

    fun onProfileClick() {
        userInfo.value.onSuccess {
            currentStackNavigation.bringToFront(RootConfig.Profile(it.id))
        }

    }

    fun onScheduleClick() {
        slotNavigation.activate(SlotConfig.Schedule)
    }

    fun onWorksClick() {
        onDialogClose()
        navigation.bringToFront(Config.YourCourseWorks)
    }

    fun onCourseClick(courseId: UUID) {
        onDialogClose()
        currentStackNavigation.bringToFront(RootConfig.Course(courseId))
    }

    fun onAdminDashboardClick() {
        navigation.bringToFront(Config.AdminDashboard)
    }

    fun onSettingsClick() {
        slotNavigation.activate(SlotConfig.Settings)
    }

    fun onBackClick() {
        navigation.pop()
    }

    data class AvailableScreens(
        val yourStudyGroups: Boolean = false,
        val yourWorks: Boolean = false,
        val adminDashboard: Boolean = false,
    )

    @Parcelize
    sealed interface Config : Parcelable {

        data object YourTimetables : Config {
            private fun readResolve(): Any = YourTimetables
        }

        data object YourStudyGroups : Config {
            private fun readResolve(): Any = YourStudyGroups
        }

        data object YourCourseWorks : Config {
            private fun readResolve(): Any = YourCourseWorks
        }

        data object AdminDashboard : Config {
            private fun readResolve(): Any = AdminDashboard
        }
    }

    sealed interface PrimaryChild {
        val component: RootStackChildrenContainer
    }

    sealed interface ExtraChild

    sealed interface Child : PrimaryChild {

        class YourTimetables(
            override val component: YourTimetablesRootComponent,
        ) : Child, ChildrenContainerChild, PrimaryChild

        class YourStudyGroups(
            override val component: YourStudyGroupsRootComponent,
        ) : Child, ChildrenContainerChild, PrimaryChild

        class YourWorks(
            override val component: YourCourseWorksRootComponent,
        ) : Child, ExtraChild

        class AdminDashboard(
            override val component: AdminDashboardRootComponent,
        ) : Child, ExtraChild
    }
}

typealias AppNavigation = StackNavigation<MainComponent.Config>