package com.denchic45.kts.ui.coursetopics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
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
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.EmptyAppBar
import com.denchic45.kts.ui.periodeditor.TransparentTextField
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse

@Composable
fun CourseTopicsScreen(component: CourseTopicsComponent, appBarInteractor: AppBarInteractor) {
    component.lifecycle.doOnStart { appBarInteractor.set(EmptyAppBar) }
    val topicsResource by component.topics.collectAsState()
    CourseTopicsContent(
        topicsResource,
        onTopicRename = component::onTopicRename,
        onTopicRemove = component::onTopicRemove
    )
}

@Composable
private fun CourseTopicsContent(
    topicsResource: Resource<List<TopicResponse>>,
    onTopicRename: (Int, String) -> Unit,
    onTopicRemove: (Int) -> Unit
) {
    topicsResource.onSuccess { topics ->
        LazyColumn {
            itemsIndexed(topics, key = { _, item -> item.id }) { index, item ->
                var isEdit by remember { mutableStateOf(false) }
                if (isEdit) {
                    TopicItemUI(item, onEditClick = { isEdit = true })
                } else {
                    EditingTopicItemUI(
                        name = item.name,
                        onRename = { newName ->
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
private fun TopicItemUI(response: TopicResponse, onEditClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(text = response.name)
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
private fun EditingTopicItemUI(
    name: String,
    onRename: (String) -> Unit,
    onRemove: () -> Unit
) {
    var text by remember { mutableStateOf(name) }
    ListItem(
        headlineContent = {
            TransparentTextField(
                value = name,
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
            IconButton(onClick = { onRename(text) }) {
                Icon(
                    imageVector = Icons.Outlined.Done,
                    contentDescription = ""
                )
            }
        }
    )
}