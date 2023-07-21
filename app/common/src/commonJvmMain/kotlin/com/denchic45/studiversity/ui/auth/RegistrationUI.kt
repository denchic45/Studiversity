package com.denchic45.studiversity.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.WindowWidthSizeClass
import com.denchic45.studiversity.ui.theme.calculateWindowSizeClass
import com.denchic45.studiversity.ui.theme.spacing


@Composable
fun RegistrationScreen(component: RegistrationComponent) {
    val state = remember { component.state }
    AuthLayout(
        imageContent = { AuthHeaderIcon(rememberVectorPainter(Icons.Outlined.Email)) },
        title = "Регистрация",
        content = {
            RegistrationContent(
                state = state,
                onFirstNameType = component::onFirstNameType,
                onSurnameType = component::onSurnameType,
                onPatronymicType = component::onPatronymicType,
                onEmailType = component::onEmailType,
                onPasswordType = component::onPasswordType,
                onRetryPasswordType = component::onRetryPasswordType,
                onSignInClick = component::onSignInClick,
                onSignUpClick = component::onSignUpClick
            )
        }
    )
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationContent(
    state: RegistrationState,
    onFirstNameType: (String) -> Unit,
    onSurnameType: (String) -> Unit,
    onPatronymicType: (String) -> Unit,
    onEmailType: (String) -> Unit,
    onPasswordType: (String) -> Unit,
    onRetryPasswordType: (String) -> Unit,
    onSignUpClick: () -> Unit,
    onSignInClick: () -> Unit
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(Modifier.padding(MaterialTheme.spacing.normal)) {
            Icon(Icons.Outlined.Build, null)
            Spacer(Modifier.size(16.dp))
            Text("Регистрация в разработке")
        }
        OutlinedTextField(
            value = state.firstName,
            onValueChange = { onFirstNameType(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Имя") },
            singleLine = true,
            supportingText = {}
        )

        OutlinedTextField(
            value = state.surname,
            onValueChange = { onSurnameType(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Фамилия") },
            singleLine = true,
            supportingText = {}
        )

        OutlinedTextField(
            value = state.patronymic,
            onValueChange = { onPatronymicType(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Отчество") },
            singleLine = true,
            supportingText = {}
        )

        OutlinedTextField(
            value = state.email,
            onValueChange = { onEmailType(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Почта") },
            singleLine = true,
            supportingText = {}
        )

        var passwordVisible by remember { mutableStateOf(false) }
        OutlinedTextField(
            value = state.password,
            onValueChange = { onPasswordType(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Пароль") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            },
            supportingText = {}
        )

        OutlinedTextField(
            value = state.retryPassword,
            onValueChange = { onRetryPasswordType(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Повторите пароль") },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility
                else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) "Hide password" else "Show password"

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, description)
                }
            },
            supportingText = {}
        )

        val registerOrLogin: @Composable () -> Unit = {
            FilledTonalButton(onClick = { onSignUpClick() }) {
                Text("Зарегистрироваться")
            }
            Spacer(Modifier.size(MaterialTheme.spacing.normal))
            Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
                Text("Уже есть аккаунт? ")
                ClickableText(
                    text = AnnotatedString("Авторизуйтесь!"),
                    onClick = { onSignInClick() },
                    style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary),
                )
            }
        }
        if (calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Compact) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                registerOrLogin()
            }
        } else {
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.normal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                registerOrLogin()
            }
        }


    }
}