package com.denchic45.studiversity.ui.course

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import com.denchic45.studiversity.api.course.model.CourseResponse

@Composable
fun CourseInfoDialog(studyGroup: CourseResponse, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Курс ${studyGroup.name}") },
        text = { CourseInfoContent(studyGroup) },
        confirmButton = { FilledTonalButton(onClick = onDismissRequest) { Text("ОК") } }
    )
}

@Composable
fun CourseInfoContent(course: CourseResponse) {
    Column {
        course.subject?.let { subject ->
            ListItem({ Text("Предмет: " + subject.name) })
        }
        if (course.archived)
            Text("Курс добавлен в архив", color = MaterialTheme.colorScheme.error)
    }
}