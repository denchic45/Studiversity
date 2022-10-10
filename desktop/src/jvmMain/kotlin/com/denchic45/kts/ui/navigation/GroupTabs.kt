package com.denchic45.kts.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.ui.group.courses.GroupCoursesComponent
import com.denchic45.kts.ui.group.members.GroupMembersComponent

sealed class GroupTabsConfig : Parcelable {
    object Members : GroupTabsConfig()
    object Courses : GroupTabsConfig()
}

sealed class GroupTabsChild {
    class Members(val membersComponent: GroupMembersComponent) : GroupTabsChild()
    class Courses(val coursesComponent: GroupCoursesComponent) : GroupTabsChild()
}