package com.denchic45.kts.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.ui.course.CourseScreen
import com.denchic45.kts.ui.navigation.RootStackChildrenContainer
import com.denchic45.kts.ui.navigator.RootChild
import com.denchic45.kts.ui.studygroup.StudyGroupScreen
import com.denchic45.kts.ui.yourstudygroups.YourStudyGroupsScreen

@Composable
fun RootScreen(component: RootStackChildrenContainer) {
    val childStack by component.childStack.subscribeAsState()

    Children(childStack) {
        when (val child = it.instance) {
            is RootChild.YourStudyGroups -> YourStudyGroupsScreen(child.component)

            is RootChild.StudyGroup -> StudyGroupScreen(child.component)

            is RootChild.Course -> CourseScreen(child.component)

            is RootChild.AdminDashboard -> TODO()
            is RootChild.Works -> TODO()
            is RootChild.YourTimetables -> TODO()
        }
    }
}