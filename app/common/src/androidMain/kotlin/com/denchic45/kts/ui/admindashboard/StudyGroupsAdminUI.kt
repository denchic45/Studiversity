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
import com.denchic45.kts.ui.appbar2.hideAppBar
import com.denchic45.kts.ui.layout.AdaptiveMasterSidebarLayout
import com.denchic45.kts.ui.search.SearchScreen
import com.denchic45.kts.ui.search.StudyGroupListItem
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorScreen
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse

@Composable
fun StudyGroupsAdminScreen(component: StudyGroupsAdminComponent) {

    val childSidebar by component.childSidebar.subscribeAsState()

    AdaptiveMasterSidebarLayout(
        masterContent = { StudyGroupsAdminMainScreen(component) },
        detailContent = childSidebar.overlay?.let {
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
                keyItem = StudyGroupResponse::id,
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