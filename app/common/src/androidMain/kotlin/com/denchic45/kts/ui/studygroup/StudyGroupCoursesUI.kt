package com.denchic45.kts.ui.studygroup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.School
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.ResourceContent
import com.denchic45.kts.ui.model.StudyGroupCourseItem
import com.denchic45.kts.ui.studygroup.courses.StudyGroupCoursesComponent
import com.denchic45.kts.ui.theme.spacing
import com.seiko.imageloader.rememberAsyncImagePainter
import java.util.UUID

@Composable
fun StudyGroupCoursesScreen(component: StudyGroupCoursesComponent) {
    val courses by component.courses.collectAsState()
    ResourceContent(resource = courses) {
        StudyGroupCoursesContent(courseItems = it, onCourseClick = component::onCourseClick)
    }
}

@Composable
fun StudyGroupCoursesContent(
    courseItems: List<StudyGroupCourseItem>,
    onCourseClick: (UUID) -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(MaterialTheme.spacing.normal),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small)
    ) {
        items(courseItems, key = { it.id }) { course ->
            StudyGroupCourseListItem(
                item = course,
                modifier = Modifier.clickable { onCourseClick(course.id) }
            )
        }
    }
}

@Composable
fun StudyGroupCourseListItem(
    item: StudyGroupCourseItem,
    modifier: Modifier = Modifier,
) {
    OutlinedCard(
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
//        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(MaterialTheme.spacing.normal),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val painter = item.iconUrl?.let {
                rememberAsyncImagePainter(url = it)
            } ?: rememberVectorPainter(image = Icons.Outlined.School)

            Icon(
                painter = painter,
                contentDescription = "course icon",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(48.dp)
            )

            Column(Modifier.padding(start = 24.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
            }

//            item.iconName?.let {
//                Icon(
//                    painterResource(it.toDrawablePath()),
//                    null,
//                    Modifier.size(48.dp),
//                    MaterialTheme.colorScheme.secondary
//                )
//            }
        }
    }
}