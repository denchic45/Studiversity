package com.denchic45.kts.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.ui.studygroup.courses.StudyGroupCoursesComponent
import com.denchic45.kts.ui.studygroup.members.StudyGroupMembersComponent

sealed class GroupTabsConfig : Parcelable {
    object Members : GroupTabsConfig()
    object Courses : GroupTabsConfig()
}

sealed class StudyGroupTabsChild {
    class Members(val membersComponent: StudyGroupMembersComponent) : StudyGroupTabsChild()
    class Courses(val coursesComponent: StudyGroupCoursesComponent) : StudyGroupTabsChild()
}