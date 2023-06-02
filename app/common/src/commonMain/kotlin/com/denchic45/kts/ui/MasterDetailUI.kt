package com.denchic45.kts.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.WindowSizeClass
import com.denchic45.kts.WindowWidthSizeClass
import com.denchic45.kts.ui.theme.calculateWindowSizeClass


typealias MasterDetailDrawContent = @Composable (
    mainContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit
) -> Unit

@Composable
fun MasterDetailContent(
    mainContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit
) {
    ResponsiveContent(
        compactContent = { CompactMasterDetailScreen(mainContent, detailContent) },
        mediumDrawContent = { MasterDetailSidebarScreen(mainContent, detailContent) },
        expandedDrawContent = { MasterDetailSidebarScreen(mainContent, detailContent) }
    )
}

@Composable
fun MasterDetailSidebarContent(
    masterContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit
) {
    ResponsiveContent(
        compactContent = { CompactMasterDetailScreen(masterContent, detailContent) },
        mediumDrawContent = { MediumMasterDetailScreen(masterContent, detailContent) },
        expandedDrawContent = { MediumMasterDetailScreen(masterContent, detailContent) }
    )
}

@Composable
fun ResponsiveContent(
    compactContent: @Composable (WindowSizeClass) -> Unit,
    mediumDrawContent: @Composable (WindowSizeClass) -> Unit,
    expandedDrawContent: @Composable (WindowSizeClass) -> Unit,
) {
    val sizeClass = calculateWindowSizeClass()
    when (sizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            compactContent(sizeClass)
        }

        WindowWidthSizeClass.Medium -> {
            mediumDrawContent(sizeClass)
        }

        WindowWidthSizeClass.Expanded -> {
            expandedDrawContent(sizeClass)
        }
    }
}

@Composable
fun CompactMasterDetailScreen(
    mainContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
) {
    Box() {
        mainContent()
        detailContent()
    }
}

@Composable
fun MediumMasterDetailScreen(
    masterContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
) {
    Row {
        Box(Modifier.weight(0.4f)) {
            masterContent()
        }
        Box(Modifier.weight(0.6f)) {
            detailContent()
        }
    }
}

@Composable
fun MasterDetailSidebarScreen(
    masterContent: @Composable () -> Unit,
    detailContent: @Composable () -> Unit,
) {
    Row {
        Box(Modifier.weight(1f)) {
            masterContent()
        }
        Box(Modifier.width(500.dp)) {
            detailContent()
        }
    }
}