package com.denchic45.kts.ui.confirm

import androidx.compose.runtime.Immutable
import com.denchic45.kts.ui.UiText

@Immutable
data class ConfirmState(
    val title: UiText,
    val text: UiText? = null
)