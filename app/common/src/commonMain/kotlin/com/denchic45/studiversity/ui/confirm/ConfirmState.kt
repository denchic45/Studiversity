package com.denchic45.studiversity.ui.confirm

import androidx.compose.runtime.Immutable
import com.denchic45.studiversity.ui.UiText

@Immutable
data class ConfirmState(
    val title: UiText,
    val text: UiText? = null
)