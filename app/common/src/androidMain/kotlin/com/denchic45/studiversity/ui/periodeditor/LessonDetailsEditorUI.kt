package com.denchic45.studiversity.ui.periodeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.denchic45.studiversity.ui.component.HeaderItem
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun LessonDetailsEditorScreen(
    component: LessonDetailsEditorComponent,
    members: List<UserItem>,
    onAddMemberClick: () -> Unit,
    onRemoveMemberClick: (UserItem) -> Unit,
) {
    val details = remember { component.details }
    LessonDetailsEditorContent(
        details = details,
        members = members,
        onAddMemberClick = onAddMemberClick,
        onRemoveMemberClick = onRemoveMemberClick,
        onCourseChoose = component::onCourseChoose
    )
}

@Composable
fun LessonDetailsEditorContent(
    details: EditingPeriodDetails.Lesson,
    members: List<UserItem>,
    onAddMemberClick: () -> Unit,
    onRemoveMemberClick: (UserItem) -> Unit,
    onCourseChoose: () -> Unit,
) {
    Column {
        Box(modifier = Modifier.clickable(onClick = onCourseChoose)) {
            details.course?.let {
                ListItem(
                    headlineContent = { Text(text = it.subject?.name ?: it.name) },
                    leadingContent = {
                        Icon(
                            painter = it.subject?.let {
                                rememberAsyncImagePainter(
                                    ImageRequest.Builder(LocalContext.current)
                                        .decoderFactory(SvgDecoder.Factory())
                                        .data(it.iconUrl)
                                        .build()
                                )
                            } ?: rememberVectorPainter(Icons.Outlined.School),
                            contentDescription = "subject icon",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(40.dp)
                        )
                    },
                )
            } ?: ListItem(
                headlineContent = {
                    Text(
                        text = "Выбрать курс",
                        color = if (details.courseError) MaterialTheme.colorScheme.error
                        else Color.Unspecified
                    )
                },
                leadingContent = {
                    Icon(
                        imageVector = Icons.Outlined.School,
                        tint = MaterialTheme.colorScheme.secondary,
                        contentDescription = ""
                    )
                }
            )
        }
        HeaderItem("Преподаватели")
        Row(
            Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
            members.forEach {
                AssistChip(
                    onClick = { /*TODO*/ },
                    label = { Text(text = it.title) },
                    leadingIcon = {
                        AsyncImage(
                            model = it.avatarUrl,
                            contentDescription = "user avatar",
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                        )
                    },
                    trailingIcon = {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "remove member",
                            modifier = Modifier.clickable { onRemoveMemberClick(it) }
                        )
                    })
                Spacer(modifier = Modifier.width(MaterialTheme.spacing.normal))
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