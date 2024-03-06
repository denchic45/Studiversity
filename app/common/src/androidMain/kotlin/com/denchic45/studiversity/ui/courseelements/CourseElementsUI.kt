package com.denchic45.studiversity.ui.courseelements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.resource.Forbidden
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.element.model.CourseMaterial
import com.denchic45.stuiversity.api.course.element.model.CourseWork
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate
import java.time.LocalTime
import java.util.*


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CourseElementsScreen(component: CourseElementsComponent) {
    val elementsResource by component.elements.collectAsState()
    val refreshing by component.refreshing.collectAsState()

    val refreshState = rememberPullRefreshState(refreshing, component::onRefresh)

    Box(modifier = Modifier.pullRefresh(refreshState)) {
        CourseElementsContent(
            elementsResource = elementsResource,
            onElementClick = component::onItemClick,
        )
        if (refreshState.progress > 0)
            PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
    }
}

@Composable
fun CourseElementsContent(
    elementsResource: Resource<List<Pair<CourseTopicResponse?, List<CourseElementResponse>>>>,
    onElementClick: (elementId: UUID, type: CourseElementType) -> Unit,
) {
    ResourceContent(
        resource = elementsResource,
        onError = {
            when (it) {
                Forbidden -> {
                    IconTitleBox(
                        icon = {
                            Icon(
                                Icons.Outlined.Lock,
                                "forbidden",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        },
                        title = { Text("У вас нет доступа") }
                    )
                }

                else -> {
                    IconTitleBox(
                        icon = {
                            Icon(
                                Icons.Outlined.Error,
                                "forbidden",
                                modifier = Modifier.size(78.dp)
                            )
                        },
                        title = {
                            Text("Неизвестная ошибка")
                        }
                    )
                }
            }
        },
    ) { elementsByTopics ->
        if (elementsByTopics.flatMap { topicElements -> topicElements.second }.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = MaterialTheme.spacing.normal)
            ) {
                elementsByTopics.forEach { (topic, elements) ->
                    topic?.let {
                        item(key = it.id) {
                            Column {
                                Text(
                                    text = it.name,
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(MaterialTheme.spacing.normal)
                                )
                                Divider(
                                    modifier = Modifier.padding(end = MaterialTheme.spacing.normal),
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }

                    items(elements, key = { it.id }) {
                        CourseElementListItem(
                            response = it,
                            onClick = {
                                onElementClick(
                                    it.id, when (it.details) {
                                        is CourseWork -> CourseElementType.WORK
                                        CourseMaterial -> CourseElementType.MATERIAL
                                    } // todo использовать напрямую CourseElementType, а не details
                                )
                            })
                    }
                }
            }
        } else {
            IconTitleBox(
                icon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Assignment,
                        contentDescription = "empty",
                        modifier = Modifier.size(78.dp),
                        tint = MaterialTheme.colorScheme.surfaceVariant,
                    )
                },
                title = { Text(text = "Пусто") })
        }
    }
}

@Composable
fun CourseElementListItem(response: CourseElementResponse, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(height = 64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(bounded = true),
                onClick = { onClick() })
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = when (response.details) {
                    is CourseWork -> Icons.AutoMirrored.Outlined.Assignment
                    CourseMaterial -> Icons.AutoMirrored.Outlined.MenuBook
                },
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                response.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            when (val details = response.details) {
                is CourseWork -> details.dueDate?.let {
                    Text(
                        text = it.toString("dd MMM"), style = MaterialTheme.typography.bodySmall,
                        color = if (details.late) MaterialTheme.colorScheme.error else Color.Unspecified
                    )
                }

                is CourseMaterial -> {}
            }
        }
    }
}

@Preview
@Composable
fun CourseElementPreview() {
    AppTheme {
        CourseElementListItem(
            CourseElementResponse(
                id = UUID.randomUUID(),
                courseId = UUID.randomUUID(),
                name = "Задание №25",
                description = "Сегодня",
                null,
                0,
                CourseWork(LocalDate.now(), LocalTime.now(), CourseWorkType.ASSIGNMENT, 5, null)
            )
        ) {}
    }
}