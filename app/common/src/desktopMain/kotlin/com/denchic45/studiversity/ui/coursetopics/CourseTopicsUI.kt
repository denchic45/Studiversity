package com.denchic45.studiversity.ui.coursetopics

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.domain.resource.onSuccess

@Composable
fun CourseTopicsDialog(component: CourseTopicsComponent, onDismissRequest: () -> Unit) {
    val topics by component.topics.collectAsState()

    AlertDialog(onDismissRequest = onDismissRequest,
        title = { Text("Изменить темы курса") },
        text = {
            topics.onSuccess { topics ->
                CourseTopicsContent(
                    topics = topics,
                    onTopicCreate = component::onTopicAdd,
                    onTopicRename = component::onTopicRename,
                    onTopicRemove = component::onTopicRemove
                )
            }
        },
        confirmButton = { TextButton(onClick = onDismissRequest) { Text("ОК") } })
}