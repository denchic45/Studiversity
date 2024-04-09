package com.denchic45.studiversity.ui.coursetopics

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAnimatedAppBarState
import com.denchic45.studiversity.ui.uiTextOf

@Composable
fun CourseTopicsScreen(component: CourseTopicsComponent) {
    updateAnimatedAppBarState(AppBarContent(uiTextOf("Изменить темы")))
    val topicsResource by component.topics.collectAsState()
    ResourceContent(topicsResource) { topics ->
        CourseTopicsContent(
            topics = topics,
            onTopicCreate = component::onTopicAdd,
            onTopicRename = component::onTopicRename,
            onTopicRemove = component::onTopicRemove
        )
    }
}