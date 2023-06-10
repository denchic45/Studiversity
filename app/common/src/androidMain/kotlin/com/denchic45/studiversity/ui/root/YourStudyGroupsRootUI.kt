package com.denchic45.studiversity.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.ui.appbar2.LocalAppBarState
import com.denchic45.studiversity.ui.course.CourseScreen
import com.denchic45.studiversity.ui.courseeditor.CourseEditorScreen
import com.denchic45.studiversity.ui.studygroup.StudyGroupScreen
import com.denchic45.studiversity.ui.yourstudygroups.YourStudyGroupsScreen

//@Composable
//fun YourStudyGroupsRootScreen(
//    component: YourStudyGroupsRootComponent,
//) {
//    val appBarState = LocalAppBarState.current
//    val childStack by component.childStack.subscribeAsState()
//
//    Children(childStack) {
//        when (val child = it.instance) {
//            is YourStudyGroupsRootComponent.Child.YourStudyGroups -> {
//                YourStudyGroupsScreen(child.component)
//            }
//
//            is YourStudyGroupsRootComponent.Child.StudyGroup -> {
//                StudyGroupScreen(child.component)
//            }
//
//            is YourStudyGroupsRootComponent.Child.Course -> {
//                CourseScreen(child.component)
//            }
//
//            is YourStudyGroupsRootComponent.Child.CourseEditor -> {
//                CourseEditorScreen(child.component)
//            }
//
//            is YourStudyGroupsRootComponent.Child.CourseTopics -> TODO()
//            is YourStudyGroupsRootComponent.Child.CourseWork -> TODO()
//            is YourStudyGroupsRootComponent.Child.CourseWorkEditor -> TODO()
//        }
//    }
//}