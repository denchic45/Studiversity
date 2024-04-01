package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.ui.appbar.hideAppBar
import com.denchic45.studiversity.ui.component.ExpandableDropdownMenu
import com.denchic45.studiversity.ui.search.SearchScreen
import com.denchic45.studiversity.ui.subject.SubjectListItem
import com.denchic45.studiversity.ui.subjecteditor.SubjectEditorDialog

@Composable
fun SubjectsAdminScreen(component: SubjectsAdminComponent) {
    val childSlot by component.childSlot.subscribeAsState()
    SubjectsAdminMainScreen(component)
    childSlot.child?.let {
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
    hideAppBar()
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