package com.denchic45.studiversity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.*
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.studiversity.di.*
import com.denchic45.studiversity.ui.AppBarMediator
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.MainScreen
import com.denchic45.studiversity.ui.auth.AuthScreen
import com.denchic45.studiversity.ui.root.RootComponent
import com.denchic45.studiversity.ui.theme.DesktopApp
import com.denchic45.studiversity.di.appComponent
import java.awt.Toolkit

//val splashComponent = appComponent.splashComponent

fun main() = mainApp()

@OptIn(ExperimentalDecomposeApi::class)
private fun mainApp() {
    val lifecycle = LifecycleRegistry()
    val backDispatcher = BackDispatcher()
    val componentContext = DefaultComponentContext(
        lifecycle = lifecycle,
        backHandler = backDispatcher
    )
    val rootComponent = appComponent.rootComponent(componentContext)
    application {

        val active by rootComponent.childActive.subscribeAsState()

        when (val child = active.overlay!!.instance) {
            is RootComponent.Child.Auth -> {
                val state = rememberWindowState()
                DesktopApp(
                    title = "Studiversity - Авторизация",
                    onCloseRequest = ::exitApplication,
                    state = state,
                    backDispatcher = backDispatcher
                ) {
                    AuthScreen(child.component)
                }
            }

            is RootComponent.Child.Main -> {
                val size = Toolkit.getDefaultToolkit().screenSize.run {
                    DpSize((width - 124).dp, (height - 124).dp)
                }
                val state =
                    rememberWindowState(
                        size = size,
                        position = WindowPosition(Alignment.Center)
                    )
                LifecycleController(lifecycle, state)

                DesktopApp(
                    title = "Studiversity",
                    onCloseRequest = ::exitApplication,
                    state = state,
                    backDispatcher = backDispatcher
                ) {
                    CompositionLocalProvider(LocalAppBarMediator provides AppBarMediator()) {
                        MainScreen(appComponent.mainComponent(componentContext))
                    }
                }
            }

            RootComponent.Child.Splash -> DesktopApp(
                title = "Studiversity",
                onCloseRequest = ::exitApplication,
                backDispatcher = backDispatcher
            ) {
                CompositionLocalProvider(LocalAppBarMediator provides AppBarMediator()) {
                    Box(Modifier.size(200.dp))
                }
            }
        }

//        val isAuth by splashComponent.isAuth.collectAsState(null)
//
//        isAuth?.let {
//            if (it) {
//
//            } else {
//
//            }
//        }
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