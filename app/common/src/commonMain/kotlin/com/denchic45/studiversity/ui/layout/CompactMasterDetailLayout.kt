package com.denchic45.studiversity.ui.layout

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun AdaptiveMasterSidebarLayout(
    masterContent: @Composable () -> Unit,
    detailContent: (@Composable () -> Unit)?,
) {
    AdaptiveContent(
        compactContent = { CompactMasterDetailLayout(masterContent, detailContent) },
        mediumDrawContent = { MediumMasterDetailLayout(masterContent, detailContent) },
        expandedDrawContent = { MediumMasterDetailLayout(masterContent, detailContent) }
    )
}

@Composable
fun CompactMasterDetailLayout(
    masterContent: @Composable () -> Unit,
    detailContent: (@Composable () -> Unit)?,
) {
    Box(Modifier.fillMaxSize()) {
        detailContent?.let {
            detailContent()
        } ?: masterContent()
    }
}

@Composable
fun MediumMasterDetailLayout(
    masterContent: @Composable () -> Unit,
    detailContent: (@Composable () -> Unit)?,
) {
    Row(Modifier.fillMaxSize()) {
        Box(Modifier.weight(0.4f)) {
            masterContent()
        }
        detailContent?.let {
            Box(Modifier.weight(0.6f)) {
                detailContent()
            }
        }
    }
}

@Composable
fun MasterDetailSidebarLayout(
    masterContent: @Composable () -> Unit,
    detailContent: (@Composable () -> Unit)?,
) {
    Row(Modifier.fillMaxSize()) {
        Box(Modifier.weight(1f)) {
            masterContent()
        }
        detailContent?.let {
            Box(Modifier.width(500.dp)) {
                detailContent()
            }
        }
    }
}