package com.denchic45.studiversity

import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.studiversity.di.appComponent
import com.denchic45.studiversity.ui.auth.AuthWindow
import com.denchic45.studiversity.ui.main.MainWindow
import com.denchic45.studiversity.ui.root.RootComponent
import com.denchic45.studiversity.ui.splash.SplashWindow

fun main() = mainApp()

private fun mainApp() {
    val lifecycle = LifecycleRegistry()
    val backDispatcher = BackDispatcher()
    val componentContext = DefaultComponentContext(
        lifecycle = lifecycle,
        backHandler = backDispatcher
    )
    val rootComponent = appComponent.rootComponent(componentContext)
    application {
        val childSlot by rootComponent.childSlot.subscribeAsState()

        when (val child = childSlot.child!!.instance) {
            RootComponent.Child.Splash -> SplashWindow(
                backDispatcher = backDispatcher
            )

            is RootComponent.Child.Auth -> AuthWindow(
                component = child.component,
                backDispatcher = backDispatcher
            )

            is RootComponent.Child.Main -> MainWindow(
                component = appComponent.mainComponent(componentContext),
                lifecycle = lifecycle,
                backDispatcher = backDispatcher
            )
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