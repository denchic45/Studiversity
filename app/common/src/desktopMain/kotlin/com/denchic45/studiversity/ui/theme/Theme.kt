package com.denchic45.studiversity.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState

@Composable
fun DesktopApp(
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    onCloseRequest: () -> Unit,
    title: String = "Untitled",
    icon: Painter? = painterResource("app_logo.png"),
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalWindowState provides state) {
        Window(
            visible = visible,
            title = title,
            onCloseRequest = onCloseRequest,
            icon = icon,
            state = state
        ) {
            DesktopAppTheme(useDarkTheme, content)
        }
    }
}

@Composable
fun DesktopAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CommonAppTheme(useDarkTheme, content)
}