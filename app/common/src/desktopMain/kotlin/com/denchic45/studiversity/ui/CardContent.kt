package com.denchic45.studiversity.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.theme.spacing


@Composable
fun BlockContent(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    Surface(
        tonalElevation = (-1).dp,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.padding(end = MaterialTheme.spacing.normal, bottom = MaterialTheme.spacing.normal),
    ) {
        Card(
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            content()
        }
    }
}