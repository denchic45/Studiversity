package com.denchic45.studiversity.ui.appbar

import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.denchic45.studiversity.ui.ActionMenuItem
import com.denchic45.studiversity.ui.DropdownMenuItem
import com.denchic45.studiversity.ui.UiText

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