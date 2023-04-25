package com.denchic45.kts.ui.coursework.details

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlarmAdd
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import okio.Path.Companion.toPath
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseWorkDetailsScreen(component: CourseWorkDetailsComponent) {
    val workResource by component.courseWork.collectAsState()
    val attachmentsResource by component.attachments.collectAsState()
    CourseWorkDetailsContent(workResource, attachmentsResource)
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun CourseWorkDetailsContent(
    workResource: Resource<CourseWorkResponse>,
    attachmentsResource: Resource<List<AttachmentItem>>,
) {
    workResource.onSuccess { work ->
        Column(
            Modifier
                .fillMaxWidth()
                .padding(MaterialTheme.spacing.normal)
        ) {
            CourseWorkHeader(work.name)
            Divider()
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
            work.dueDate?.let { dueDate ->
                Row {
                    AssistChip(onClick = { /*TODO*/ }, label = {
                        Text(dueDate.let { date ->
                            val pattern = DateTimeFormatter.ofPattern("dd MMM")
                            date.format(pattern)
                        } + work.dueTime?.let { time ->
                            val pattern = DateTimeFormatter.ofPattern("HH:mm")
                            time.format(pattern)
                        })
                    }, leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.AlarmAdd,
                            contentDescription = "alarm"
                        )
                    })
                }
            }
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
            work.description?.let {
                Text(text = it)
            }
            attachmentsResource.onSuccess { attachments ->
                HeaderItemUI(name = "Прикрепления")
                LazyRow {
                    items(attachments, key = { it.attachmentId ?: Any() }) {
                        AttachmentItemUI(item = it)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseWorkHeader(name: String, commentsCount: Int = 0) {
    Column {
        Text(text = name, style = MaterialTheme.typography.titleLarge)
        Row(
            Modifier.height(56.dp),
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

@Composable
fun AttachmentItemUI(item: AttachmentItem, onRemove: (() -> Unit)? = null) {
    AssistChip(onClick = { /*TODO*/ },
        label = {
            Text(
                text = when (item) {
                    is AttachmentItem.FileAttachmentItem -> item.name
                    is AttachmentItem.LinkAttachmentItem -> item.url
                }
            )
        },
        leadingIcon = {
            Icon(imageVector = Icons.Outlined.AttachFile, contentDescription = "attachment")
        },
        trailingIcon = {
            onRemove?.let {
                IconButton(onClick = { it() }) {
                    Icon(imageVector = Icons.Outlined.Close, contentDescription = "attachment")
                }
            }
        }
    )
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
                        null,
                        false,
                        createdAt = LocalDateTime.now(),
                        updatedAt = LocalDateTime.now()
                    )
                ),
                Resource.Success(
                    listOf(
                        AttachmentItem.FileAttachmentItem(
                            attachmentId = UUID.randomUUID(),
                            name = "image",
                            previewUrl = null,
                            path = "image.png".toPath(),
                            state = FileState.Preview
                        )
                    )
                )
            )
        }
    }
}