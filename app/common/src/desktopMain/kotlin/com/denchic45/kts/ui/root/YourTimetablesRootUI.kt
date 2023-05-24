package com.denchic45.kts.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.ui.yourtimetables.YourTimetablesScreen

@Composable
fun YourTimetablesRootScreen(component: YourTimetablesRootStackChildrenContainer) {
    val stack by component.childStack.subscribeAsState()

    Children(stack) {
        when(val child = it.instance) {
            is YourTimetablesRootStackChildrenContainer.Child.YourTimetables -> {
                YourTimetablesScreen(child.component)
            }
        }
    }
}