package com.denchic45.studiversity.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.ui.yourtimetables.YourTimetablesScreen

//@Composable
//fun YourTimetablesRootScreen(component: YourTimetablesRootComponent) {
//    val stack by component.childStack.subscribeAsState()
//
//    Children(stack) {
//        when(val child = it.instance) {
//            is YourTimetablesRootComponent.Child.YourTimetables -> {
//                YourTimetablesScreen(child.component)
//            }
//        }
//    }
//}