package com.denchic45.kts.ui.theme


import androidx.compose.runtime.Composable
import com.denchic45.kts.WindowSizeClass

@Composable
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val size = LocalWindowState.current.size
    return WindowSizeClass.calculateFromSize(size)
}
