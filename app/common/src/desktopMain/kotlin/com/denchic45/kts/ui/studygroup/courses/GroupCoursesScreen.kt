package com.denchic45.kts.ui.studygroup.courses

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.model.StudyGroupCourseItem
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.theme.toDrawablePath
import java.util.*

@Composable
fun GroupCoursesScreen(studyGroupCoursesComponent: StudyGroupCoursesComponent) {
    Column(Modifier.width(1040.dp)) {
        val courses by studyGroupCoursesComponent.courses.collectAsState()
        courses.onSuccess {
            GroupCourseList(it)
        }
    }
}

@Composable
fun GroupCourseList(list: List<StudyGroupCourseItem>) {
    Spacer(Modifier.height(8.dp))
    HeaderItemUI("${list.size} курсов")
    Spacer(Modifier.height(8.dp))
    LazyVerticalGrid(
        GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(list) {
            GroupCourseListItem(it)
        }
    }
}

@Composable
fun GroupCourseListItem(item: StudyGroupCourseItem, modifier: Modifier = Modifier) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            Modifier.size(512.dp, 104.dp).padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.iconUrl?.let {
                Icon(
                    painterResource(it.toDrawablePath()),
                    null,
                    Modifier.size(48.dp),
                    MaterialTheme.colorScheme.secondary
                )
                Column(Modifier.padding(start = 24.dp)) {
                    Text(item.name, style = MaterialTheme.typography.titleMedium)
                }
            }
        }
    }
}

@Preview
@Composable
fun GroupCourseListItemPreview() {
    AppTheme {
        val url =
            "https://sun9-46.userapi.com/impg/uWGoczB9st04meAMg6wWn4iSlmUXIjckTZNyqg/pb7GZSpZUAA.jpg?size=1080x2033&quality=96&sign=1320149c468c5ee8341e40a5f58f8923&type=album"
        GroupCourseListItem(
            StudyGroupCourseItem(
                UUID.randomUUID(),
                "История",
                "ic_subject_history"
            )
        )
    }
}