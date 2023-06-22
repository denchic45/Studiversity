package com.denchic45.studiversity.ui.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun HeaderItemUI(name: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.height(56.dp)
//            .padding(horizontal = MaterialTheme.spacing.normal)
        ,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(name, style = MaterialTheme.typography.titleMedium)
    }
}
