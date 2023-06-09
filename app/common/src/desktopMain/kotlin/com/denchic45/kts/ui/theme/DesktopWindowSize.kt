package com.denchic45.kts.ui.theme


import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.denchic45.kts.WindowSizeClass

@Composable
actual fun calculateWindowSizeClass(): WindowSizeClass {
    val size = LocalWindowState.current.size
    return WindowSizeClass.calculateFromSize(size)
}

@Composable
fun rememberWindowSizeClass(): WindowSizeClass {
    val current = LocalWindowState.current
    val size = current.size
    return remember(size) { WindowSizeClass.calculateFromSize(size) }
}