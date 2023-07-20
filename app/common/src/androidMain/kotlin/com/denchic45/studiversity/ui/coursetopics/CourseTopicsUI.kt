package com.denchic45.studiversity.ui.coursetopics

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.periodeditor.TransparentTextField
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse

@Composable
fun CourseTopicsScreen(component: CourseTopicsComponent) {
    updateAnimatedAppBarState(AppBarContent())
    val topicsResource by component.topics.collectAsState()
    CourseTopicsContent(
        items = topicsResource,
        onTopicCreate = component::onTopicAdd,
        onTopicRename = component::onTopicRename,
        onTopicRemove = component::onTopicRemove
    )
}

@Composable
private fun CourseTopicsContent(
    items: Resource<List<CourseTopicResponse>>,
    onTopicCreate: (String) -> Unit,
    onTopicRename: (Int, String) -> Unit,
    onTopicRemove: (Int) -> Unit
) {
    ResourceContent(items) { topics ->
        var indexOfEditing by remember { mutableStateOf(-1) }
        LazyColumn {
            item {
                CreatingTopicItem(onSave = onTopicCreate)
            }
            itemsIndexed(topics, key = { _, item -> item.id }) { index, item ->
                EditableTopicItem(
                    item = item,
                    isEdit = indexOfEditing == index,
                    onEditClick = { indexOfEditing = index },
                    onSaveClick = { newName ->
                        if (item.name != newName)
                            onTopicRename(index, newName)
                        indexOfEditing = -1
                    },
                    onRemoveClick = { onTopicRemove(index) }
                )
            }
        }
    }
}

@Composable
private fun CreatingTopicItem(onSave: (String) -> Unit) {
    var enabled by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }

    ListItem(
        headlineContent = {
            val focusRequester = remember { FocusRequester() }
            if (enabled) {
                LaunchedEffect(Unit) {
                    focusRequester.requestFocus()
                }
            }
            TransparentTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = "Создать тему",
                readOnly = !enabled,
                modifier = Modifier.focusRequester(focusRequester)
            )

        },
        leadingContent = {
            IconButton(onClick = {
                if (enabled) text = ""
                enabled = !enabled
            }) {
                Icon(
                    imageVector = if (enabled) Icons.Default.Close else Icons.Default.Add,
                    contentDescription = if (enabled) "Cancel creating topic" else "Create topic"
                )
            }
        },
        trailingContent = {
            if (enabled) {
                IconButton(
                    enabled = text.isNotEmpty(),
                    onClick = {
                        onSave(text)
                        enabled = false
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = ""
                    )
                }
            }
        },
        modifier = Modifier.clickable { enabled = true }
    )
}

@Composable
private fun EditableTopicItem(
    item: CourseTopicResponse,
    isEdit: Boolean,
    onEditClick: () -> Unit,
    onSaveClick: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    var text by remember { mutableStateOf(item.name) }
    ListItem(
        headlineContent = {
            TransparentTextField(
                value = text,
                onValueChange = { text = it },
                readOnly = !isEdit,
                modifier = Modifier.clickable(onClick = onEditClick)
            )
        },
        leadingContent = {
            if (isEdit) {
                IconButton(onClick = onRemoveClick, modifier = Modifier.background(Color.Cyan)) {
                    Icon(
                        imageVector = Icons.Outlined.Delete,
                        contentDescription = ""
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .minimumInteractiveComponentSize()
                        .size(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = ""
                    )
                }
            }
        },
        trailingContent = {
            IconButton(
                enabled = text.isNotEmpty(),
                onClick = { if (isEdit) onSaveClick(text) else onEditClick() }
            ) {
                Icon(
                    imageVector = if (isEdit) Icons.Outlined.Done else Icons.Outlined.Edit,
                    contentDescription = ""
                )
            }
        }
    )
}