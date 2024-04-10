package com.denchic45.studiversity.ui.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.*
import com.denchic45.studiversity.api.role.model.Role
import com.denchic45.studiversity.domain.MainInteractor
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.domain.usecase.FindAssignedUserRolesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindYourCoursesUseCase
import com.denchic45.studiversity.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.studiversity.ui.navigation.ChildrenContainerChild
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.root.AdminDashboardRootComponent
import com.denchic45.studiversity.ui.root.YourCourseWorksRootComponent
import com.denchic45.studiversity.ui.root.YourStudyGroupsRootComponent
import com.denchic45.studiversity.ui.root.YourTimetablesRootComponent
import com.denchic45.studiversity.ui.schedule.ScheduleComponent
import com.denchic45.studiversity.ui.settings.SettingsComponent
import com.denchic45.studiversity.util.UUIDSerializer
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class MainComponent(
    findYourCoursesUseCase: FindYourCoursesUseCase,
    findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
    yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesRootComponent,
    yourStudyGroupsRootComponent: (UUID, ComponentContext) -> YourStudyGroupsRootComponent,
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

    val stack = childStack(
        source = navigation,
        serializer = Config.serializer(),
        handleBackButton = true,
        initialConfiguration = Config.YourTimetables,
        childFactory = { config, context ->
            when (config) {
                is Config.YourTimetables -> Child.YourTimetables(
                    yourTimetablesRootComponent(context)
                )

                is Config.YourStudyGroups -> Child.YourStudyGroups(
                    yourStudyGroupsRootComponent(config.studyGroupId, context)
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
        serializer = SlotConfig.serializer(),
        handleBackButton = true,
        key = "MainChildSlot"
    ) { config, context ->
        when (config) {
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

    val yourStudyGroups = findYourStudyGroupsUseCase().stateInResource(componentScope)

    init {
        componentScope.launch { interactor.startListeners() }
    }

    fun onTimetableClick() {
        onDialogClose()
        navigation.bringToFront(Config.YourTimetables)
    }

    fun onStudyGroupsClick(studyGroupId: UUID) {
        onDialogClose()
        navigation.bringToFront(Config.YourStudyGroups(studyGroupId))

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
    ) {
        val moreItemsThanOne: Boolean = listOf(
            yourStudyGroups, yourWorks, adminDashboard
        ).count { it } > 1
    }

    @Serializable
    sealed interface Config {

        @Serializable
        data object YourTimetables : Config

        @Serializable
        data class YourStudyGroups(@Serializable(UUIDSerializer::class) val studyGroupId: UUID) : Config

        @Serializable
        data object YourCourseWorks : Config

        @Serializable
        data object AdminDashboard : Config
    }


    sealed interface Child {
        val component: RootStackChildrenContainer

        class YourTimetables(
            override val component: YourTimetablesRootComponent,
        ) : Child, ChildrenContainerChild

        class YourStudyGroups(
            override val component: YourStudyGroupsRootComponent,
        ) : Child, ChildrenContainerChild

        class YourWorks(
            override val component: YourCourseWorksRootComponent,
        ) : Child

        class AdminDashboard(
            override val component: AdminDashboardRootComponent,
        ) : Child
    }

    @Serializable
    sealed interface SlotConfig {
        @Serializable
        data object Schedule : SlotConfig

        @Serializable
        data object Settings : SlotConfig

    }

    sealed interface SlotChild {
        class Schedule(val component: ScheduleComponent) : SlotChild

        class Settings(val component: SettingsComponent) : SlotChild
    }

}

typealias AppNavigation = StackNavigation<MainComponent.Config>