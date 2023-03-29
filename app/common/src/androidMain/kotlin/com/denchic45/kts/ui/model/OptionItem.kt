package com.denchic45.kts.ui.model

data class OptionItem(
    val id: String,
    val title: UiText,
    val icon: UiImage? = null,
    val color: UiColor? = null,
    val enable: Boolean = true,
) {

    fun hasIcon(): Boolean = icon != null
}