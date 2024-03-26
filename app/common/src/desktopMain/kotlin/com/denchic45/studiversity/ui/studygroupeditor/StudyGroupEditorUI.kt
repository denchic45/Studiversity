package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.theme.CommonAppTheme
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import java.util.*

@Composable
fun StudyGroupEditorDialog(component: StudyGroupEditorComponent) {
    val resource by component.viewState.collectAsState()

    resource.onSuccess { state ->
        AlertDialog(
            onDismissRequest = component::onDismissClick,
            title = { Text(if (state.isNew) "Создать группу" else "Изменить пользователя") },
            text = {
                StudyGroupEditorContent(
                    state = state,
                    onNameChange = component::onNameChange,
                    onStartAcademicYearChange = component::onStartYearChange,
                    onEndAcademicYearChange = component::onEndYearChange,
                    onSpecialtyQueryChange = component::onSpecialtyQueryChange,
                    onSpecialtySelect = component::onSpecialtySelect,
                    onRemoveStudyGroupClick = component::onRemoveStudyGroupClick
                )
            },
            confirmButton = {
                TextButton(component::onSaveClick) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton({ component.onDismissClick() }) { Text("Отмена") }
            }
        )
    }
}

@Preview
@Composable
fun StudyGroupEditorPreview() {
    CommonAppTheme {
        StudyGroupEditorContent(
            state = EditingStudyGroup(false).apply {
                this.name = "ПО-45б"
                this.specialty = SpecialtyResponse(UUID.randomUUID(), "Программная инженерия", shortname = "ПО")
                this.startAcademicYear = 2023
                this.endAcademicYear = 2027
            },
            {}, {}, {}, {}, {},{}
        )
    }
}