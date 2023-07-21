package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.denchic45.studiversity.ui.appbar.hideAppBar
import com.denchic45.studiversity.ui.component.ExpandableDropdownMenu
import com.denchic45.studiversity.ui.roomeditor.RoomEditorDialog
import com.denchic45.studiversity.ui.search.RoomListItem
import com.denchic45.studiversity.ui.search.SearchScreen
import com.denchic45.stuiversity.api.room.model.RoomResponse

@Composable
fun RoomsAdminScreen(component: RoomsAdminComponent) {

    val childOverlay by component.childOverlay.subscribeAsState()

    RoomsAdminMainScreen(component)

    childOverlay.overlay?.let {
        RoomsAdminDetailScreen(it.instance)
    }
}

@Composable
private fun RoomsAdminDetailScreen(child: RoomsAdminComponent.Child) {
    when (child) {
        is RoomsAdminComponent.Child.RoomEditor -> {
            RoomEditorDialog(child.component)
        }
    }
}

@Composable
private fun RoomsAdminMainScreen(component: RoomsAdminComponent) {
    hideAppBar()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = component::onAddClick,
                text = { Text(text = "Создать аудиторию") },
                icon = { Icon(Icons.Default.Add, "add room") }
            )
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            SearchScreen(
                component = component.chooserComponent,
                keyItem = RoomResponse::id,
                placeholder = "Поиск аудиторий"
            ) { item ->
                RoomListItem(item, trailingContent = {
                    var expanded by remember { mutableStateOf(false) }
                    ExpandableDropdownMenu(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {
                        DropdownMenuItem(
                            text = { Text(text = "Редактировать") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "edit Room"
                                )
                            },
                            onClick = {
                                expanded = false
                                component.onEditClick(item.id)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Удалить") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Delete,
                                    contentDescription = "delete Room"
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