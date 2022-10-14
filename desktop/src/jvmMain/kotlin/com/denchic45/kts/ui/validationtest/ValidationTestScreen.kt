package com.denchic45.kts.ui.validationtest

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.TextField
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.DefaultComponentContext
import com.arkivanov.essenty.lifecycle.LifecycleRegistry

@Composable
fun ValidationTestScreen() {

    println("ValidationTestScreen")

    val component = ValidationTestComponent(DefaultComponentContext(LifecycleRegistry()))

    ValidationTestBody(component)
}

@Composable
private fun ValidationTestBody(component: ValidationTestComponent) {

    val input by component.field.collectAsState()
    println("component input: ${component.field.value}")

    val errorMessage by component.fieldError.collectAsState()

    Column() {
        println("Input: $input")
        TextField(input, onValueChange = {
            println("onValueChange: $it")
            component.onTextChange(it)
        }, isError = errorMessage != null)
        errorMessage?.let {
            Text(
                it,
                style = MaterialTheme.typography.bodySmall
            )
        }

        val enabled by component.validateEnabled.collectAsState()

        Button(onClick = { component.onValidateClick() }, enabled = enabled) { Text("Validate") }

        val result by component.result.collectAsState()

        result?.let { Text(it) }
    }
}