package com.denchic45.kts.ui.appbar2

import com.denchic45.kts.ui.UiText
import com.denchic45.kts.ui.uiTextOf


data class AppBarContent(
    val title: UiText = uiTextOf(""),
    val actionItems: List<ActionMenuItem2> = emptyList(),
    val dropdownItems: List<DropdownMenuItem2> = emptyList(),
)