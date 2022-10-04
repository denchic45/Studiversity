package com.denchic45.kts.ui.group.courses

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.model.GroupCourseItem
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.util.AsyncImage
import com.denchic45.kts.util.loadImageBitmap

@Composable
fun GroupCoursesScreen(groupCoursesComponent: GroupCoursesComponent) {
}

@Composable
fun CourseListItem(item: GroupCourseItem) {
    Card(border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        elevation = 0.dp,
        shape = RoundedCornerShape(16.dp)) {
        Row(Modifier.size(512.dp, 104.dp).padding(horizontal = 32.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Image(painterResource(item.iconName.toDrawablePath()), null, Modifier.size(48.dp))
            Column(Modifier.padding(start = 24.dp)) {
                Text(item.name, style = MaterialTheme.typography.bodyLarge)
                Row(Modifier.padding(top = 4.dp)) {
                    AsyncImage(load = { loadImageBitmap(item.teacherPhotoUrl) },
                        painterFor = { BitmapPainter(it) },
                        null,
                        Modifier.size(24.dp).clip(CircleShape), contentScale = ContentScale.Crop)
                    Text(item.teacherName,Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

@Preview
@Composable
fun CourseListItemPreview() {
    val url =
        "https://sun9-46.userapi.com/impg/uWGoczB9st04meAMg6wWn4iSlmUXIjckTZNyqg/pb7GZSpZUAA.jpg?size=1080x2033&quality=96&sign=1320149c468c5ee8341e40a5f58f8923&type=album"

    CourseListItem(GroupCourseItem("", "История", "ic_subject_history", "Михаил Звягинцев", url))
}