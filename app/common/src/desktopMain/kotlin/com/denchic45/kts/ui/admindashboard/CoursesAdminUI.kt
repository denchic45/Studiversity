package com.denchic45.kts.ui.admindashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.stuiversity.api.course.model.CourseResponse

@Composable
fun CoursesAdminScreen(component: CoursesAdminComponent) {
    AdminSearchScreen(component, CourseResponse::id) {
        Text(it.name)
    }
}