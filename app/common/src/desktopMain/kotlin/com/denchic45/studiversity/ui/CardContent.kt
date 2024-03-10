package com.denchic45.studiversity.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.theme.spacing

@Deprecated("Use BlockContent")
@Composable
fun CardContent(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxHeight()
            .padding(end = MaterialTheme.spacing.normal, bottom = MaterialTheme.spacing.normal),
//            .padding(MaterialTheme.spacing.medium),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        content()
    }
}

@Composable
fun SurfaceContent(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Surface(
        tonalElevation = (-1).dp,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxSize()
            .padding(end = MaterialTheme.spacing.normal, bottom = MaterialTheme.spacing.normal),
    ) {
        CardContent {
            content()
        }
    }
}

@Composable
fun BlockContent(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
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