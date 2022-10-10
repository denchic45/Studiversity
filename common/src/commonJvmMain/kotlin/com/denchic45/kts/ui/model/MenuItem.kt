package com.denchic45.kts.ui.model

interface MenuItem {
    val title: String
}

interface MenuItemWithIcon : MenuItem {
    val iconName: String
}