package com.denchic45.studiversity.ui.coursetopics

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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.periodeditor.TransparentTextField
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse

@Composable
fun CourseTopicsScreen(component: CourseTopicsComponent) {
    updateAnimatedAppBarState(AppBarContent(uiTextOf("Изменить темы")))
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
        var isCreating by remember { mutableStateOf(false) }
        var indexOfEditing by remember { mutableStateOf(-1) }
        LazyColumn {
            item {
                CreatingTopicItem(
                    enabled = isCreating,
                    onEnableChange = {
                        isCreating = it
                        indexOfEditing = -1
                    },
                    onSave = onTopicCreate
                )
            }
            itemsIndexed(topics, key = { _, item -> item.id }) { index, item ->
                EditableTopicItem(
                    item = item,
                    isEdit = indexOfEditing == index,
                    onEditClick = { indexOfEditing = index },
                    onSave = { newName ->
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
private fun CreatingTopicItem(
    enabled: Boolean,
    onEnableChange: (Boolean) -> Unit,
    onSave: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    ListItem(
        headlineContent = {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(enabled) {
                if (enabled) {
                    focusRequester.requestFocus()
                } else {
                    text = ""
                    focusManager.clearFocus()
                }
            }
            TransparentTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = "Создать тему",
//                readOnly = !enabled,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        onEnableChange(it.isFocused)
                    }
            )

        },
        leadingContent = {
            IconButton(onClick = {
                onEnableChange(!enabled)
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
                        onEnableChange(false)
                    }) {
                    Icon(
                        imageVector = Icons.Outlined.Done,
                        contentDescription = "",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        modifier = Modifier.clickable { onEnableChange(true) }
    )
}

@Composable
private fun EditableTopicItem(
    item: CourseTopicResponse,
    isEdit: Boolean,
    onEditClick: () -> Unit,
    onSave: (String) -> Unit,
    onRemoveClick: () -> Unit
) {
    var text by remember { mutableStateOf(item.name) }
    val focusManager = LocalFocusManager.current
    ListItem(
        headlineContent = {
            val focusRequester = remember { FocusRequester() }
            LaunchedEffect(isEdit) {
                if (isEdit) {
                    focusRequester.requestFocus()
                } else {
                    focusManager.clearFocus()
                    if (item.name != text)
                        onSave(text)
                }
            }
            TransparentTextField(
                value = text,
                onValueChange = { text = it },
//                readOnly = !isEdit,
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .onFocusChanged {
                        if (it.isFocused) {
                            onEditClick()
                        }
                    }
            )
        },
        leadingContent = {
            if (isEdit) {
                IconButton(onClick = onRemoveClick, modifier = Modifier) {
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
                onClick = { if (isEdit) onSave(text) else onEditClick() }
            ) {
                Icon(
                    imageVector = if (isEdit) Icons.Outlined.Done else Icons.Outlined.Edit,
                    contentDescription = ""
                )
            }
        },
        modifier = Modifier.clickable(onClick = onEditClick)
    )
}