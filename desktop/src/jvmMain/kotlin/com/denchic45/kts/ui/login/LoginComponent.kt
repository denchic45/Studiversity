package com.denchic45.kts.ui.login

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.service.AuthService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class LoginComponent(
    private val authService: AuthService,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    fun onLoginClick(email: String, password: String) {
        GlobalScope.launch(Dispatchers.IO) {
            authService.signInWithEmailAndPassword(email, password)
        }
    }
}