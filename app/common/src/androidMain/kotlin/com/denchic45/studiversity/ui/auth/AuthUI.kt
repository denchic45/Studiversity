package com.denchic45.studiversity.ui.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetpack.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.ifSuccess
import com.denchic45.studiversity.ui.theme.md_theme_dark_tertiary


@Composable
fun AuthScreen(component: AuthComponent) {
    val childStack by component.childStack.subscribeAsState()

    Column(Modifier.fillMaxSize()) {
        AuthProgress(childStack.active.instance)
        Children(stack = component.childStack, animation = stackAnimation(slide() + fade())) {
            when (val child = it.instance) {
                is AuthComponent.Child.Welcome -> WelcomeScreen(child.component)
                is AuthComponent.Child.Login -> LoginScreen(child.component)
                is AuthComponent.Child.Registration -> RegistrationScreen(child.component)
                is AuthComponent.Child.ResetPassword -> TODO()
            }
        }
    }
}

@Composable
private fun AuthProgress(child: AuthComponent.Child) {
    val animatedProgress = animateFloatAsState(
        targetValue = when (child) {
            is AuthComponent.Child.Welcome -> 0f
            is AuthComponent.Child.Login -> {
                child.component.state.result?.ifSuccess { 1f } ?: 0.5f
            }

            is AuthComponent.Child.ResetPassword,
            is AuthComponent.Child.Registration,
            -> 0.25f
        },
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    ).value
    LinearProgressIndicator(
        progress = animatedProgress,
        modifier = Modifier.fillMaxWidth(),
        color = md_theme_dark_tertiary,
        trackColor = Color.Transparent
    )
}
