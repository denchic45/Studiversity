package com.denchic45.kts.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment

@Composable
fun WelcomeScreen(component: WelcomeComponent) {
    val state by component.state.collectAsState()
    AuthLayout(
        imageContent = {},
        title = "Добро пожаловать!",
        description = "Для авторизации укажите сайт организации",
        content = {
            WelcomeContent(
                state = state,
                onNextClick = component::onCheckDomainClick
            )
        }
    )
}

@Composable
fun WelcomeContent(state: WelcomeComponent.State, onNextClick: (String) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        var domainText by mutableStateOf("")
        OutlinedTextField(
            value = domainText,
            onValueChange = {
                domainText = it
            },
            enabled = state !is WelcomeComponent.State.Loading,
            trailingIcon = if (state is WelcomeComponent.State.Loading) {
                { CircularProgressIndicator() }
            } else null
        )
        Button(
            onClick = { onNextClick(domainText) },
            enabled = domainText.isNotEmpty()
        ) {
            Text("Продолжить")
        }
    }
}