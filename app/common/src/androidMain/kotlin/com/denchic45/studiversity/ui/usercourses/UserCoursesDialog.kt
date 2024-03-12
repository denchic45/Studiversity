package com.denchic45.studiversity.ui.usercourses

import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.courses.CourseListItem

@Composable
fun UserCoursesDialog(component: UserCoursesComponent) {
    val courses by component.coursesByUser.collectAsState()

    ResourceContent(courses) {
        LazyColumn {
            items(it) {
                CourseListItem(it, Modifier.clickable { component.onCourseClick(it.id) })
            }
        }
    }
}