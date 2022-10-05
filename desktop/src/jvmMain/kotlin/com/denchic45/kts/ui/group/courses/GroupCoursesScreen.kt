package com.denchic45.kts.ui.group.courses

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.components.HeaderItem
import com.denchic45.kts.ui.model.GroupCourseItem
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap

@Composable
fun GroupCoursesScreen(groupCoursesComponent: GroupCoursesComponent) {
    Column(Modifier.width(1040.dp)) {
        val courses by groupCoursesComponent.courses.collectAsState()
        GroupCourseList(courses)
    }
}

@Composable
fun GroupCourseList(list: List<GroupCourseItem>) {
    Spacer(Modifier.height(8.dp))
    HeaderItem("${list.size} курсов")
    Spacer(Modifier.height(8.dp))
    LazyVerticalGrid(GridCells.Fixed(2),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        items(list) {
            GroupCourseListItem(it)
        }
    }
}

@Composable
fun GroupCourseListItem(item: GroupCourseItem, modifier: Modifier = Modifier) {
    Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier) {
        Row(Modifier.size(512.dp, 104.dp).padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Icon(painterResource(item.iconName.toDrawablePath()),
                null,
                Modifier.size(48.dp),
                MaterialTheme.colorScheme.secondary)
            Column(Modifier.padding(start = 24.dp)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Row(Modifier, verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(load = { loadImageBitmap(item.teacherPhotoUrl) },
                        painterFor = { BitmapPainter(it) },
                        null,
                        Modifier.size(24.dp).clip(CircleShape),
                        contentScale = ContentScale.Crop)
                    Text(item.teacherName,
                        Modifier.padding(start = 8.dp),
                        style = MaterialTheme.typography.bodyMedium)
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

        GroupCourseListItem(GroupCourseItem("",
            "История",
            "ic_subject_history",
            "Михаил Звягинцев",
            url))
    }
}