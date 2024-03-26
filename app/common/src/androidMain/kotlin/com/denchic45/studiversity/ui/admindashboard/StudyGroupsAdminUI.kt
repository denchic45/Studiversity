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
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.ui.appbar.hideAppBar
import com.denchic45.studiversity.ui.component.ExpandableDropdownMenu
import com.denchic45.studiversity.ui.layout.AdaptiveMasterSidebarLayout
import com.denchic45.studiversity.ui.search.SearchScreen
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorScreen
import com.denchic45.studiversity.ui.studygroups.StudyGroupListItem
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse

@Composable
fun StudyGroupsAdminScreen(component: StudyGroupsAdminComponent) {

    val childSidebar by component.childSlot.subscribeAsState()

    AdaptiveMasterSidebarLayout(
        masterContent = { StudyGroupsAdminMainScreen(component) },
        detailContent = childSidebar.child?.let {
            { StudyGroupsAdminDetailScreen(it.instance) }
        }
    )
}

@Composable
private fun StudyGroupsAdminDetailScreen(child: StudyGroupsAdminComponent.Child) {
    when (child) {
        is StudyGroupsAdminComponent.Child.StudyGroupEditor -> {
            StudyGroupEditorScreen(child.component)
        }
    }
}

@Composable
private fun StudyGroupsAdminMainScreen(component: StudyGroupsAdminComponent) {
    hideAppBar()
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = component::onAddClick,
                text = { Text(text = "Создать группу") },
                icon = { Icon(Icons.Default.Add, "add study group") }
            )
        }) { paddingValues ->
        Box(Modifier.padding(paddingValues)) {
            SearchScreen(
                component = component.chooserComponent,
                keyItem = StudyGroupItem::id,
                placeholder = "Поиск групп"
            ) { item ->
                StudyGroupListItem(item = item, trailingContent = {
                    var expanded by remember { mutableStateOf(false) }
                    ExpandableDropdownMenu(
                        expanded = expanded,
                        onExpandedChange = { expanded = it }) {
                        DropdownMenuItem(
                            text = { Text(text = "Редактировать") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Outlined.Edit,
                                    contentDescription = "edit study group"
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
                                    contentDescription = "delete study group"
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