package com.denchic45.kts.ui.login

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.kts.data.service.AuthService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginComponent @Inject constructor(
    private val authService: AuthService,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    fun onLoginClick(email: String, password: String) {
        GlobalScope.launch {
            authService.signInWithEmailAndPassword(email, password)
        }
    }
}