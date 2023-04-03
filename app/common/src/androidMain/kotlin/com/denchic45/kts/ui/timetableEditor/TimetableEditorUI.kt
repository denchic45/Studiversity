package com.denchic45.kts.ui.timetableEditor

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.denchic45.kts.domain.model.StudyGroupNameItem
import com.denchic45.kts.domain.timetable.model.PeriodDetails
import com.denchic45.kts.domain.timetable.model.PeriodItem
import java.util.*
import com.denchic45.kts.R

@Composable
fun TimetableEditorUI() {

}

@Composable
fun PeriodItemUI(
    item: PeriodItem?,
    time: String,
    groupShowing: Boolean = false,
    isEdit: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .height(56.dp)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item?.let {
                Text(text = item.order.toString(), style = MaterialTheme.typography.bodyLarge)
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(
                            when (val details = item.details) {
                                is PeriodDetails.Event -> details.iconUrl
                                is PeriodDetails.Lesson -> details.subjectIconUrl
                            }
                        )
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier
                        .size(32.dp)
                        .padding(16.dp)
                )
                Row(Modifier.weight(1f)) {
                    when (val details = item.details) {
                        is PeriodDetails.Lesson -> Text(
                            text = details.subjectName,
                            style = MaterialTheme.typography.titleMedium
                        )
                        is PeriodDetails.Event -> Text(
                            text = details.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    Text(text = time)
                }

                if (groupShowing)
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.ic_group),
                            contentDescription = null,
                            modifier = Modifier.padding(8.dp)
                        )
                        Text(item.studyGroup.name)
                    }
                item.room?.let { Text(it) }
            } ?: run {
                Text(
                    text = "Пусто",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.Gray
                )
            }
        }
    }
}

@Preview
@Composable
fun PeriodItemPreview() {
    PeriodItemUI(
        item = PeriodItem(
            id = -1,
            studyGroup = StudyGroupNameItem(UUID.randomUUID(), "ПКС-4"),
            room = "1 м.",
            members = emptyList(),
            order = 1,
            details = PeriodDetails.Lesson(
                courseId = UUID.randomUUID(),
                subjectIconUrl = "https://coil-kt.github.io/coil/images/coil_logo_black.svg",
                "Математика"
            )
        ),
        time = "8:30 - 9:20"
    )
}