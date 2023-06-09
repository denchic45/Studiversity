package com.denchic45.kts.ui.auth

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.fade
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.plus
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.slide
import com.arkivanov.decompose.extensions.compose.jetbrains.stack.animation.stackAnimation
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.WindowWidthSizeClass
import com.denchic45.kts.domain.ifSuccess
import com.denchic45.kts.ui.animation.slideVertical
import com.denchic45.kts.ui.theme.LocalWindowState
import com.denchic45.kts.ui.theme.calculateWindowSizeClass
import com.denchic45.kts.ui.theme.md_theme_dark_tertiary
import com.denchic45.kts.ui.theme.rememberWindowSizeClass

@Composable
fun AuthScreen(component: AuthComponent) {
    val childStack by component.childStack.subscribeAsState()

    val windowSize = rememberWindowSizeClass()
    Column {
        Text(windowSize.widthSizeClass.toString())
        Text(LocalWindowState.current.size.width.value.toString())
    }

    Surface {
        Column(Modifier.fillMaxSize()) {
            AuthProgress(childStack.active.instance)
            val windowSizeClass = calculateWindowSizeClass()
            Children(
                stack = component.childStack, animation = stackAnimation(
                    when (windowSizeClass.widthSizeClass) {
                        WindowWidthSizeClass.Compact -> {
                            slide() + fade()
                        }

                        else -> {
                            slideVertical() + fade()
                        }
                    }
                )
            ) {
                when (val child = it.instance) {
                    is AuthComponent.Child.Welcome -> WelcomeScreen(child.component)
                    is AuthComponent.Child.Login -> LoginScreen(child.component)
                    is AuthComponent.Child.Registration -> TODO()
                    is AuthComponent.Child.ResetPassword -> TODO()
                }
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
