package com.denchic45.kts.ui.layout

import androidx.compose.runtime.Composable
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
    AdaptiveContent(
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
    AdaptiveContent(
        compactContent = { CompactMasterDetailLayout(masterContent, detailContent) },
        mediumDrawContent = { MediumMasterDetailLayout(masterContent, detailContent) },
        expandedDrawContent = { MediumMasterDetailLayout(masterContent, detailContent) }
    )
}

@Composable
fun AdaptiveContent(
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