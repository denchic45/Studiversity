package com.denchic45.studiversity.ui.appbar

import com.denchic45.studiversity.ui.UiIcon
import com.denchic45.studiversity.ui.UiText

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