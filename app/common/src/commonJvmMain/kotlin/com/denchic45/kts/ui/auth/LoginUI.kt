package com.denchic45.kts.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.denchic45.kts.WindowWidthSizeClass
import com.denchic45.kts.ui.theme.calculateWindowSizeClass


@Composable
fun LoginScreen(component: LoginComponent) {
    AuthLayout(
        imageContent = { AuthHeaderIcon(rememberVectorPainter(Icons.Outlined.Email)) },
        title = "Вход через почту",
        content = {
            LoginContent(
                component::onEmailType,
                component::onPasswordType,
                component::onLoginClick,
                component::onRegisterClick
            )
        }
    )
}

@Composable
fun LoginContent(
    onEmailType: (String) -> Unit,
    onPasswordType: (String) -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    Column {
        LoginTextFields(onEmailType, onPasswordType)
        val register: @Composable () -> Unit = {
            Text("или ")
            ClickableText(
                text = AnnotatedString("зарегистрируйтесь"),
                onClick = { onSignUpClick() },
                style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary),
            )

        }
        if (calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Compact) {
            Column {
                Button(onSignInClick) { Text("Войти") }
                register()
            }
        } else {
            Row {
                Button(onSignInClick) { Text("Войти") }
                register()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTextFields(
    onEmailType: (String) -> Unit,
    onPasswordType: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    TextField(
        value = email,
        onValueChange = {
            email = it
            onEmailType(it)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Почта") },
        singleLine = true
    )
    Spacer(Modifier.height(16.dp))

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    TextField(
        value = password,
        onValueChange = {
            password = it
            onPasswordType(it)
        },
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Пароль") },
        singleLine = true,
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible) Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            // Please provide localized description for accessibility services
            val description = if (passwordVisible) "Hide password" else "Show password"

            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                Icon(imageVector = image, description)
            }
        }
    )
}