package com.denchic45.kts.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState

@Composable
fun DesktopApp(
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    onCloseRequest: () -> Unit,
    title: String = "Untitled",
    icon: Painter? = null,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalWindowState provides state) {
        Window(
            visible = visible,
            title = title,
            onCloseRequest = onCloseRequest,
            icon = icon
        ) {
            DesktopAppTheme(useDarkTheme, content)
        }
    }
}

@Composable
fun DesktopAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CommonAppTheme(useDarkTheme, AppTypography, content)
}