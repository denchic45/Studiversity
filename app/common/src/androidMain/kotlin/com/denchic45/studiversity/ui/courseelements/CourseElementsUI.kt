package com.denchic45.studiversity.ui.courseelements

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.AssignmentTurnedIn
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.CircularProgressIndicator
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
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onLoading
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.CourseElementType
import com.denchic45.stuiversity.api.course.element.model.CourseMaterial
import com.denchic45.stuiversity.api.course.element.model.CourseWork
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


@Composable
fun CourseElementsScreen(component: CourseElementsComponent) {
    val elementsResource by component.elements.collectAsState()

    CourseElementsContent(
        elementsResource = elementsResource,
        onElementClick = component::onItemClick,
    )
}

@Composable
fun CourseElementsContent(
    elementsResource: Resource<List<Pair<TopicResponse?, List<CourseElementResponse>>>>,
    onElementClick: (elementId: UUID, type: CourseElementType) -> Unit,
) {
    elementsResource.onSuccess {
        LazyColumn(contentPadding = PaddingValues(vertical = MaterialTheme.spacing.normal)) {
            it.forEach { (topic, elements) ->
                topic?.let {
                    item(key = { it.id }) { }
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
    }.onLoading {
        Box(
            modifier = Modifier.fillMaxHeight(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
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