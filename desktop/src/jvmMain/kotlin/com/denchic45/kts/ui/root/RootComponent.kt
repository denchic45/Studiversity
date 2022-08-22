package com.denchic45.kts.ui.root

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.ui.login.LoginComponent
import javax.inject.Inject


class RootComponent @Inject constructor(
    loginComponent: LoginComponent,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    private val stack = childStack<Config, Child>(
        source = navigation,
        initialConfiguration = Config.Login,
        childFactory = { config: Config, componentContext: ComponentContext ->
            Child.Login(loginComponent)
        }
    )

    val childStack: Value<ChildStack<*, Child>> get() = stack

    private sealed class Config : Parcelable {
        object Login : Config()
    }

    sealed class Child {
        class Login(val loginComponent: LoginComponent) : Child()
    }
}