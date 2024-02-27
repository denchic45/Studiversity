package com.denchic45.studiversity.ui.theme

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.denchic45.studiversity.ui.components.flow.FlowCrossAxisAlignment
import org.jetbrains.jewel.foundation.GlobalColors
import org.jetbrains.jewel.foundation.GlobalMetrics
import org.jetbrains.jewel.foundation.LocalGlobalColors
import org.jetbrains.jewel.foundation.LocalGlobalMetrics
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalTextStyle
import org.jetbrains.jewel.intui.core.theme.IntUiLightTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.defaults
import org.jetbrains.jewel.intui.standalone.theme.light
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.intui.window.styling.lightWithLightHeader
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.*
import java.awt.Desktop
import java.net.URI

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