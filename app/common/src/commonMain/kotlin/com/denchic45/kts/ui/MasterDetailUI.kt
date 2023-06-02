package com.denchic45.kts.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.WindowWidthSizeClass
import com.denchic45.kts.ui.theme.calculateWindowSizeClass


typealias MasterDetailDrawContent = @Composable (
    mainContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
) -> Unit

@Composable
fun MasterDetailContent(
    mainContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
) {
    ResponsiveContent(
        compactContent = { CompactMasterDetailLayout(mainContent, detailContent) },
        mediumDrawContent = { MasterDetailSidebarLayout(mainContent, detailContent) },
        expandedDrawContent = { MasterDetailSidebarLayout(mainContent, detailContent) }
    )
}

@Composable
fun MasterDetailSidebarContent(
    masterContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
) {
    ResponsiveContent(
        compactContent = { CompactMasterDetailLayout(masterContent, detailContent) },
        mediumDrawContent = { MediumMasterDetailLayout(masterContent, detailContent) },
        expandedDrawContent = { MediumMasterDetailLayout(masterContent, detailContent) }
    )
}

@Composable
fun ResponsiveContent(
    compactContent: @Composable () -> Unit,
    mediumDrawContent: @Composable () -> Unit,
    expandedDrawContent: @Composable () -> Unit,
) {
    val sizeClass = calculateWindowSizeClass()
    when (sizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            compactContent()
        }

        WindowWidthSizeClass.Medium -> {
            mediumDrawContent()
        }

        WindowWidthSizeClass.Expanded -> {
            expandedDrawContent()
        }
    }
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