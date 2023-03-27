package com.denchic45.kts.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.ui.studygroup.courses.GroupCoursesComponent
import com.denchic45.kts.ui.studygroup.members.GroupMembersComponent

sealed class GroupTabsConfig : Parcelable {
    object Members : GroupTabsConfig()
    object Courses : GroupTabsConfig()
}

sealed class StudyGroupTabsChild {
    class Members(val membersComponent: GroupMembersComponent) : StudyGroupTabsChild()
    class Courses(val coursesComponent: GroupCoursesComponent) : StudyGroupTabsChild()
}