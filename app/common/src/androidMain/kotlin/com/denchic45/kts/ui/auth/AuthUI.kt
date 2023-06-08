package com.denchic45.kts.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState


@Composable
fun AuthScreen(component: AuthComponent) {
    val childStack by component.childStack.subscribeAsState()
    val progress by component.progress.collectAsState()

    Column {
        LinearProgressIndicator(progress = progress)
        Children(stack = component.childStack) {
            when (val child = it.instance) {
                is AuthComponent.Child.Welcome -> WelcomeScreen(child.component)
                is AuthComponent.Child.Login -> LoginScreen(child.component)
                is AuthComponent.Child.Registration -> TODO()
                is AuthComponent.Child.ResetPassword -> TODO()
            }
        }
    }
}
