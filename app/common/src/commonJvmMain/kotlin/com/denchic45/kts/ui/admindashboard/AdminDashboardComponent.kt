package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.navigation.StackChildrenContainer
import com.denchic45.kts.ui.navigator.RootConfig
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.timetableLoader.TimetableLoaderComponent
import com.denchic45.kts.ui.timetablefinder.TimetableFinderComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AdminDashboardComponent(
    timetableFinderComponent: (ComponentContext) -> TimetableFinderComponent,
    timetableLoaderComponent: (ComponentContext) -> TimetableLoaderComponent,
    coursesAdminComponent: (
        rootNavigation: StackNavigation<RootConfig>,
        ComponentContext,
    ) -> CoursesAdminComponent,
    usersAdminComponent: (
        rootNavigation: StackNavigation<RootConfig>,
        ComponentContext
    ) -> UsersAdminComponent,
    studyGroupsAdminComponent: (
        rootNavigation: StackNavigation<RootConfig>,
        ComponentContext
    ) -> StudyGroupsAdminComponent,
    profileComponent: (onStudyGroupOpen: (UUID) -> Unit, UUID, ComponentContext) -> ProfileComponent,
    @Assisted
    rootNavigation: StackNavigation<RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    StackChildrenContainer<AdminDashboardComponent.Config, AdminDashboardComponent.Child> {

    override val navigation = StackNavigation<Config>()

    override val childStack = childStack(
        source = navigation,
        initialConfiguration = Config.None,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                Config.None -> Child.None

                Config.TimetableFinder -> Child.TimetableFinder(timetableFinderComponent(context))
                Config.TimetableLoader -> Child.TimetableLoader(timetableLoaderComponent(context))

                Config.Courses -> Child.Courses(coursesAdminComponent(rootNavigation, context))

                Config.Users -> Child.Users(usersAdminComponent(rootNavigation, context))

                Config.StudyGroups -> Child.StudyGroups(
                    studyGroupsAdminComponent(rootNavigation, context)
                )

                Config.Subjects -> Child.Subjects(
                    TODO()
                )

                Config.Specialties -> Child.Specialties(
                    TODO()
                )

            }
        }
    )

//    private val sidebarNavigation = OverlayNavigation<SidebarConfig>()

//    val childSidebar = childOverlay(
//        source = sidebarNavigation,
//        handleBackButton = true,
//        childFactory = { config, context ->
//            when (config) {
//                is SidebarConfig.Profile -> SidebarChild.Profile(profileComponent({
//                    rootNavigation.bringToFront(
//                        RootConfig.StudyGroup(it)
//                    )
//                }, config.userId, context))
//            }
//        }
//    )

    fun onTimetableFinderClick() {
        navigation.bringToFront(Config.TimetableFinder)
    }

    fun onTimetableLoaderClick() {
        navigation.bringToFront(Config.TimetableLoader)
    }

    fun onCoursesClick() {
        navigation.bringToFront(Config.Courses)
    }

    fun onUsersClick() {
        navigation.bringToFront(Config.Users)
    }

    fun onStudyGroupsClick() {
        navigation.bringToFront(Config.StudyGroups)
    }

    fun onSubjectsClick() {
        navigation.bringToFront(Config.Subjects)
    }

    fun onSpecialtiesClick() {
        navigation.bringToFront(Config.Specialties)
    }


    @Parcelize
    sealed interface Config : Parcelable {
        object None : Config
        object TimetableFinder : Config
        object TimetableLoader : Config
        object Courses : Config
        object Users : Config
        object StudyGroups : Config
        object Subjects : Config
        object Specialties : Config

    }

    sealed interface Child {
        object None : Child
        class TimetableFinder(val component: TimetableFinderComponent) : Child
        class TimetableLoader(val component: TimetableLoaderComponent) : Child
        class Courses(val component: CoursesAdminComponent) : Child
        class Users(val component: UsersAdminComponent) : Child
        class StudyGroups(val component: StudyGroupsAdminComponent) : Child
        class Subjects(val component: ComponentContext) : Child
        class Specialties(val component: ComponentContext) : Child

    }

    @Parcelize
    sealed interface SidebarConfig : Parcelable {
        data class Profile(val userId: UUID) : SidebarConfig
    }

    sealed interface SidebarChild {
        class Profile(val component: ProfileComponent) : SidebarChild
    }

//    @Parcelize
//    sealed interface StackOverlayConfig : Parcelable {
//        data class Course(val courseId: UUID) : StackOverlayConfig
//        data class StudyGroup(val studyGroupId: UUID) : StackOverlayConfig
//    }
//
//    sealed interface StackOverlayChild {
//        class Course(val component: CourseComponent) : StackOverlayChild
//        class StudyGroup(val component: StudyGroupComponent) : StackOverlayChild
//    }

}