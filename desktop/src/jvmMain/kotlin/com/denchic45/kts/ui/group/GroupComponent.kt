package com.denchic45.kts.ui.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.ui.group.courses.GroupCoursesComponent
import com.denchic45.kts.ui.group.members.GroupMembersComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

@Inject
class GroupComponent(
    lazyGroupMembersComponent: (groupId: String) -> GroupMembersComponent,
    lazyGroupCourseComponent: (groupId: String) -> GroupCoursesComponent,
    componentContext: ComponentContext,
    private val groupId: String,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val groupMembersComponent by lazy { lazyGroupMembersComponent(groupId) }
    private val groupCourseComponent by lazy { lazyGroupCourseComponent(groupId) }

    val stack = childStack(source = navigation,
        initialConfiguration = Config.Members,
        childFactory = { config: Config, _ ->
            when (config) {
                is Config.Members -> Child.Members(groupMembersComponent)
                is Config.Courses -> Child.Courses(groupCourseComponent)
            }
        })

    val tabs: StateFlow<List<TabItem>> = MutableStateFlow(listOf(TabItem.Members, TabItem.Courses))
    val selectedTab = MutableStateFlow(0)

    fun onTabClick(index: Int) {
        selectedTab.value = index
        navigation.bringToFront(when (tabs.value[index]) {
            is TabItem.Members -> Config.Members
            is TabItem.Courses -> Config.Courses
            TabItem.DutyRoster -> TODO()
            TabItem.Timetable -> TODO()
        })
    }

    sealed class Config : Parcelable {
        object Members : Config()
        object Courses : Config()
    }

    sealed class Child {
        class Members(val membersComponent: GroupMembersComponent) : Child()
        class Courses(val coursesComponent: GroupCoursesComponent) : Child()
    }

    sealed class TabItem(val title: String) {
        object Members : TabItem("Участники")
        object Courses : TabItem("Курсы")
        object DutyRoster : TabItem("Дежурства")
        object Timetable : TabItem("Дежурства")
    }
}