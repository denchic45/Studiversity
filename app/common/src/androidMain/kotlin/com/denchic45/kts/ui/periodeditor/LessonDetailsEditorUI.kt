package com.denchic45.kts.ui.periodeditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.timetable.model.PeriodMember

@Composable
fun LessonDetailsEditorScreen(
    component: LessonDetailsEditorComponent,
    members: List<PeriodMember>,
    onAddMemberClick: () -> Unit,
    onRemoveMemberClick: (PeriodMember) -> Unit,
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
    members: List<PeriodMember>,
    onAddMemberClick: () -> Unit,
    onRemoveMemberClick: (PeriodMember) -> Unit,
    onCourseChoose: () -> Unit,
) {
    Box(modifier = Modifier.clickable { onCourseChoose() }) {
        details.course?.let {
            ListItem(
                headlineContent = { Text(text = it.subject?.name ?: it.name) },
                leadingContent = {
                    it.subject?.let {
                        Image(
                            painter = rememberAsyncImagePainter(
                                ImageRequest.Builder(LocalContext.current)
                                    .decoderFactory(SvgDecoder.Factory())
                                    .data(it.iconUrl)
                                    .build()
                            ),
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
        members.forEach {
            AssistChip(
                onClick = { /*TODO*/ },
                label = { Text(text = it.fullName) },
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