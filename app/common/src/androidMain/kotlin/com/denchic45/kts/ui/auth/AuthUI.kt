package com.denchic45.kts.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState

@Composable
fun AuthScreen(component: AuthComponent) {
    val childStack by component.childStack.subscribeAsState()
    val progress by component.progress.collectAsState()

    Children(stack = component.childStack) {
        when(val child = it.instance) {
            is AuthComponent.Child.Login -> TODO()
            is AuthComponent.Child.Registration -> TODO()
            is AuthComponent.Child.ResetPassword -> TODO()
            is AuthComponent.Child.Welcome -> TODO()
        }
    }
}