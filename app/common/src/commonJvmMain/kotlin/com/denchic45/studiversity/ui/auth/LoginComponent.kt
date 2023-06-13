package com.denchic45.studiversity.ui.auth

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.PlatformMain
import com.denchic45.studiversity.domain.EmptyResource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class LoginComponent(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    @Assisted
    private val onForgotPassword:()->Unit,
    @Assisted
    private val onRegister:()->Unit,
    @Assisted
    private val onSuccess: () -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val state = LoginState()

    fun onEmailType(email: String) {
        state.email = email
    }

    fun onPasswordType(password: String) {
        state.password = password
    }

    fun onForgotPasswordClick() {
        onForgotPassword()
    }

    fun onRegisterClick() {
        onRegister()
    }

    fun onLoginClick() {
        componentScope.launch {
            state.result = resourceOf()
            val result = signInWithEmailAndPasswordUseCase(state.email, state.password)
            withContext(Dispatchers.PlatformMain) {
                state.result = result
                result.onSuccess { onSuccess() }
            }
        }
    }
}

@Stable
class LoginState {
    var email by mutableStateOf("")
    var password by mutableStateOf("")

    var result by mutableStateOf<EmptyResource?>(null)
}