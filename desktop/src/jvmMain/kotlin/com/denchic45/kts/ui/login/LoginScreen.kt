package com.denchic45.kts.ui.login

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Preview
@Composable
fun LoginScreen(loginComponent: LoginComponent) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface {
        Column {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Почта") }
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") }
            )
            Button(
                onClick = {}
            ) {}
        }
    }
}