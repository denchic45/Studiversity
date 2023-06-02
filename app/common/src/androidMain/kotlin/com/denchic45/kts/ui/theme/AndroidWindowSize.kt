package com.denchic45.kts.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toComposeRect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.window.layout.WindowMetricsCalculator
import com.denchic45.kts.WindowSizeClass
import com.denchic45.kts.util.findActivity

@Composable
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val density = LocalDensity.current
    val metrics = WindowMetricsCalculator.getOrCreate()
        .computeCurrentWindowMetrics(LocalContext.current.findActivity())
    val size = with(density) { metrics.bounds.toComposeRect().size.toDpSize() }
    return WindowSizeClass.calculateFromSize(size)
}