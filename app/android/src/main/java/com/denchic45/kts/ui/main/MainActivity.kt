package com.denchic45.kts.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.app
import com.denchic45.kts.ui.auth.AuthScreen
import com.denchic45.kts.ui.root.RootComponent
import com.denchic45.kts.ui.theme.AppTheme

class MainActivity : ComponentActivity() {

    private val appComponent = app.appComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val defaultComponentContext = defaultComponentContext()
        val rootComponent = appComponent.rootComponent(defaultComponentContext)
        installSplashScreen().setKeepOnScreenCondition {
            rootComponent.isReady
        }

        val component = appComponent.mainComponent(defaultComponentContext)
        val confirmDialogInteractor = appComponent.confirmDialogInteractor
        setContent {
            AppTheme {

                val active by rootComponent.childActive.subscribeAsState()

                active.overlay?.instance.let {
                    when (val child = it) {
                        is RootComponent.Child.Auth -> AuthScreen(child.component)
                        is RootComponent.Child.Main -> MainScreen(
                            component = component,
                            activity = this,
                            confirmDialogInteractor = confirmDialogInteractor
                        )

                        RootComponent.Child.Splash -> {}
                        null -> {}
                    }
                }

            }
        }
    }
}