package com.denchic45.studiversity.ui.main

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.MainInteractor
import com.denchic45.studiversity.domain.resource.ifSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.domain.usecase.FindAssignedUserRolesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindYourCoursesUseCase
import com.denchic45.studiversity.ui.admindashboard.AdminDashboardRootComponent
import com.denchic45.studiversity.ui.course.CourseComponent
import com.denchic45.studiversity.ui.coursework.CourseWorkComponent
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.studiversity.ui.navigation.ChildrenContainerChild
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigation.SlotChild
import com.denchic45.studiversity.ui.navigation.SlotConfig
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.root.YourStudyGroupsRootComponent
import com.denchic45.studiversity.ui.root.YourTimetablesRootComponent
import com.denchic45.studiversity.ui.schedule.ScheduleComponent
import com.denchic45.studiversity.ui.settings.SettingsComponent
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.ui.yourworks.YourWorksComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.role.model.Role
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class MainComponent(
    findYourCoursesUseCase: FindYourCoursesUseCase,
    findAssignedUserRolesInScopeUseCase: FindAssignedUserRolesInScopeUseCase,
    yourTimetablesRootComponent: (ComponentContext) -> YourTimetablesRootComponent,
    yourStudyGroupsRootComponent: (ComponentContext) -> YourStudyGroupsRootComponent,
    profileComponent: (onStudyGroupOpen: (UUID) -> Unit, UUID, ComponentContext, ) -> ProfileComponent,
    scheduleComponent: (ComponentContext) -> ScheduleComponent,
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
    courseWorkComponent: (
        onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
        onFinish: () -> Unit,
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
    courseWorkEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID,
        workId: UUID?,
        topicId: UUID?,
        ComponentContext,
    ) -> CourseWorkEditorComponent,
    yourWorksComponent: (ComponentContext) -> YourWorksComponent,
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

                Config.YourWorks -> Child.YourWorks(yourWorksComponent(context))
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

                is Config.CourseWork -> Child.CourseWork(
                    courseWorkComponent({ courseId, workId ->
                        navigation.push(
                            Config.CourseWorkEditor(
                                courseId,
                                workId
                            )
                        )
                    }, {}, config.courseId, config.workId, context)
                )

                is Config.CourseWorkEditor -> Child.CourseWorkEditor(
                    courseWorkEditorComponent({}, config.courseId, config.workId, null, context)
                )

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

            SlotConfig.YourProfile -> SlotChild.YourProfile(
                profileComponent(
                    {
                        navigation.bringToFront(Config.StudyGroup(it))
                        slotNavigation.dismiss()
                    },
                    userInfo.value.ifSuccess { it.id } ?: error("Id required"),
                    context
                )
            )

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
        if (!hasStudyGroups)
        /* remove StudyGroup config if exists */
            withContext(Dispatchers.Main) {
                navigation.navigate { stack ->
                    stack.firstOrNull { it is Config.StudyGroup }?.let { stack - it } ?: stack
                }
            }
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
//        coroutineScope.launch { mainInteractor.observeHasGroup() }
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

    fun onProfileClick() {
        slotNavigation.activate(SlotConfig.YourProfile)
    }

    fun onScheduleClick() {
        slotNavigation.activate(SlotConfig.Schedule)
    }

    fun onWorksClick() {
        onDialogClose()
        navigation.bringToFront(Config.YourWorks)
    }

    fun onCourseClick(courseId: UUID) {
        onDialogClose()
        navigation.bringToFront(Config.Course(courseId))
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

        object YourTimetables : Config

        object YourStudyGroups : Config

        object YourWorks : Config

        data class StudyGroup(val studyGroupId: UUID) : Config

        data class Course(val courseId: UUID) : Config

        data class YourCourse(val courseId: UUID) : Config

        data class CourseWork(val courseId: UUID, val workId: UUID) : Config

        data class CourseWorkEditor(val courseId: UUID, val workId: UUID?) : Config

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

        class YourWorks(
            val component: YourWorksComponent,
        ) : Child, ExtraChild

        class StudyGroup(
            override val component: StudyGroupComponent,
        ) : Child, ChildrenContainerChild, ExtraChild

        class Course(
            val component: CourseComponent,
        ) : Child, ExtraChild

        class YourCourse(
            override val component: CourseComponent,
        ) : Child, ChildrenContainerChild, ExtraChild

        class CourseWork(
            val component: CourseWorkComponent
        ) : Child, ExtraChild

        class CourseWorkEditor(
            val component: CourseWorkEditorComponent
        ) : Child, ExtraChild

        class AdminDashboard(
            val component: AdminDashboardRootComponent,
        ) : Child, ExtraChild
    }
}

typealias AppNavigation = StackNavigation<MainComponent.Config>