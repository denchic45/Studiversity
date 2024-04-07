package com.denchic45.studiversity.ui.studygroup

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.denchic45.studiversity.api.studygroup.model.StudyGroupResponse

@Composable
fun StudyGroupInfoDialog(studyGroup: StudyGroupResponse, onDismissRequest: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Группа ${studyGroup.name}") },
        text = { StudyGroupInfoContent(studyGroup) },
        confirmButton = { FilledTonalButton(onClick = onDismissRequest) { Text("ОК") } }
    )
}

@Composable
fun StudyGroupInfoContent(studyGroup: StudyGroupResponse) {
    Column {
        studyGroup.specialty?.let { specialty ->
            ListItem({ Text("Специальность: " + specialty.name) })
        }
        ListItem({ Text("Курс: ${studyGroup.academicYear.end - studyGroup.academicYear.start}") })
        ListItem({ Text("Период обучения: ${studyGroup.academicYear.start} - ${studyGroup.academicYear.end}") })
    }
}