package com.denchic45.studiversity.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.essenty.backhandler.BackDispatcher
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.lightWithLightHeader
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.styling.TitleBarStyle

@Composable
fun DesktopApp(
    state: WindowState = rememberWindowState(),
    visible: Boolean = true,
    onCloseRequest: () -> Unit,
    title: String = "Untitled",
    icon: Painter? = painterResource("app_logo.png"),
    backDispatcher: BackDispatcher,
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val theme = JewelTheme.lightThemeDefinition()
    IntUiTheme(
        theme = theme,
        styling = ComponentStyling.decoratedWindow(
            titleBarStyle = TitleBarStyle.lightWithLightHeader()
        ),
    ) {
        CompositionLocalProvider(LocalWindowState provides state, LocalBackDispatcher provides backDispatcher) {
            DecoratedWindow(
                visible = visible,
                title = title,
                onCloseRequest = onCloseRequest,
                icon = icon,
                state = state
            ) {
//                CompositionLocalProvider(LocalGlobalColors provides GlobalColors.light(), LocalGlobalMetrics provides GlobalMetrics.defaults(),LocalTitleBarStyle provides TitleBarStyle.lightWithLightHeader() , LocalTextStyle provides TextStyle.Default) {
                TitleBar() {
//                        Row() {
//                            Text("custom title bar")
//                        }
                }
//                }
                DesktopAppTheme(useDarkTheme, content)
            }
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