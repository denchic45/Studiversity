package com.denchic45.kts.ui.admindashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Archive
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
import com.denchic45.kts.ui.ExpandableDropdownMenu
import com.denchic45.kts.ui.search.CourseListItem
import com.denchic45.kts.ui.search.SearchScreen
import com.denchic45.stuiversity.api.course.model.CourseResponse

@Composable
fun CoursesAdminScreen(component: CoursesAdminComponent) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = component::onAddClick,
                text = { Text(text = "Создать курс") },
                icon = { Icon(Icons.Default.Add, "add course") })
        }) {
        Box(Modifier.padding(it)) {
            SearchScreen(
                component = component.chooserComponent,
                keyItem = CourseResponse::id
            ) { item ->
                CourseListItem(item = item, trailingContent = {
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
                        if (item.archived) {
                            DropdownMenuItem(
                                text = { Text(text = "Удалить") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = "delete course"
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    component.onRemoveClick(item.id)
                                }
                            )
                        } else {
                            DropdownMenuItem(
                                text = { Text(text = "Архивировать") },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Archive,
                                        contentDescription = "archive course"
                                    )
                                },
                                onClick = {
                                    expanded = false
                                    component.onArchiveClick(item.id)
                                }
                            )
                        }
                    }
                })
            }
        }
    }
}