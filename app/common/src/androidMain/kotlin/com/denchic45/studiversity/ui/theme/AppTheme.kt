package com.denchic45.studiversity.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.denchic45.studiversity.ui.appbar.LocalAppBarState
import com.denchic45.studiversity.ui.appbar.rememberAppBarState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val rememberedAppBarState = rememberAppBarState()
    CommonAppTheme(useDarkTheme) {
        CompositionLocalProvider(LocalAppBarState provides rememberedAppBarState) {
            Surface {
                content()
            }
        }
    }
}