package com.denchic45.kts.ui.admindashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.kts.ui.chooser.SearchScreen
import com.denchic45.kts.ui.chooser.UserListItem
import com.denchic45.kts.ui.model.UserItem

@Composable
fun UsersAdminScreen(component: UsersAdminComponent) {
    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = component::onAddClick) {
                Icon(Icons.Default.Add, "add course")
            }
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            SearchScreen(
                component = component.chooserComponent,
                keyItem = UserItem::id
            ) { item ->
                UserListItem(item)
            }
        }
    }
}