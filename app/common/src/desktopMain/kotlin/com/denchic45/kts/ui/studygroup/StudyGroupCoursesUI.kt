package com.denchic45.kts.ui.studygroup

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.component.HeaderItemUI
import com.denchic45.kts.ui.model.StudyGroupCourseItem
import com.denchic45.kts.ui.studygroup.courses.StudyGroupCoursesComponent
import com.denchic45.kts.ui.theme.AppTheme
import com.seiko.imageloader.rememberAsyncImagePainter
import java.util.UUID

@Composable
fun StudyGroupCoursesScreen(studyGroupCoursesComponent: StudyGroupCoursesComponent) {
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        val courses by studyGroupCoursesComponent.courses.collectAsState()
        courses.onSuccess {
            StudyGroupCourseList(it)
        }
    }
}

@Composable
fun StudyGroupCourseList(list: List<StudyGroupCourseItem>) {
    Column {
        Spacer(Modifier.height(8.dp))
        HeaderItemUI("${list.size} курсов")
        Spacer(Modifier.height(8.dp))
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.width(1040.dp)
        ) {
            items(list) {
                StudyGroupCourseListItem(it)
            }
        }
    }
}

@Composable
fun StudyGroupCourseListItem(
    item: StudyGroupCourseItem,
    modifier: Modifier = Modifier
) {
    Card(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Row(
            Modifier.size(512.dp, 104.dp).padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            item.iconUrl?.let {
                Icon(
                    painter = rememberAsyncImagePainter(it),
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
        StudyGroupCourseListItem(
            StudyGroupCourseItem(
                UUID.randomUUID(),
                "История",
                "ic_subject_history"
            )
        )
    }
}