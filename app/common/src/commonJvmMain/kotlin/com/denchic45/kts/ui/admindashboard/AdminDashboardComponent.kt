package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.ui.MainComponent
import com.denchic45.kts.ui.course.CourseComponent
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.profile.ProfileComponent
import com.denchic45.kts.ui.studygroup.StudyGroupComponent
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AdminDashboardComponent(
    coursesAdminComponent: (
        onSelect: (CourseResponse) -> Unit,
        ComponentContext
    ) -> CoursesAdminComponent,
    usersAdminComponent: (onSelect: (UserItem) -> Unit, ComponentContext) -> UsersAdminComponent,

    profileComponent: (onStudyGroupOpen: (UUID) -> Unit, UUID, ComponentContext) -> ProfileComponent,
    @Assisted
    rootNavigation: StackNavigation<MainComponent.RootConfig>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack = childStack(
        source = navigation,
        initialConfiguration = Config.None,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                Config.None -> Child.None
                Config.Courses -> Child.Courses(coursesAdminComponent({
                    rootNavigation.bringToFront(MainComponent.RootConfig.Course(it.id))
                }, context))

                Config.Users -> Child.Users(usersAdminComponent({
                    sidebarNavigation.activate(
                        SidebarConfig.Profile(it.id)
                    )
                }, context))
            }
        }
    )

    private val sidebarNavigation = OverlayNavigation<SidebarConfig>()

    val childSidebar = childOverlay(
        source = sidebarNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is SidebarConfig.Profile -> SidebarChild.Profile(profileComponent({
                    rootNavigation.bringToFront(
                        MainComponent.RootConfig.StudyGroup(it)
                    )
                }, config.userId, context))
            }
        }
    )

    fun onCoursesClick() {
        navigation.bringToFront(Config.Courses)
    }

    fun onUsersClick() {
        navigation.bringToFront(Config.Users)
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object None : Config
        object Courses : Config
        object Users : Config

    }

    sealed interface Child {
        object None : Child
        class Courses(val component: CoursesAdminComponent) : Child
        class Users(val component: UsersAdminComponent) : Child

    }

    @Parcelize
    sealed interface SidebarConfig : Parcelable {
        data class Profile(val userId: UUID) : SidebarConfig
    }

    sealed interface SidebarChild {
        class Profile(val component: ProfileComponent) : SidebarChild
    }

    @Parcelize
    sealed interface StackOverlayConfig : Parcelable {
        data class Course(val courseId: UUID) : StackOverlayConfig
        data class StudyGroup(val studyGroupId: UUID) : StackOverlayConfig
    }

    sealed interface StackOverlayChild {
        class Course(val component: CourseComponent) : StackOverlayChild
        class StudyGroup(val component: StudyGroupComponent) : StackOverlayChild
    }
}