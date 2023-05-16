package com.denchic45.kts.ui.studygroups

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.denchic45.kts.ui.AppBarMediator
import com.denchic45.kts.ui.studygroup.StudyGroupScreen

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