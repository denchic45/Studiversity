package com.denchic45.studiversity.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.ui.course.CourseScreen
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootChild
import com.denchic45.studiversity.ui.studygroup.StudyGroupScreen
import com.denchic45.studiversity.ui.yourstudygroups.YourStudyGroupsScreen
import com.denchic45.studiversity.ui.yourtimetables.YourTimetablesScreen

@Composable
fun RootScreen(component: RootStackChildrenContainer) {
    val childStack by component.childStack.subscribeAsState()

    Children(childStack) {
        when (val child = it.instance) {
            is RootChild.YourTimetables -> YourTimetablesScreen(child.component)
            is RootChild.YourStudyGroups -> YourStudyGroupsScreen(child.component)
            is RootChild.StudyGroup -> StudyGroupScreen(child.component)
            is RootChild.Course -> CourseScreen(child.component)
            is RootChild.AdminDashboard -> TODO()
            is RootChild.Works -> TODO()
            is RootChild.CourseEditor -> TODO()
            is RootChild.CourseWork -> TODO()
            is RootChild.CourseWorkEditor -> TODO()
        }
    }
}