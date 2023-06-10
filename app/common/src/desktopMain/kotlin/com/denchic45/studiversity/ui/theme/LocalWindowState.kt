package com.denchic45.studiversity.ui.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.window.WindowState

val LocalWindowState = staticCompositionLocalOf<WindowState> {
    error("CompositionLocal LocalWindowState not present")
}