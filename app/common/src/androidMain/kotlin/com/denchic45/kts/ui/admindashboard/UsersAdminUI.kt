package com.denchic45.kts.ui.admindashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.ui.ExpandableDropdownMenu
import com.denchic45.kts.ui.layout.AdaptiveMasterSidebarLayout
import com.denchic45.kts.ui.model.UserItem
import com.denchic45.kts.ui.profile.ProfileScreen
import com.denchic45.kts.ui.search.SearchScreen
import com.denchic45.kts.ui.search.UserListItem
import com.denchic45.kts.ui.usereditor.UserEditorScreen

@Composable
fun UsersAdminScreen(component: UsersAdminComponent) {
    val childOverlay by component.childOverlay.subscribeAsState()

    AdaptiveMasterSidebarLayout(
        masterContent = { UsersAdminMainScreen(component) },
        detailContent = childOverlay.overlay?.let {
            { UsersAdminDetailScreen(it.instance) }
        }
    )
}

@Composable
private fun UsersAdminDetailScreen(child: UsersAdminComponent.Child) {
    when (child) {
        is UsersAdminComponent.Child.Profile -> {
            ProfileScreen(child.component)
        }

        is UsersAdminComponent.Child.UserEditor -> {
            UserEditorScreen(child.component)
        }
    }
}

@Composable
private fun UsersAdminMainScreen(component: UsersAdminComponent) {
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
                UserListItem(item, trailingContent = {
                    var expanded by remember { mutableStateOf(false) }
                    ExpandableDropdownMenu(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {

                        DropdownMenuItem(
                            text = { Text(text = "Удалить") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "delete user"
                                )
                            },
                            onClick = {
                                expanded = false
                                component.onRemoveClick(item.id)
                            }
                        )
                    }
                })
            }
        }
    }
}