package com.denchic45.studiversity.ui.search

import androidx.compose.runtime.Composable
import com.denchic45.studiversity.ui.component.CourseListItem

@Composable
fun CourseChooserScreen(component: CourseChooserComponent) {
    SearchScreen(
        component = component,
        keyItem = { it.id }, itemContent = {
            CourseListItem(it)
        })
}