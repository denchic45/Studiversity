package com.denchic45.kts.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.appbar.LocalAppBarInteractor
import com.denchic45.kts.ui.uiTextOf

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CommonAppTheme(useDarkTheme, AndroidTypography) {
       CompositionLocalProvider(LocalAppBarInteractor provides AppBarInteractor()) {
           Surface {
               content()
           }
       }
    }
}