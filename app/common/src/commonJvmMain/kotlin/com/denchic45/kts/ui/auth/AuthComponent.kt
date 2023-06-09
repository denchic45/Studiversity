package com.denchic45.kts.ui.auth

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.router.stack.push
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class AuthComponent(
    private val welcomeComponent: (
        onSuccess: () -> Unit,
        ComponentContext,
    ) -> WelcomeComponent,
    private val loginComponent: (
        onForgotPassword: () -> Unit,
        onRegister: () -> Unit,
        onSuccess: () -> Unit,
        ComponentContext,
    ) -> LoginComponent,
    private val resetPasswordComponent: (
        onSuccess: () -> Unit,
        ComponentContext,
    ) -> ResetPasswordComponent,
    private val registrationComponent: (
        onSuccess: () -> Unit,
        ComponentContext,
    ) -> RegistrationComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<Config>()

    val childStack = childStack(
        source = navigation,
        initialConfiguration = Config.Welcome,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                Config.Welcome -> Child.Welcome(
                    welcomeComponent(::onLogin, context)
                )

                Config.Login -> Child.Login(
                    loginComponent(
                        ::onResetPassword,
                        ::onRegister,
                        {
//                            progress.update { 1f }
                        },
                        context
                    )
                )

                Config.ResetPassword -> Child.ResetPassword(
                    resetPasswordComponent(::onLogin, context)
                )

                Config.Registration -> Child.Registration(
                    registrationComponent(::onLogin, context)
                )
            }
        })


//    val progress = MutableStateFlow(0f)

    private fun onResetPassword() {
//        progress.update { 0.25f }
        navigation.push(Config.ResetPassword)
    }

    private fun onRegister() {
//        progress.update { 0.25f }
        navigation.push(Config.Registration)
    }

    private fun onLogin() {
        navigation.bringToFront(Config.Login)
//        progress.update { 0.5f }
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object Welcome : Config
        object Login : Config
        object ResetPassword : Config
        object Registration : Config
    }

    sealed interface Child {
        class Welcome(val component: WelcomeComponent) : Child
        class Login(val component: LoginComponent) : Child
        class ResetPassword(val component: ResetPasswordComponent) : Child
        class Registration(val component: RegistrationComponent) : Child
    }
}