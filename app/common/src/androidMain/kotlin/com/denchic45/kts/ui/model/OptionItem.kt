package com.denchic45.kts.ui.model

import java.util.*

data class OptionItem(
    override val id: UUID,
    val title: UiText,
    val icon: UiImage? = null,
    val color: UiColor? = null,
    val enable: Boolean = true,
) : UiModel {

    fun hasIcon(): Boolean = icon != null
}