package com.denchic45.kts.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.ui.course.CourseScreen
import com.denchic45.kts.ui.studygroup.StudyGroupScreen
import com.denchic45.kts.ui.yourstudygroups.YourStudyGroupsScreen

@Composable
fun YourStudyGroupsRootScreen(component: YourStudyGroupsRootComponent) {
    val childStack by component.childStack.subscribeAsState()

    Children(childStack) {
        when (val child = it.instance) {
            is YourStudyGroupsRootComponent.Child.YourStudyGroups -> {
                YourStudyGroupsScreen(child.component)
            }

            is YourStudyGroupsRootComponent.Child.StudyGroup -> {
                StudyGroupScreen(child.component)
            }

            is YourStudyGroupsRootComponent.Child.Course -> {
                CourseScreen(child.component)
            }

            is YourStudyGroupsRootComponent.Child.CourseEditor -> TODO()
            is YourStudyGroupsRootComponent.Child.CourseTopics -> TODO()
            is YourStudyGroupsRootComponent.Child.CourseWork -> TODO()
            is YourStudyGroupsRootComponent.Child.CourseWorkEditor -> TODO()
        }
    }
}