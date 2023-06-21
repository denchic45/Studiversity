package com.denchic45.studiversity.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

@Composable
fun Scaffold(
    topBar: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column {
        topBar?.invoke()
        Surface(
            tonalElevation = (-1).dp,
            color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ) {
            content()
        }
    }
}