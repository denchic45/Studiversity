package com.denchic45.studiversity.ui.login

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.usecase.SignInWithEmailAndPasswordUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class LoginComponent(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    fun onLoginClick(email: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            signInWithEmailAndPasswordUseCase(email, password)
        }
    }
}