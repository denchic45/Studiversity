package com.denchic45.studiversity.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.overlay
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.PlatformMain
import com.denchic45.studiversity.domain.usecase.ObserveAuthStateUseCase
import com.denchic45.studiversity.ui.MainComponent
import com.denchic45.studiversity.ui.auth.AuthComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class RootComponent(
    authComponent: (ComponentContext) -> AuthComponent,
    mainComponent: (ComponentContext) -> MainComponent,
    private val observeAuthStateUseCase: ObserveAuthStateUseCase,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val navigation = OverlayNavigation<Config>()

    val childActive = childOverlay(
        source = navigation,
        initialConfiguration = { Config.Splash },
        childFactory = { config, context ->
            when (config) {
                Config.Auth -> Child.Auth(authComponent(context))
                Config.Main -> Child.Main(mainComponent(context))
                Config.Splash -> Child.Splash
            }
        }
    )

    val isReady
        get() = childActive.overlay?.instance !is Child.Splash

    init {
        componentScope.launch {
            observeAuthStateUseCase().collect {
                withContext(Dispatchers.PlatformMain) {
                    if (it) {
                        navigation.activate(Config.Main)
                    } else {
                        navigation.activate(Config.Auth)
                    }
                }
            }
        }
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object Splash : Config
        object Auth : Config
        object Main : Config
    }

    sealed interface Child {
        object Splash : Child
        class Auth(val component: AuthComponent) : Child
        class Main(val component: MainComponent) : Child
    }
}