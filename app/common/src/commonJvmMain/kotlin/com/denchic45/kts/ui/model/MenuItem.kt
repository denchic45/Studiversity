package com.denchic45.kts.ui.model

interface MenuAction {
    val title: String
    val iconName: String?
}

data class MenuItem<T : MenuAction>(
    val action: T,
    val enabled: Boolean = true,
) : MenuAction by action
