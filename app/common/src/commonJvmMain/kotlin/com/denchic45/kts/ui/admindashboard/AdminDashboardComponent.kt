package com.denchic45.kts.ui.admindashboard

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.stuiversity.api.course.model.CourseResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class AdminDashboardComponent(
    coursesAdminComponent: (onSelect: (CourseResponse) -> Unit, ComponentContext) -> CoursesAdminComponent,
    usersAdminComponent: (ComponentContext) -> UsersAdminComponent,
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
                Config.Courses -> Child.Courses(coursesAdminComponent({ TODO() }, context))
                Config.Users -> Child.Users(usersAdminComponent(context))
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
}