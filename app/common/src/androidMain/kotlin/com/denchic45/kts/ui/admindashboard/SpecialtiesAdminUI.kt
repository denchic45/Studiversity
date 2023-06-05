package com.denchic45.kts.ui.admindashboard

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
import com.denchic45.kts.ui.ExpandableDropdownMenu
import com.denchic45.kts.ui.search.SearchScreen
import com.denchic45.kts.ui.search.SpecialtyListItem
import com.denchic45.kts.ui.specialtyeditor.SpecialtyEditorDialog
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse

@Composable
fun SpecialtiesAdminScreen(component: SpecialtiesAdminComponent) {
    val childOverlay by component.childOverlay.subscribeAsState()

    SpecialtiesAdminMainScreen(component)

    childOverlay.overlay?.let {
        SpecialtiesAdminDetailScreen(it.instance)
    }
}

@Composable
private fun SpecialtiesAdminDetailScreen(child: SpecialtiesAdminComponent.Child) {
    when (child) {
        is SpecialtiesAdminComponent.Child.SpecialtyEditor -> {
            SpecialtyEditorDialog(child.component)
        }
    }
}

@Composable
private fun SpecialtiesAdminMainScreen(component: SpecialtiesAdminComponent) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = component::onAddClick,
                text = { Text(text = "Создать специальность") },
                icon = { Icon(Icons.Default.Add, "add specialty") }
            )
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            SearchScreen(
                component = component.chooserComponent,
                keyItem = SpecialtyResponse::id,
                placeholder = "Поиск специальностей"
            ) { item ->
                SpecialtyListItem(item, trailingContent = {
                    var expanded by remember { mutableStateOf(false) }
                    ExpandableDropdownMenu(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {
                        DropdownMenuItem(
                            text = { Text(text = "Редактировать") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "edit specialty"
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
                                    contentDescription = "delete specialty"
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