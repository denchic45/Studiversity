package com.denchic45.studiversity.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.*

@Stable
class AppBarMediator {
    var title by mutableStateOf("")
    var content by mutableStateOf<(@Composable RowScope.() -> Unit)?>(null)
}

val LocalAppBarMediator = staticCompositionLocalOf<AppBarMediator> { error("Nothing AppBarMediator") }