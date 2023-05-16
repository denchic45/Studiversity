package com.denchic45.kts.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TabIndicator(
    modifier: Modifier = Modifier,
    color: Color = LocalContentColor.current,
) {
    Box(modifier.fillMaxWidth().height(4.dp)
        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)).background(color = color))
}

@Composable
fun Tab(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: String? = null,
    icon: @Composable (() -> Unit)? = null,
) {
    androidx.compose.material3.Tab(selected,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        text = { text?.let { Text(it, style = MaterialTheme.typography.titleSmall) } },
        icon = icon,
        selectedContentColor = MaterialTheme.colorScheme.primary,
        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )
}