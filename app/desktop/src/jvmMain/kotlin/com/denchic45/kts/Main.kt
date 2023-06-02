package com.denchic45.kts

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.kts.di.*
import com.denchic45.kts.ui.AppBarMediator
import com.denchic45.kts.ui.LocalAppBarMediator
import com.denchic45.kts.ui.MainContent
import com.denchic45.kts.ui.login.LoginScreen
import com.denchic45.kts.ui.theme.DesktopApp
import java.awt.Toolkit

val splashComponent = appComponent.splashComponent

fun main() = mainApp()

@OptIn(ExperimentalDecomposeApi::class)
private fun mainApp() {
    val lifecycle = LifecycleRegistry()
    val backDispatcher = BackDispatcher()
    val componentContext = DefaultComponentContext(
        lifecycle = lifecycle,
        backHandler = backDispatcher
    )
    application {
        val isAuth by splashComponent.isAuth.collectAsState(null)

        isAuth?.let {
            if (it) {
                val size = Toolkit.getDefaultToolkit().screenSize.run {
                    DpSize((width - 124).dp, (height - 124).dp)
                }
                val state =
                    rememberWindowState(size = size, position = WindowPosition(Alignment.Center))
                LifecycleController(lifecycle, state)

                DesktopApp(
                    title = "Studiversity",
                    onCloseRequest = ::exitApplication,
                    state = state
                ) {
                    CompositionLocalProvider(LocalAppBarMediator provides AppBarMediator()) {
                        MainContent(appComponent.mainComponent(componentContext))
                    }
                }

            } else {
                DesktopApp(
                    title = "Studiversity - Авторизация",
                    onCloseRequest = ::exitApplication,
                    state = rememberWindowState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
                ) {
                    LoginScreen(appComponent.loginComponent(componentContext))
                }
            }
        }
    }
}

fun previewUi() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            state = WindowState(size = DpSize(Dp.Unspecified, Dp.Unspecified))
        ) {

        }
    }
}