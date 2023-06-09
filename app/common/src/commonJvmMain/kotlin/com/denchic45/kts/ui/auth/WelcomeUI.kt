package com.denchic45.kts.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.theme.spacing

@Composable
fun WelcomeScreen(component: WelcomeComponent) {
    val state by component.state.collectAsState()
    val domainText by component.url.collectAsState()
    AuthLayout(
        imageContent = { AnimatedLogo() },
        title = "Добро пожаловать!",
        description = "Для авторизации укажите сайт организации",
        content = {
            WelcomeContent(
                state = state,
                initialDomainText = domainText,
                onDomainType = component::onDomainType,
                onNextClick = component::onCheckDomainClick
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeContent(
    initialDomainText: String,
    state: WelcomeComponent.State,
    onDomainType: (String) -> Unit,
    onNextClick: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        var domainText by remember { mutableStateOf(initialDomainText) }
        OutlinedTextField(
            value = domainText,
            onValueChange = {
                domainText = it
                onDomainType(it)
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is WelcomeComponent.State.Loading,
            placeholder = { Text("Например: https://mysite.com") },
            trailingIcon = when (state) {
                is WelcomeComponent.State.Loading -> {
                    { CircularProgressIndicator(Modifier.size(24.dp)) }
                }

                is WelcomeComponent.State.Success -> {
                    { Icon(Icons.Default.Done, "done") }
                }

                else -> null
            },
            singleLine = true,
            isError = state.isError,
            supportingText = {
                when (state) {
                    WelcomeComponent.State.NoConnection -> Text("Проверьте интернет-подключение")
                    WelcomeComponent.State.FailedConnect -> Text("Не удалось подключиться по данному адресу")
                    WelcomeComponent.State.UnknownError -> Text("Неизвестная ошибка")
                    WelcomeComponent.State.WrongDomain -> Text("Некорректный домен")
                    WelcomeComponent.State.Timeout -> Text("Превышено время ожидания")
                    else -> {}
                }
            }
        )
        Spacer(Modifier.height(MaterialTheme.spacing.normal))
        Button(
            onClick = onNextClick,
            enabled = domainText.isNotEmpty()
        ) {
            key(domainText.isNotEmpty()) {
                Text("Продолжить")
            }
        }
    }
}