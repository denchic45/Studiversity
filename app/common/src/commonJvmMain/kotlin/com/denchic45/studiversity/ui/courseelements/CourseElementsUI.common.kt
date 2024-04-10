package com.denchic45.studiversity.ui.courseelements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.api.course.element.model.CourseElementResponse
import com.denchic45.studiversity.api.course.element.model.CourseElementType
import com.denchic45.studiversity.api.course.element.model.CourseMaterial
import com.denchic45.studiversity.api.course.element.model.CourseWork
import com.denchic45.studiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.studiversity.domain.resource.ClientError
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.util.toString
import io.ktor.http.*
import java.util.*

@Composable
fun CourseElementListItem(item: CourseElementResponse, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(height = 64.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(),
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
                imageVector = when (item.details) {
                    is CourseWork -> Icons.Outlined.Assignment
                    CourseMaterial -> Icons.Outlined.MenuBook
                },
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                item.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            when (val details = item.details) {
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

@Composable
fun CourseElementsContent(
    elementsResource: Resource<List<Pair<CourseTopicResponse?, List<CourseElementResponse>>>>,
    onElementClick: (elementId: UUID, type: CourseElementType) -> Unit,
) {
    ResourceContent(
        resource = elementsResource,
        onError = {
            when (it) {
                is ClientError -> {
                    if (it.response.code == HttpStatusCode.Forbidden.value) {
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
                }

                else -> {
                    IconTitleBox(
                        icon = {
                            Icon(
                                Icons.Outlined.Error,
                                "unknown error",
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
                            item = it,
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
                        imageVector = Icons.Outlined.Assignment,
                        contentDescription = "empty",
                        modifier = Modifier.size(78.dp),
                        tint = MaterialTheme.colorScheme.surfaceVariant,
                    )
                },
                title = { Text(text = "Пусто") }
            )
        }
    }
}