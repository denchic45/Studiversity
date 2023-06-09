package com.denchic45.kts.ui.theme

import androidx.compose.runtime.Composable
import com.jthemedetecor.OsThemeDetector

@Composable
actual fun isSystemInDarkTheme(): Boolean {
    return OsThemeDetector.getDetector().isDark
}