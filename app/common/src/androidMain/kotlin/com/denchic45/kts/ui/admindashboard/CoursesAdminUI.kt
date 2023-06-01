package com.denchic45.kts.ui.admindashboard

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.denchic45.kts.ui.chooser.SearchScreen
import com.denchic45.stuiversity.api.course.model.CourseResponse

@Composable
fun CoursesAdminScreen(component: CoursesAdminComponent) {
    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = component::onAddCourseClick,
                text = { Text(text = "Создать курс") },
                icon = { Icon(Icons.Default.Add, "add course") })
        }) {
        Box(Modifier.padding(it)) {
            SearchScreen(
                component = component,
                keyItem = CourseResponse::id
            ) {
                Text(text = it.name)
            }
        }
    }
}