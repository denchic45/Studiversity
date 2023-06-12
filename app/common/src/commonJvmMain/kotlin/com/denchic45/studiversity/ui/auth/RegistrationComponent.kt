package com.denchic45.studiversity.ui.auth

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class RegistrationComponent(
    @Assisted
    private val onSuccess: () -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    fun onFirstNameType(s: String) {
        TODO("Not yet implemented")
    }

    fun onSurnameType(s: String) {

    }

    fun onPatronymicType(s: String) {

    }

    fun onEmailType(s: String) {

    }

    fun onPasswordType(s: String) {

    }

    fun onRetryPasswordType(s: String) {

    }

    fun onSignInClick() {

    }

    fun onSignUpClick() {

    }

    val state = RegistrationState()
}

@Stable
class RegistrationState {
    var firstName by mutableStateOf("")
    var surname by mutableStateOf("")
    var patronymic by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var retryPassword by mutableStateOf("")
}