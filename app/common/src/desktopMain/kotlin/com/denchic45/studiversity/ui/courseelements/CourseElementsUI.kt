package com.denchic45.studiversity.ui.courseelements

import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.main.CustomAppBar

@Composable
fun CourseElementsScreen(component: CourseElementsComponent, allowEdit: Boolean) {
    val elementsResource by component.elements.collectAsState()
    var addElement by remember { mutableStateOf(false) }
    var filtersExpanded by remember { mutableStateOf(false) }

    BlockContent {
        CustomAppBar(title = {
            if (allowEdit)
                ExtendedFloatingActionButton(
                    text = { Text("Новый элемент") },
                    icon = { Icon(Icons.Outlined.Add, null) },
                    onClick = { addElement = true })
        },
            actions = {
                IconButton(onClick = { filtersExpanded = true }) {
                    Icon(Icons.Outlined.FilterList, null)
                }
                DropdownMenu(
                    expanded = addElement,
                    onDismissRequest = { addElement = false },
                    offset = DpOffset(16.dp, 0.dp),
                    modifier = Modifier.widthIn(min = 164.dp)
                ) {
                    DropdownMenuItem(
                        onClick = {
                            addElement = false
                            component.onAddWorkClick()
                        },
                        text = { Text("Задание") },
                        leadingIcon = { Icon(Icons.Outlined.Assignment, null) }
                    )
                    DropdownMenuItem(
                        onClick = {
                            addElement = false
                            component.onAddMaterialClick()
                        },
                        text = { Text("Материал") },
                        leadingIcon = { Icon(Icons.Outlined.Book, null) }
                    )
                }
            })
        CourseElementsContent(
            elementsResource = elementsResource,
            onElementClick = component::onItemClick,
        )
    }
}