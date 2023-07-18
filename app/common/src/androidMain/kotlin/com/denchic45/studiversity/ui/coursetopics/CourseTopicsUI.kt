package com.denchic45.studiversity.ui.coursetopics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Done
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Topic
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.periodeditor.TransparentTextField
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse

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
    items: Resource<List<TopicResponse>>,
    onTopicCreate: (String) -> Unit,
    onTopicRename: (Int, String) -> Unit,
    onTopicRemove: (Int) -> Unit
) {
    ResourceContent(items) { topics ->
        LazyColumn {
            item {
                CreatingTopicItem(onSave = onTopicCreate)
            }
            itemsIndexed(topics, key = { _, item -> item.id }) { index, item ->
                var isEdit by remember { mutableStateOf(false) }
                if (isEdit) {
                    TopicItem(item, onEditClick = { isEdit = true })
                } else {
                    EditingTopicItem(
                        name = item.name,
                        onSave = { newName ->
                            if (item.name != newName)
                                onTopicRename(index, newName)
                            isEdit = false
                        },
                        onRemove = { onTopicRemove(index) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TopicItem(item: TopicResponse, onEditClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(text = item.name)
        },
        leadingContent = {
            Icon(
                imageVector = Icons.Outlined.Topic,
                contentDescription = ""
            )
        },
        trailingContent = {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = ""
                )
            }
        },
        modifier = Modifier.clickable(onClick = onEditClick)
    )
}

@Composable
private fun CreatingTopicItem(onSave: (String) -> Unit) {
    var enabled by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    ListItem(
        headlineContent = {
            if (enabled) {
                TransparentTextField(
                    value = text,
                    onValueChange = { text = it },
                    placeholder = "Создать тему"
                )
            } else {
                Text(text = "Создать тему")
            }
        },
        leadingContent = {
            IconButton(onClick = {
                enabled = false
                text = ""
            }) {
                Icon(
                    imageVector = Icons.Outlined.Close,
                    contentDescription = "Cancel create topic"
                )
            }
        },
        trailingContent = {
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
    )
}

@Composable
private fun EditingTopicItem(
    name: String,
    onSave: (String) -> Unit,
    onRemove: () -> Unit
) {
    var text by remember { mutableStateOf(name) }
    ListItem(
        headlineContent = {
            TransparentTextField(
                value = text,
                onValueChange = { text = it }
            )
        },
        leadingContent = {
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = ""
                )
            }
        },
        trailingContent = {
            IconButton(enabled = text.isNotEmpty(), onClick = { onSave(text) }) {
                Icon(
                    imageVector = Icons.Outlined.Done,
                    contentDescription = ""
                )
            }
        }
    )
}