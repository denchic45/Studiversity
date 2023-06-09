package com.denchic45.kts.ui.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.denchic45.kts.WindowWidthSizeClass
import com.denchic45.kts.ui.theme.calculateWindowSizeClass
import com.denchic45.kts.ui.theme.spacing


@Composable
fun AuthLayout(
    imageContent: @Composable () -> Unit,
    title: String,
    description: String? = null,
    content: @Composable () -> Unit,
) {
    val windowSize = calculateWindowSizeClass()

    when (windowSize.widthSizeClass) {
        WindowWidthSizeClass.Compact -> Column(Modifier.padding(MaterialTheme.spacing.normal)) {
            Column(Modifier.weight(0.8f), horizontalAlignment = Alignment.CenterHorizontally) {
                Spacer(Modifier.weight(1f))
                HeaderContent(imageContent, title, description)
            }
            Spacer(Modifier.height(MaterialTheme.spacing.normal))
            Column(
                Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(Modifier.widthIn(max = 360.dp)) {
                    content()
                }
            }
        }

        else -> Row(Modifier.padding(MaterialTheme.spacing.normal)) {
            Column(Modifier.weight(1f).fillMaxSize()) {
                Box(Modifier.weight(1f), contentAlignment = Alignment.BottomCenter) {
                    HeaderContent(imageContent, title, description)
                }
                Spacer(Modifier.weight(0.8f))
            }

            Spacer(Modifier.height(MaterialTheme.spacing.small))
            Box(Modifier.weight(1f).fillMaxSize(), contentAlignment = Alignment.Center) {
                Box(Modifier.widthIn(max = 420.dp)) {
                    content()
                }
            }
        }
    }
}

@Composable
fun AuthHeaderIcon(painter: Painter) {
    Icon(
        painter = painter,
        contentDescription = null,
        modifier = Modifier.size(84.dp),
        tint = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun HeaderContent(
    headerImagePainter: @Composable () -> Unit,
    title: String,
    description: String?,
) {
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        headerImagePainter()
        Text(title, style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
        description?.let {
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
        }
    }
}