package com.denchic45.studiversity.ui.login

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.usecase.SignInWithEmailAndPasswordUseCase
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class LoginComponent(
    private val signInWithEmailAndPasswordUseCase: SignInWithEmailAndPasswordUseCase,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()
    fun onLoginClick(email: String, password: String) {
        componentScope.launch(Dispatchers.IO) {
            signInWithEmailAndPasswordUseCase(email, password)
        }
    }
}