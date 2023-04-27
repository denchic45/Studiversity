package com.denchic45.kts.ui

data class DropdownMenuItem(
    val id: String,
    val title: UiText,
    val icon: UiIcon? = null,
//    val color: UiColor? = null,
    val enable: Boolean = true,
    val onClick: () -> Unit = {}
) {

    fun hasIcon(): Boolean = icon != null
}