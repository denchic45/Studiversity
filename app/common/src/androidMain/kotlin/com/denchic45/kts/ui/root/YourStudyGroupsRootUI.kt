package com.denchic45.kts.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.courseeditor.CourseEditorScreen
import com.denchic45.kts.ui.studygroup.StudyGroupScreen
import com.denchic45.kts.ui.yourstudygroups.YourStudyGroupsScreen

@Composable
fun YourStudyGroupsRootScreen(
    component: YourStudyGroupsRootComponent,
    appBarInteractor: AppBarInteractor
) {
    val childStack by component.childStack.subscribeAsState()

    Children(childStack) {
        when (val child = it.instance) {
            is YourStudyGroupsRootComponent.Child.YourStudyGroups -> {
                YourStudyGroupsScreen(child.component, appBarInteractor)
            }

            is YourStudyGroupsRootComponent.Child.StudyGroup -> {
                StudyGroupScreen(child.component, appBarInteractor)
            }

            is YourStudyGroupsRootComponent.Child.Course -> {
                TODO()
            }

            is YourStudyGroupsRootComponent.Child.CourseEditor -> {
                CourseEditorScreen(child.component, appBarInteractor)
            }

            is YourStudyGroupsRootComponent.Child.CourseTopics -> TODO()
            is YourStudyGroupsRootComponent.Child.CourseWork -> TODO()
            is YourStudyGroupsRootComponent.Child.CourseWorkEditor -> TODO()
        }
    }
}