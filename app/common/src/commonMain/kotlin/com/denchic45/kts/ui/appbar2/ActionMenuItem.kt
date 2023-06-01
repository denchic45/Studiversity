package com.denchic45.kts.ui.appbar2

import com.denchic45.kts.ui.UiIcon
import com.denchic45.kts.ui.UiText

data class ActionMenuItem2(
    val icon: UiIcon,
    val title: UiText? = null,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
)

data class DropdownMenuItem2(
    val icon: UiIcon? = null,
    val title: UiText,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
)