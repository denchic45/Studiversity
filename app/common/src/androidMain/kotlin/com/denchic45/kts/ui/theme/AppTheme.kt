package com.denchic45.kts.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CommonAppTheme(useDarkTheme, AndroidTypography) {
        Surface {
            content()
        }
    }
}