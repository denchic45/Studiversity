package com.denchic45.studiversity.ui.admindashboard

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse

@Composable
fun SubjectsAdminScreen(component: SubjectsAdminComponent) {
    AdminSearchScreen(
        component = component,
        keyItem = SubjectResponse::id,
        searchPlaceholder = "Найти предмет",
        fabText = "Создать предмет"
    ) {
        Text(it.name)
    }
}