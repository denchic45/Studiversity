package com.denchic45.kts.ui.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun LoginScreen(component: LoginComponent) {
    val state = remember { component.state }

}

@Composable
fun LoginContent(state: LoginState) {

}