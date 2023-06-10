package com.denchic45.kts.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.essenty.backhandler.backHandler
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.kts.app
import com.denchic45.kts.ui.auth.AuthScreen
import com.denchic45.kts.ui.root.RootComponent
import com.denchic45.kts.ui.theme.AppTheme

private val appComponent = app.appComponent


class MainActivity : ComponentActivity() {
    
    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar?.hide()
        val rootComponent = appComponent.rootComponent(defaultComponentContext())
//        val defaultComponentContext = defaultComponentContext()

        installSplashScreen().setKeepOnScreenCondition {
            !rootComponent.isReady
        }

//        val component = appComponent.mainComponent(defaultComponentContext)
        val confirmDialogInteractor = appComponent.confirmDialogInteractor
        setContent {
            AppTheme {
                val active by rootComponent.childActive.subscribeAsState()
                AnimatedContent(targetState = active.overlay) { overlay ->
                    overlay?.instance.let {
                        when (val child = it) {
                            is RootComponent.Child.Auth -> AuthScreen(child.component)
                            is RootComponent.Child.Main -> MainScreen(
                                component = child.component,
                                activity = this@MainActivity,
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
}