package com.denchic45.kts.ui.group

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.StackNavigator
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.denchic45.kts.ui.group.courses.GroupCoursesComponent
import com.denchic45.kts.ui.group.members.GroupMembersComponent
import com.denchic45.kts.ui.navigation.GroupConfig
import com.denchic45.kts.ui.navigation.GroupTabsChild
import com.denchic45.kts.ui.navigation.GroupTabsConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import me.tatarka.inject.annotations.Inject

@Inject
class GroupComponent(
    lazyGroupMembersComponent: (navigator: StackNavigator<in GroupConfig>, groupId: String) -> GroupMembersComponent,
    lazyGroupCourseComponent: (groupId: String) -> GroupCoursesComponent,
    componentContext: ComponentContext,
    private val navigator: StackNavigator<in GroupConfig>,
    private val groupId: String,
) : ComponentContext by componentContext {

    private val groupMembersComponent by lazy {
        lazyGroupMembersComponent(navigator, groupId)
    }
    private val groupCourseComponent by lazy { lazyGroupCourseComponent(groupId) }

    private val navigation = StackNavigation<GroupTabsConfig>()
    val stack = childStack(source = navigation,
        initialConfiguration = GroupTabsConfig.Members,
        childFactory = { tabConfig: GroupTabsConfig, _ ->
            when (tabConfig) {
                is GroupTabsConfig.Members -> GroupTabsChild.Members(groupMembersComponent)
                is GroupTabsConfig.Courses -> GroupTabsChild.Courses(groupCourseComponent)
            }
        })

    val tabs: StateFlow<List<TabItem>> = MutableStateFlow(listOf(TabItem.Members, TabItem.Courses))
    val selectedTab = MutableStateFlow(0)

    fun onTabClick(index: Int) {
        selectedTab.value = index
        navigation.bringToFront(
            when (tabs.value[index]) {
                is TabItem.Members -> GroupTabsConfig.Members
                is TabItem.Courses -> GroupTabsConfig.Courses
                TabItem.DutyRoster -> TODO()
                TabItem.Timetable -> TODO()
            }
        )
    }


    sealed class TabItem(val title: String) {
        object Members : TabItem("Участники")
        object Courses : TabItem("Курсы")
        object DutyRoster : TabItem("Дежурства")
        object Timetable : TabItem("Дежурства")
    }
}