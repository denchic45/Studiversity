package com.denchic45.studiversity.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.autofill.AutofillNode
import androidx.compose.ui.autofill.AutofillType
import androidx.compose.ui.composed
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalAutofill
import androidx.compose.ui.platform.LocalAutofillTree
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.denchic45.studiversity.WindowWidthSizeClass
import com.denchic45.studiversity.ui.theme.calculateWindowSizeClass
import com.denchic45.studiversity.ui.theme.spacing


@Composable
fun LoginScreen(component: LoginComponent) {
    AuthLayout(
        imageContent = { AuthHeaderIcon(rememberVectorPainter(Icons.Outlined.Email)) },
        title = "Вход через почту",
        content = {
            LoginContent(
                component::onEmailType,
                component::onPasswordType,
                { component.onLoginClick() }
            ) { component.onRegisterClick() }
        }
    )
}

@Composable
fun LoginContent(
    onEmailType: (String) -> Unit,
    onPasswordType: (String) -> Unit,
    onSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
) {
    Column(Modifier.fillMaxWidth()) {
        LoginTextFields(onEmailType, onPasswordType)
//        Spacer(Modifier.height(MaterialTheme.spacing.normal))
        val loginOrRegister: @Composable () -> Unit = {
            Button(onSignInClick) { Text("Войти") }
            Spacer(Modifier.size(MaterialTheme.spacing.normal))
            Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
                Text("В первый раз? ")
                ClickableText(
                    text = AnnotatedString("Зарегистрируйтесь!"),
                    onClick = { onSignUpClick() },
                    style = LocalTextStyle.current.copy(color = MaterialTheme.colorScheme.primary),
                )
            }
        }
        if (calculateWindowSizeClass().widthSizeClass == WindowWidthSizeClass.Compact) {
            Column(
                Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.medium),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                loginOrRegister()
            }
        } else {
            Row(
                Modifier.fillMaxWidth().padding(top = MaterialTheme.spacing.normal),
                verticalAlignment = Alignment.CenterVertically
            ) {
                loginOrRegister()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun LoginTextFields(
    onEmailType: (String) -> Unit,
    onPasswordType: (String) -> Unit,
) {
    var email by remember { mutableStateOf("") }
    OutlinedTextField(
        value = email,
        onValueChange = {
            email = it
            onEmailType(it)
        },
        modifier = Modifier.fillMaxWidth().autofill(
            autofillTypes = listOf(AutofillType.EmailAddress),
            onFill = {
                email = it
                onEmailType(it)
            },
        ),
        leadingIcon = { Icon(Icons.Outlined.Email, null) },
        placeholder = { Text("Почта") },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        singleLine = true
    )
    Spacer(Modifier.height(MaterialTheme.spacing.normal))

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = password,
        onValueChange = {
            password = it
            onPasswordType(it)
        },
        modifier = Modifier.fillMaxWidth().autofill(
            autofillTypes = listOf(AutofillType.Password),
            onFill = {
                password = it
                onPasswordType(it)
            },
        ),
        leadingIcon = { Icon(Icons.Outlined.Lock, null) },
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

@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.autofill(
    autofillTypes: List<AutofillType>,
    onFill: ((String) -> Unit),
) = composed {
    val autofill = LocalAutofill.current
    val autofillNode = AutofillNode(onFill = onFill, autofillTypes = autofillTypes)
    LocalAutofillTree.current += autofillNode
    this.onGloballyPositioned {
        autofillNode.boundingBox = it.boundsInWindow()
    }.onFocusChanged { focusState ->
        autofill?.run {
            if (focusState.isFocused && autofillNode.boundingBox != null) {
                requestAutofillForNode(autofillNode)
            } else {
                cancelAutofillForNode(autofillNode)
            }
        }
    }
}