package com.denchic45.kts.ui.periodeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.DoorFront
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.timetable.model.PeriodMember
import com.denchic45.stuiversity.api.timetable.model.StudyGroupName
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import java.time.LocalDate
import java.util.UUID

@Composable
fun PeriodEditorScreen(component: PeriodEditorComponent) {
    PeriodEditorContent(
        state = component.state,
        onCourseChoose = component::onCourseChoose,
        onAddMemberClick = component::onAddMemberClick,
        onRemoveMemberClick = component::onRemoveMemberClick
    )
}

@Composable
fun PeriodEditorContent(
    state: EditingPeriod,
    onCourseChoose: () -> Unit,
    onAddMemberClick: () -> Unit,
    onRemoveMemberClick: (PeriodMember) -> Unit
) {
    Column {
        when (val details = state.details) {
            is EditingPeriodDetails.Event -> {

            }

            is EditingPeriodDetails.Lesson -> {
                Box(modifier = Modifier.clickable { onCourseChoose() }) {
                    details.course?.let {
                        ListItem(
                            headlineContent = { Text(text = it.name) },
                            leadingContent = {
                                it.subject?.let {
                                    AsyncImage(
                                        model = it.iconUrl,
                                        contentDescription = "subject icon",
                                        modifier = Modifier.size(40.dp)
                                    )
                                } ?: Icon(
                                    imageVector = Icons.Outlined.School,
                                    contentDescription = ""
                                )
                            }
                        )
                    } ?: ListItem(
                        headlineContent = { Text(text = "Выбрать предмет") },
                        leadingContent = {
                            Icon(
                                imageVector = Icons.Outlined.School,
                                contentDescription = ""
                            )
                        }
                    )
                }
                HeaderItemUI("Преподаватели")
                Row(
                    Modifier
                        .padding(horizontal = MaterialTheme.spacing.normal)
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    state.members.forEach {
                        AssistChip(
                            onClick = { /*TODO*/ },
                            modifier = Modifier.size(18.dp),
                            label = { it.fullName },
                            leadingIcon = {
                                AsyncImage(
                                    model = it.avatarUrl,
                                    contentDescription = "user avatar"
                                )
                            },
                            trailingIcon = {
                                IconButton(onClick = { onRemoveMemberClick(it) }) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "remove member"
                                    )
                                }
                            })
                    }
                    AssistChip(onClick = onAddMemberClick,
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "add member"
                            )
                        },
                        label = { Text(text = "Добавить") })
                }
            }
        }
        Divider(Modifier.padding(vertical = MaterialTheme.spacing.small))
        ListItem(
            headlineContent = { Text(text = state.date.toString(DateTimePatterns.d_MMMM)) },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.CalendarMonth,
                    contentDescription = "date"
                )
            }
        )
        ListItem(
            headlineContent = { Text(text = "Порядковый номер") },
            leadingContent = {
                Box(modifier = Modifier.size(24.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = state.order.toString(),
                        style = MaterialTheme.typography.titleLarge
                    )
                }
            }
        )
        ListItem(
            headlineContent = {
                TransparentTextField(
                    value = state.room?.name ?: "",
                    onValueChange = {},
                    placeholder = "Аудитория"
                )
            },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.DoorFront,
                    contentDescription = "date"
                )
            }
        )
    }
}

@Composable
private fun TransparentTextField(
    value: String,
    onValueChange: (String) -> Unit,
    singleLine: Boolean = true,
    placeholder: String? = null
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = singleLine,
        decorationBox = { innerTextField ->
            Box(modifier = Modifier) {
                if (value.isEmpty() && placeholder != null) {
                    Text(
                        text = placeholder,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }
                innerTextField()
            }
        }
    )
}

@Preview
@Composable
fun PeriodEditorPreview() {
    AppTheme {
        PeriodEditorContent(
            state = EditingPeriod().apply {
                date = LocalDate.now()
                order = 1
                group = StudyGroupName(UUID.randomUUID(), "ПКС")
                (details as EditingPeriodDetails.Lesson).course = CourseResponse(
                    id = UUID.randomUUID(),
                    name = "Математика ПКС",
                    subject = SubjectResponse(
                        id = UUID.randomUUID(),
                        name = "Математика",
                        shortname = "Матем",
                        iconUrl = ""
                    ),
                    archived = false
                )
            },
            onCourseChoose = {},
            onAddMemberClick = {},
            onRemoveMemberClick = {}
        )
    }
}