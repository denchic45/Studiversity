package com.denchic45.studiversity.ui

data class ActionMenuItem(
    val id: String,
    val icon: UiIcon,
    val title: UiText? = null,
    val enabled: Boolean = true,
    val onClick: () -> Unit,
)