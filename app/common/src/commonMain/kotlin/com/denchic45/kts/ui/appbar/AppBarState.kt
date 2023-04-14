package com.denchic45.kts.ui.appbar

import androidx.compose.runtime.Immutable
import com.denchic45.kts.ui.ActionMenuItem
import com.denchic45.kts.ui.DropdownMenuItem
import com.denchic45.kts.ui.UiText

@Immutable
data class AppBarState(
    val title: UiText = UiText.StringText(""),
    val actions: List<ActionMenuItem> = emptyList(),
    val dropdown: List<DropdownMenuItem> = emptyList(),
    val visible:Boolean = true,

    val onActionMenuItemClick: (ActionMenuItem) -> Unit = {},
    val onDropdownMenuItemClick: (DropdownMenuItem) -> Unit = {}
)