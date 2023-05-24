package com.denchic45.kts.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.course.CourseScreen
import com.denchic45.kts.ui.courseeditor.CourseEditorScreen
import com.denchic45.kts.ui.studygroup.StudyGroupScreen
import com.denchic45.kts.ui.yourstudygroups.YourStudyGroupsScreen

@Composable
fun YourStudyGroupsRootScreen(
    component: YourStudyGroupsRootStackChildrenContainer,
    appBarInteractor: AppBarInteractor,
) {
    val childStack by component.childStack.subscribeAsState()

    Children(childStack) {
        when (val child = it.instance) {
            is YourStudyGroupsRootStackChildrenContainer.Child.YourStudyGroups -> {
                YourStudyGroupsScreen(child.component, appBarInteractor)
            }

            is YourStudyGroupsRootStackChildrenContainer.Child.StudyGroup -> {
                StudyGroupScreen(child.component, appBarInteractor)
            }

            is YourStudyGroupsRootStackChildrenContainer.Child.Course -> {
                CourseScreen(child.component, appBarInteractor)
            }

            is YourStudyGroupsRootStackChildrenContainer.Child.CourseEditor -> {
                CourseEditorScreen(child.component, appBarInteractor)
            }

            is YourStudyGroupsRootStackChildrenContainer.Child.CourseTopics -> TODO()
            is YourStudyGroupsRootStackChildrenContainer.Child.CourseWork -> TODO()
            is YourStudyGroupsRootStackChildrenContainer.Child.CourseWorkEditor -> TODO()
        }
    }
}