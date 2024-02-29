package com.denchic45.studiversity.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.child
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.usecase.ObserveAuthStateUseCase
import com.denchic45.studiversity.ui.auth.AuthComponent
import com.denchic45.studiversity.ui.main.MainComponent
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

    private val navigation = SlotNavigation<Config>()

    val childSlot = childSlot(
        source = navigation,
        key = "DefaultChildSlot",
        initialConfiguration = { Config.Splash },
        childFactory = { config, context ->
            when (config) {
                Config.Splash -> Child.Splash
                Config.Auth -> Child.Auth(authComponent(context))
                Config.Main -> Child.Main(mainComponent(context))
            }
        }
    )

    val isReady
        get() = childSlot.child?.instance !is Child.Splash

    init {
        componentScope.launch {
            observeAuthStateUseCase().collect {
                withContext(Dispatchers.Main) {
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