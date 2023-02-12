package com.denchic45.kts.ui.login

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.usecase.SignInWithEmailAndPasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class LoginComponent(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    fun onLoginClick(email: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            signInWithEmailAndPasswordUseCase(email, password)
        }
    }
}