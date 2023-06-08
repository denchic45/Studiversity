package com.denchic45.kts.ui.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import com.denchic45.kts.WindowWidthSizeClass
import com.denchic45.kts.ui.theme.calculateWindowSizeClass
import com.denchic45.kts.ui.theme.spacing


@Composable
fun AuthLayout(
    imageContent: @Composable () -> Unit,
    title: String,
    description: String? = null,
    content: @Composable () -> Unit
) {
    val windowSize = calculateWindowSizeClass()

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Column {
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.weight(1f))
                HeaderContent(imageContent, title, description)
            }
            Spacer(Modifier.height(MaterialTheme.spacing.small))
            Column(Modifier.weight(1f)) {
                content()
            }
        }

        else -> Row {
            Box(
                Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                HeaderContent(imageContent, title, description)
            }
            Spacer(Modifier.height(MaterialTheme.spacing.small))
            Box(Modifier.weight(1f)) {
                content()
            }
        }
    }
}

@Composable
fun AuthHeaderIcon(painter: Painter) {
    Image(painter, null, Modifier.size(84.dp))
}

@Composable
private fun HeaderContent(
    headerImagePainter: @Composable () -> Unit,
    title: String,
    description: String?
) {
    Column {
        headerImagePainter()
        Text(title, style = MaterialTheme.typography.titleLarge)
        description?.let {
            Text(description)
        }
    }
}