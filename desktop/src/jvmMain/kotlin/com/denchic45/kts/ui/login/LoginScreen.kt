package com.denchic45.kts.ui.login

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Surface
import androidx.compose.material.TextField
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun LoginScreen(loginComponent: LoginComponent) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Surface(modifier = Modifier.padding(16.dp).size(450.dp)) {
        Column(verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {
            TextField(
                modifier = Modifier.padding(8.dp),
                value = email,
                onValueChange = { email = it },
                label = { Text("Почта") },
            )
            TextField(
                modifier = Modifier.padding(8.dp),
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") }
            )
            Button(
                onClick = {loginComponent.onLoginClick("denchic860@gmail.com", "Den141819!kts")}
            ) {
                Text("Войти")
            }
        }
    }
}