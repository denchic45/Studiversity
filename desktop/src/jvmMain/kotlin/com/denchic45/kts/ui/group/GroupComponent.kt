package com.denchic45.kts.ui.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.ui.group.courses.GroupCoursesComponent
import com.denchic45.kts.ui.group.members.GroupMembersComponent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
class GroupComponent(
    lazyMembersComponent: Lazy<GroupMembersComponent>,
    lazyCourseComponent: Lazy<GroupCoursesComponent>,
    componentContext: ComponentContext,
    private val groupId: String,
) : ComponentContext by componentContext {

    private val groupMembersComponent by lazyMembersComponent
    private val courseComponent by lazyCourseComponent

    private val navigation = StackNavigation<Config>()

    val stack = childStack(source = navigation,
        initialConfiguration = Config.Members(groupId),
        childFactory = { config: Config, componentContext: ComponentContext ->
            when (config) {
                is Config.Members -> Child.Members(groupMembersComponent)
                is Config.Courses -> Child.Courses(courseComponent)
            }
        })

    val tabs: StateFlow<List<TabItem>> = MutableStateFlow(listOf(TabItem.Members, TabItem.Courses))
    val selectedTab = MutableStateFlow(0)

    fun onTabClick(index: Int) {
        selectedTab.update { index }
        navigation.replaceCurrent(when (tabs.value[index]) {
            is TabItem.Members -> Config.Members(groupId)
            is TabItem.Courses -> Config.Courses(groupId)
            TabItem.DutyRoster -> TODO()
            TabItem.Timetable -> TODO()
        })
    }

    sealed class Config(groupId: String) : Parcelable {
        class Members(groupId: String) : Config(groupId)
        class Courses(groupId: String) : Config(groupId)
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