package com.denchic45.studiversity.ui.courseelements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.api.course.element.model.CourseElementResponse
import com.denchic45.studiversity.api.course.element.model.CourseElementType
import com.denchic45.studiversity.api.course.element.model.CourseMaterial
import com.denchic45.studiversity.api.course.element.model.CourseWork
import com.denchic45.studiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.studiversity.api.course.work.model.CourseWorkType
import com.denchic45.studiversity.domain.resource.ClientError
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import io.ktor.http.*
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
                                HorizontalDivider(
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
                        imageVector = Icons.AutoMirrored.Outlined.Assignment,
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