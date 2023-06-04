package com.denchic45.kts.ui.admindashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
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
import com.denchic45.kts.ui.SubjectEditorDialog
import com.denchic45.kts.ui.search.SearchScreen
import com.denchic45.kts.ui.search.SubjectListItem
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse

@Composable
fun SubjectsAdminScreen(component: SubjectsAdminComponent) {
    val childOverlay by component.childOverlay.subscribeAsState()
    SubjectsAdminMainScreen(component)
    childOverlay.overlay?.let {
        SubjectsAdminDetailScreen(it.instance)
    }
}

@Composable
private fun SubjectsAdminDetailScreen(child: SubjectsAdminComponent.Child) {
    when (child) {
        is SubjectsAdminComponent.Child.SubjectEditor -> {
            SubjectEditorDialog(child.component)
        }
    }
}

@Composable
private fun SubjectsAdminMainScreen(component: SubjectsAdminComponent) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = component::onAddClick,
                text = { Text(text = "Создать предмет") },
                icon = { Icon(Icons.Default.Add, "add subject") }
            )
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            SearchScreen(
                component = component.chooserComponent,
                keyItem = SubjectResponse::id,
                placeholder = "Поиск предметов"
            ) { item ->
                SubjectListItem(item = item, trailingContent = {
                    var expanded by remember { mutableStateOf(false) }
                    ExpandableDropdownMenu(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {
                        DropdownMenuItem(
                            text = { Text(text = "Редактировать") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "edit subject"
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
                                    contentDescription = "delete subject"
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