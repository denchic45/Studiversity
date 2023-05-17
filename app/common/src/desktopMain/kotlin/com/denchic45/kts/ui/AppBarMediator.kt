package com.denchic45.kts.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.denchic45.kts.ui.appbar.AppBarInteractor

class AppBarMediator {
    var title by mutableStateOf("")
    var content by mutableStateOf<(@Composable RowScope.() -> Unit)?>(null)
}

val LocalAppBarMediator = staticCompositionLocalOf<AppBarMediator> { error("Nothing AppBarMediator") }