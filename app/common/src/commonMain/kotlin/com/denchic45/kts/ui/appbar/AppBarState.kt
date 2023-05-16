package com.denchic45.kts.ui.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.denchic45.kts.ui.ActionMenuItem
import com.denchic45.kts.ui.DropdownMenuItem
import com.denchic45.kts.ui.UiText
import com.denchic45.kts.ui.theme.Spacing

@Immutable
data class AppBarState(
    val title: UiText = UiText.StringText(""),
    val actions: List<ActionMenuItem> = emptyList(),
    val actionsUI: (@Composable RowScope.() -> Unit)? = null,
    val dropdown: List<DropdownMenuItem> = emptyList(),
    val visible:Boolean = true,

    val onActionMenuItemClick: (ActionMenuItem) -> Unit = {},
    val onDropdownMenuItemClick: (DropdownMenuItem) -> Unit = {}
)

val EmptyAppBar = AppBarState()


val LocalAppBarInteractor = staticCompositionLocalOf<AppBarInteractor> { error("Nothing AppBarInteractor") }