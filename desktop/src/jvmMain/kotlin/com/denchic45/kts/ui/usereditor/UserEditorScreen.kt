package com.denchic45.kts.ui.usereditor

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.components.HeaderItem
import com.denchic45.kts.ui.components.Spinner
import com.denchic45.kts.ui.theme.TextM2
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap

@Composable
fun UserEditorScreen(component: UserEditorComponent) {
    Column(Modifier.padding(vertical = 48.dp)) {
        AsyncImage(
            load = { loadImageBitmap("") },
            painterFor = { BitmapPainter(it) },
            null
        ) {
            Box(Modifier.size(40.dp))
        }
        HeaderItem("Личные данные")
        val firstName by component.firstNameField.collectAsState()
        TextField(
            value = firstName,
            onValueChange = component::onFirstNameType,
            label = { TextM2("Имя") },
            singleLine = true
        )
        val surname by component.surnameField.collectAsState()
        TextField(
            value = surname,
            onValueChange = component::onSurnameType,
            Modifier.padding(top = 24.dp),
            label = { TextM2("Фамилия") },
            singleLine = true
        )
        val patronymic by component.patronymicField.collectAsState()
        TextField(
            value = patronymic,
            onValueChange = component::onPatronymicType,
            Modifier.padding(top = 24.dp),
            label = { TextM2("Отчество") },
            placeholder = { TextM2("Отчество (необязательно)") },
            singleLine = true
        )

        val gender by component.genderField.collectAsState()
        var expanded by remember { mutableStateOf(false) }
        Spinner(
            items = component.genders,
            onActionClick = component::onGenderSelect,
            expanded = expanded,
            onExpandedChange = { expanded = it },
            placeholder = "Пол",
            activeAction = gender
        )
        HeaderItem("Вход в аккаунт")
        val email by component.emailField.collectAsState()
        TextField(
            value = email,
            onValueChange = component::onEmailType,
            Modifier.padding(top = 24.dp),
            label = { TextM2("Почта") },
            leadingIcon = { Icon(painterResource("ic_email".toDrawablePath()), null) }
        )
        val password by component.passwordField.collectAsState()
        var passwordVisible by remember { mutableStateOf(false) }
        TextField(
            value = password,
            onValueChange = component::onPasswordType,
            Modifier.padding(top = 24.dp),
            label = { TextM2("Пароль") },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff,
                        if (passwordVisible) "Hide password" else "Show password"
                    )
                }
            },
            singleLine = true
        )
    }
}