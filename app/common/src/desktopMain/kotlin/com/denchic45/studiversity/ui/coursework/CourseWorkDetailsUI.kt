package com.denchic45.studiversity.ui.coursework

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.ui.ExpandableDropdownMenu
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.coursework.details.CourseWorkDetailsComponent
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.theme.CommonAppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.UUID


@Composable
fun CourseWorkDetailsScreen(
    component: CourseWorkDetailsComponent,
    allowEdit: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val workResource by component.courseWork.collectAsState()
    val attachmentsResource by component.attachments.collectAsState()

//    val attachmentViewer by lazy {
//        AttachmentViewer(context.findActivity()) {
//            Toast.makeText(
//                context,
//                "Невозможно открыть файл на данном устройстве",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }

//    component.openAttachment.collectWithLifecycle {
//        attachmentViewer.openAttachment(it)
//    }

    CourseWorkDetailsContent(
        workResource = workResource,
        attachmentsResource = attachmentsResource,
        onAttachmentClick = component::onAttachmentClick,
        allowEdit = allowEdit,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    )
}

@Composable
fun CourseWorkDetailsContent(
    workResource: Resource<CourseWorkResponse>,
    allowEdit: Boolean,
    attachmentsResource: Resource<List<AttachmentItem>>,
    onAttachmentClick: (item: AttachmentItem) -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    ResourceContent(workResource) {
        Column {
            CourseWorkHeader(
                name = it.name,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt,
                dueDate = it.dueDate,
                dueTime = it.dueTime,
                allowEdit = allowEdit,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick
            )
        }
    }
}


@Composable
fun CourseWorkHeader(
    name: String,
    createdAt: LocalDateTime,
    updatedAt: LocalDateTime?,
    dueDate: LocalDate?,
    dueTime: LocalTime?,
    allowEdit: Boolean,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Column(Modifier.padding(MaterialTheme.spacing.normal)) {
        Row(modifier = Modifier.height(56.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = name, style = MaterialTheme.typography.headlineMedium)
            if (allowEdit) {
                Spacer(Modifier.weight(1f))
                var expanded by remember { mutableStateOf(false) }
                ExpandableDropdownMenu(expanded, onExpandedChange = { expanded = it }) {
                    DropdownMenuItem(
                        text = { Text("Изменить") },
                        onClick = onEditClick
                    )
                    DropdownMenuItem(
                        text = { Text("Удалить") },
                        onClick = onDeleteClick
                    )
                }
            }
        }
        Row(
            modifier = Modifier.padding(vertical = MaterialTheme.spacing.small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                updatedAt?.let { "(Обновлено: ${it.toString(DateTimePatterns.dd_MMM)})" }
                    ?: createdAt.toString(DateTimePatterns.dd_MMM)
            )
            dueDate?.let { date ->
                val dateText = date.toString(DateTimePatterns.dd_MMM)
                val timeText = dueTime?.toString(DateTimePatterns.HH_mm)
                Spacer(Modifier.weight(1f))
                Text(
                    text = "Срок сдачи: ${dateText + timeText?.let { ", $it" }}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview
@Composable
fun CourseWorkDetailsPreview() {
    CommonAppTheme {
        CourseWorkDetailsContent(
            workResource = resourceOf(
                CourseWorkResponse(
                    id = UUID.randomUUID(),
                    name = "Example Work",
                    description = "",
                    dueDate = LocalDate.now().plusDays(2),
                    dueTime = LocalTime.of(15, 30),
                    workType = CourseWorkType.ASSIGNMENT,
                    maxGrade = 5,
                    courseId = UUID.randomUUID(),
                    topicId = null,
                    submitAfterDueDate = null,
                    createdAt = LocalDateTime.now(),
                    updatedAt = null
                )
            ),
            allowEdit = true,
            attachmentsResource = resourceOf(),
            onAttachmentClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}

