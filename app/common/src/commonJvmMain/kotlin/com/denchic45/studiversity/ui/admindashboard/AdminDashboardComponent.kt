package com.denchic45.studiversity.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.navigation.StackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootConfig
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.timetableloader.TimetableLoaderComponent
import com.denchic45.studiversity.ui.timetablesearch.TimetableSearchComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AdminDashboardComponent(
    timetableSearchComponent: (ComponentContext) -> TimetableSearchComponent,
    timetableLoaderComponent: (onClose: () -> Unit, ComponentContext) -> TimetableLoaderComponent,
    coursesAdminComponent: (
        rootNavigation: StackNavigation<RootConfig>,
        ComponentContext,
    ) -> CoursesAdminComponent,
    usersAdminComponent: (
        rootNavigation: StackNavigation<RootConfig>,
        ComponentContext,
    ) -> UsersAdminComponent,
    studyGroupsAdminComponent: (
        rootNavigation: StackNavigation<RootConfig>,
        ComponentContext,
    ) -> StudyGroupsAdminComponent,
    subjectsAdminComponent: (
        rootNavigation: StackNavigation<RootConfig>,
        ComponentContext,
    ) -> SubjectsAdminComponent,
    specialtiesAdminComponent: (
        rootNavigation: StackNavigation<RootConfig>,
        ComponentContext,
    ) -> SpecialtiesAdminComponent,
    roomsAdminComponent: (
        ComponentContext,
    ) -> RoomsAdminComponent,
    profileComponent: (RootNavigator, UUID, ComponentContext) -> ProfileComponent,
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

                Config.TimetableFinder -> Child.TimetableFinder(timetableSearchComponent(context))

                Config.TimetableLoader -> Child.TimetableLoader(
                    timetableLoaderComponent(navigation::pop, context)
                )

                Config.Courses -> Child.Courses(coursesAdminComponent(rootNavigation, context))

                Config.Users -> Child.Users(usersAdminComponent(rootNavigation, context))

                Config.StudyGroups -> Child.StudyGroups(
                    studyGroupsAdminComponent(rootNavigation, context)
                )

                Config.Subjects -> Child.Subjects(
                    subjectsAdminComponent(rootNavigation, context)
                )

                Config.Specialties -> Child.Specialties(
                    specialtiesAdminComponent(rootNavigation, context)
                )

                Config.Rooms -> Child.Rooms(roomsAdminComponent(context))
            }
        }
    )

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

    fun onRoomsClick() {
        navigation.bringToFront(Config.Rooms)
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
        object Rooms : Config

    }

    sealed interface Child {
        object None : Child
        class TimetableFinder(val component: TimetableSearchComponent) : Child
        class TimetableLoader(val component: TimetableLoaderComponent) : Child
        class Courses(val component: CoursesAdminComponent) : Child
        class Users(val component: UsersAdminComponent) : Child
        class StudyGroups(val component: StudyGroupsAdminComponent) : Child
        class Subjects(val component: SubjectsAdminComponent) : Child
        class Specialties(val component: SpecialtiesAdminComponent) : Child
        class Rooms(val component: RoomsAdminComponent) : Child
    }

    @Parcelize
    sealed interface SidebarConfig : Parcelable {
        data class Profile(val userId: UUID) : SidebarConfig
    }

    sealed interface SidebarChild {
        class Profile(val component: ProfileComponent) : SidebarChild
    }
}