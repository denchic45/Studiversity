package com.denchic45.studiversity.ui.coursework

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.attachment.AttachmentListItem
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.coursework.details.CourseWorkDetailsComponent
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.util.AttachmentViewer
import com.denchic45.studiversity.util.collectWithLifecycle
import com.denchic45.studiversity.util.findActivity
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import okio.Path.Companion.toPath
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

@Composable
fun CourseWorkDetailsScreen(component: CourseWorkDetailsComponent) {
    val workResource by component.courseWork.collectAsState()
    val attachmentsComponent = remember { component.attachmentsComponent }
    val context = LocalContext.current

    CourseWorkDetailsContent(
        workResource = workResource,
        attachmentContent = {
            val attachmentViewer by lazy {
                AttachmentViewer(context.findActivity()) {
                    Toast.makeText(
                        context,
                        "Невозможно открыть файл на данном устройстве",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            attachmentsComponent.openAttachment.collectWithLifecycle(action = attachmentViewer::openAttachment)
            val attachments by attachmentsComponent.attachments.collectAsState()
            CourseWorkAttachments(attachments, attachmentsComponent::onAttachmentClick)
        },
    )
}

@Composable
private fun CourseWorkDetailsContent(
    workResource: Resource<CourseWorkResponse>,
    attachmentContent: @Composable () -> Unit
) {
    workResource.onSuccess { work ->
        Column(
            Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.normal)
        ) {
            CourseWorkHeader(work.name)
            HorizontalDivider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
            work.dueDate?.let { dueDate ->
                Row {
                    AssistChip(
                        onClick = { /*TODO*/ },
                        label = {
                            Text(dueDate.let { date ->
                                val pattern = DateTimeFormatter.ofPattern("dd MMM")
                                date.format(pattern)
                            } + " " + work.dueTime?.let { time ->
                                val pattern = DateTimeFormatter.ofPattern("HH:mm")
                                time.format(pattern)
                            },
                                color = if (work.late) MaterialTheme.colorScheme.error else Color.Unspecified
                            )
                        }, leadingIcon = {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = "deadline",
                                tint = if (work.late) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                            )
                        })
                }
            }
            work.description?.let {
                Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
                Text(text = it, style = MaterialTheme.typography.bodyLarge)
            }
            attachmentContent()
        }
    }
}

@Composable
fun CourseWorkAttachments(
    attachmentsResource: Resource<List<AttachmentItem>>,
    onAttachmentClick: (item: AttachmentItem) -> Unit
) {
    attachmentsResource.onSuccess { attachments ->
        if (attachments.isNotEmpty()) {
            HeaderItem(name = "Прикрепленные файлы", horizontalPadding = 0.dp)
            LazyRow {
                items(attachments, key = { it.attachmentId }) {
                    AttachmentListItem(item = it, onClick = { onAttachmentClick(it) })
                }
            }
        }
    }
}

@Composable
fun CourseWorkHeader(name: String, commentsCount: Int = 0) {
    Column {
        Text(text = name, style = MaterialTheme.typography.titleLarge)
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Comment,
                contentDescription = "comments",
                modifier = Modifier.padding(MaterialTheme.spacing.normal)
            )
            Text(text = "$commentsCount комментариев")
        }
    }
}

@Preview
@Composable
fun CourseWorkDetailsPreview() {
    AppTheme {
        Surface {
            CourseWorkDetailsContent(
                Resource.Success(
                    CourseWorkResponse(
                        UUID.randomUUID(), name = "Задание к демо-экзамену",
                        description = """
        Задание сегодняшних уроков:\n
        1. Изучаете новые слова и словосочетания
        (их произношение)\n
        2. Читаете и переводите текст (быть готовыми
        отчитать его на оценку на следующем очном
        уроке)\n
        3. Выполняете задания после текста
        (таблица и перевод с русского на английский,
        используя новую лексику и текст) письменно
        в тетрадь. Принимаю фото ТОЛЬКО тетради,
        никаких печатных вариантов.
    """.trimIndent(),
                        dueDate = LocalDate.now().plusDays(3),
                        dueTime = LocalTime.now().plusHours(2).plusMinutes(30),
                        workType = CourseWorkType.ASSIGNMENT,
                        maxGrade = 5,
                        UUID.randomUUID(),
                        null,
                        false,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                ),
                attachmentContent = {
                    CourseWorkAttachments(Resource.Success(
                        listOf(
                            AttachmentItem.FileAttachmentItem(
                                attachmentId = UUID.randomUUID(),
                                name = "image",
                                previewUrl = null,
                                path = "image.png".toPath(),
                                state = FileState.Preview
                            )
                        )
                    ), onAttachmentClick = {})
                }
            )
        }
    }
}