package com.denchic45.studiversity.ui.timetable

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.domain.model.StudyGroupNameItem
import com.denchic45.studiversity.domain.timetable.model.PeriodDetails
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.studiversity.domain.timetable.model.PeriodSlot
import com.denchic45.studiversity.domain.timetable.model.Window
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.search.UserListItem
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.ui.theme.spacing
import java.util.UUID

@Composable
fun PeriodListItem(
    item: PeriodSlot,
    order: Int,
    time: String,
    showStudyGroup: Boolean = true,
    isEdit: Boolean = false,
    onEditClick: () -> Unit = {}
) {
    Surface {
        Column {
            var expanded by remember { mutableStateOf(false) }
            Row(
                modifier = Modifier
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.toString(),
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.End,
                    modifier = Modifier
                        .padding(vertical = MaterialTheme.spacing.normal)
                        .width(24.dp)
                        .wrapContentHeight(align = Alignment.CenterVertically)
                )
                when (item) {
                    is PeriodItem -> {
                        val painter = rememberAsyncImagePainter(
                            model = ImageRequest.Builder(LocalContext.current)
                                .decoderFactory(SvgDecoder.Factory())
                                .data(
                                    when (val details = item.details) {
                                        is PeriodDetails.Event -> details.iconUrl
                                        is PeriodDetails.Lesson -> details.subjectIconUrl
                                    }
                                )
                                .build()
                        )
                        Icon(
                            painter = painter,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .padding(horizontal = MaterialTheme.spacing.normal)
                                .size(32.dp)

                        )
                        Column(Modifier.weight(1f)) {
                            when (val details = item.details) {
                                is PeriodDetails.Lesson -> {
                                    details.subjectName?.let {
                                        Text(
                                            text = details.subjectName,
                                            style = MaterialTheme.typography.titleMedium
                                        )
                                    }
                                }

                                is PeriodDetails.Event -> Text(
                                    text = details.name,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(
                                text = time,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        if (showStudyGroup) {
                            AssistChip(
                                onClick = { },
                                label = { Text(item.studyGroup.name) },
                                leadingIcon = {
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_study_group),
                                        contentDescription = null,
                                        modifier = Modifier.padding(8.dp)
                                    )
                                })

                            Spacer(modifier = Modifier.width(MaterialTheme.spacing.small))
                        }
                        item.room?.let {
                            Text(
                                text = it,
                                textAlign = TextAlign.End,
                                modifier = Modifier.widthIn(max = 48.dp),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        AnimatedVisibility(visible = expanded) {
                            item.let {
                                it.members.forEach { item ->
                                    UserListItem(item)
                                }
                            }
                        }
                    }

                    is Window -> {
                        Column(
                            Modifier
                                .weight(1f)
                                .padding(start = MaterialTheme.spacing.normal)
                        ) {
                            Text(
                                text = "Пусто",
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.Gray
                            )
                            Text(
                                text = time,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }


                if (isEdit) {
                    IconButton(onClick = { onEditClick() }) {
                        Icon(painterResource(id = R.drawable.ic_edit), null)
                    }
                }
            }


        }
    }
}

@Preview
@Composable
fun PeriodItemPreview() {
    AppTheme {
        PeriodListItem(
            order = 1,
            item = PeriodItem(
                id = -1,
                studyGroup = StudyGroupNameItem(UUID.randomUUID(), "ПКС-4"),
                room = "1 м.",
                members = listOf(
                    UserItem(
                        id = UUID.randomUUID(),
                        firstName = "Ivan",
                        surname = "Ivanov",
                        avatarUrl = ""
                    )
                ),
                details = PeriodDetails.Lesson(
                    courseId = UUID.randomUUID(),
                    subjectIconUrl = "https://coil-kt.github.io/coil/images/coil_logo_black.svg",
                    "Математика"
                )
            ),
            time = "8:30 - 9:20",
            isEdit = true
        )
    }
}

@Preview
@Composable
fun EmptyPeriodItemPreview() {
    AppTheme {
        PeriodListItem(
            order = 1,
            item = Window(),
            time = "8:30 - 9:20"
        )
    }
}