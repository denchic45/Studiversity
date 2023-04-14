package com.denchic45.kts.ui.fab

import androidx.compose.runtime.Immutable
import com.denchic45.kts.ui.UiIcon
import com.denchic45.kts.ui.UiText

@Immutable
data class FabState(
    val icon: UiIcon,
    val text: UiText = UiText.StringText(""),
    val visible: Boolean = true,
    val type: FabType = FabType.Default,

    val onClick: () -> Unit = {},
)

enum class FabType { Default, Extended, Large, Small }

enum class FabColor { Primary, Secondary, Tertiary}