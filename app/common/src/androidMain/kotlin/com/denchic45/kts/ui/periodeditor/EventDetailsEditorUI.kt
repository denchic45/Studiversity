package com.denchic45.kts.ui.periodeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun EventDetailsEditorScreen(component: EventDetailsEditorComponent) {
    val details = remember { component.details }
    EventDetailsEditorContent(
        details = details,
        onNameType = component::onNameType
    )
}

@Composable
fun EventDetailsEditorContent(
    details: EditingPeriodDetails.Event,
    onNameType: (String) -> Unit,
) {
    Column {
        OutlinedTextField(
            value = details.name,
            onValueChange = onNameType,
            label = { Text(text = "Название") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = details.color,
            onValueChange = onNameType,
            label = { Text(text = "Цвет") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = details.color,
            onValueChange = onNameType,
            label = { Text(text = "Иконка") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}