package com.denchic45.studiversity.ui.usercourses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.courses.CourseListItem

@Composable
fun UserCoursesScreen(component: UserCoursesComponent) {
    val courses by component.coursesByUser.collectAsState()
    AlertDialog(
        onDismissRequest = component::onDismissRequest,
        title = { Text("Курсы") },
        confirmButton = { TextButton(onClick = {}) { Text("ОК") } },
        text = {
            ResourceContent(courses) {
                LazyColumn {
                    items(it) {
                        CourseListItem(it,Modifier.clickable { component.onCourseClick(it.id) })
                    }
                }
            }
        }
    )
}