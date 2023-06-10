package com.denchic45.studiversity.ui.studygroups

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.denchic45.studiversity.ui.AppBarMediator
import com.denchic45.studiversity.ui.studygroup.StudyGroupScreen

//@Composable
//fun StudyGroupsScreen(appBarMediator: AppBarMediator, studyGroupsComponent: StudyGroupsComponent) {
//    Children(stack = studyGroupsComponent.stack) {
//        when (val child = it.instance) {
//            StudyGroupsChild.Empty -> {
//                // TODO:
//            }
//            is StudyGroupsChild.Group -> StudyGroupScreen(
//                child.component,
//                appBarMediator
//            )
//        }
//    }
//}