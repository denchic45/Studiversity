package com.denchic45.kts.ui.root

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.denchic45.kts.ui.admindashboard.AdminDashboardScreen
import com.denchic45.kts.ui.appbar2.LocalAppBarState
import com.denchic45.kts.ui.course.CourseScreen
import com.denchic45.kts.ui.courseeditor.CourseEditorScreen
import com.denchic45.kts.ui.coursework.CourseWorkScreen
import com.denchic45.kts.ui.courseworkeditor.CourseWorkEditorScreen
import com.denchic45.kts.ui.navigation.RootStackChildrenContainer
import com.denchic45.kts.ui.navigator.RootChild
import com.denchic45.kts.ui.studygroup.StudyGroupScreen
import com.denchic45.kts.ui.yourstudygroups.YourStudyGroupsScreen
import com.denchic45.kts.ui.yourtimetables.YourTimetablesScreen

@Composable
fun RootStackScreen(
    component: RootStackChildrenContainer,
) {
    val appBarState = LocalAppBarState.current
    Children(stack = component.childStack) {
        when (val child = it.instance) {
            is RootChild.YourTimetables -> YourTimetablesScreen(child.component)
            is RootChild.YourStudyGroups -> YourStudyGroupsScreen(child.component)
            is RootChild.AdminDashboard -> AdminDashboardScreen(child.component)
            is RootChild.Course -> CourseScreen(child.component)
            is RootChild.StudyGroup -> StudyGroupScreen(child.component)
            is RootChild.Works -> TODO()
            is RootChild.CourseEditor -> CourseEditorScreen(child.component)
            is RootChild.CourseWork -> CourseWorkScreen(child.component)
            is RootChild.CourseWorkEditor -> CourseWorkEditorScreen(child.component)
        }
    }
}