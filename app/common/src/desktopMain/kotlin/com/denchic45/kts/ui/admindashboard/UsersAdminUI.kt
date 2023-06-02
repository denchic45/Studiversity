package com.denchic45.kts.ui.admindashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.kts.ui.model.UserItem

@Composable
fun UsersAdminScreen(component: UsersAdminComponent) {
    AdminSearchScreen(component, UserItem::id) {
        Text(it.title)
    }
}