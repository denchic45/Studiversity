package com.denchic45.studiversity.ui.root

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.ui.admindashboard.AdminDashboardScreen
import com.denchic45.studiversity.ui.course.CourseScreen
import com.denchic45.studiversity.ui.courseeditor.CourseEditorScreen
import com.denchic45.studiversity.ui.coursework.CourseWorkScreen
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorScreen
import com.denchic45.studiversity.ui.navigation.RootStackChildrenContainer
import com.denchic45.studiversity.ui.navigator.RootChild
import com.denchic45.studiversity.ui.studygroup.StudyGroupScreen
import com.denchic45.studiversity.ui.yourstudygroups.YourStudyGroupsScreen
import com.denchic45.studiversity.ui.yourtimetables.YourTimetablesScreen

@Composable
fun RootStackScreen(
    component: RootStackChildrenContainer,
) {
    val childStack by component.childStack.subscribeAsState()
//    Children(stack = component.childStack) {
    Crossfade(targetState = childStack) {
            when (val child = it.active.instance) {
                is RootChild.YourTimetables -> YourTimetablesScreen(child.component)
                is RootChild.YourStudyGroups -> YourStudyGroupsScreen(child.component)
                is RootChild.AdminDashboard -> AdminDashboardScreen(child.component)
                is RootChild.Course -> CourseScreen(child.component)
                is RootChild.StudyGroup -> StudyGroupScreen(child.component)
                is RootChild.YourWorks -> TODO()
                is RootChild.CourseEditor -> CourseEditorScreen(child.component)
                is RootChild.CourseWork -> CourseWorkScreen(child.component)
                is RootChild.CourseWorkEditor -> CourseWorkEditorScreen(child.component)
            }
        }
//    }
}