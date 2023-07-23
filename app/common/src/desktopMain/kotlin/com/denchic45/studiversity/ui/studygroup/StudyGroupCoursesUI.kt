package com.denchic45.studiversity.ui.studygroup

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.component.HeaderItemUI
import com.denchic45.studiversity.ui.model.StudyGroupCourseItem
import com.denchic45.studiversity.ui.studygroup.courses.StudyGroupCoursesComponent
import com.denchic45.studiversity.ui.theme.DesktopAppTheme
import com.denchic45.studiversity.ui.theme.spacing
import com.seiko.imageloader.rememberAsyncImagePainter
import java.util.*

@Composable
fun StudyGroupCoursesScreen(studyGroupCoursesComponent: StudyGroupCoursesComponent) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        val courses by studyGroupCoursesComponent.courses.collectAsState()
        ResourceContent(courses) {
            StudyGroupCourses(it)
        }
    }
}

@Composable
fun StudyGroupCourses(list: List<StudyGroupCourseItem>) {
    Column {
        Spacer(Modifier.height(8.dp))
        HeaderItemUI("${list.size} курс(ов)")
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.width(1040.dp),
            contentPadding = PaddingValues(
                horizontal = MaterialTheme.spacing.normal,
                vertical = MaterialTheme.spacing.small
            )
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
    modifier: Modifier = Modifier,
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
    DesktopAppTheme {
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