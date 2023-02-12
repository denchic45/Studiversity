package com.denchic45.kts.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SupportingText(
    text: String,
    isError: Boolean = false
) = Text(
    text,
    Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp),
    if (isError) MaterialTheme.colorScheme.error else Color.Unspecified,
    style = MaterialTheme.typography.bodySmall
)