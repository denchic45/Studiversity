package com.denchic45.studiversity.ui.usercourses

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.course.CourseListItem

@Composable
fun UserCoursesDialog(component: UserCoursesComponent) {
    val courses by component.coursesByUser.collectAsState()

    ResourceContent(courses) {
        LazyColumn {
            items(it) {
                CourseListItem(it, onClick = { component.onCourseClick(it.id) })
            }
        }
    }
}