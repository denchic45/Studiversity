package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.component.TitleAlertDialog
import com.denchic45.studiversity.ui.confirm.DismissChangesConfirmDialog
import com.denchic45.studiversity.ui.theme.CommonAppTheme
import java.util.*

@Composable
fun StudyGroupEditorDialog(component: StudyGroupEditorComponent) {
    val resource by component.viewState.collectAsState()
    val allowSave by component.allowSave.collectAsState()
    val needToConfirmToDismiss by component.needToConfirmToDismiss.collectAsState()

    DismissChangesConfirmDialog(
        show = needToConfirmToDismiss,
        onConfirm = component::onDismissRequest,
        onDismiss = component::onCancelDismiss
    )

    resource.onSuccess { state ->
        AlertDialog(
            onDismissRequest = component::onDismissRequest,
            title = {
                TitleAlertDialog(
                    text = if (state.isNew) "Создать группу" else "Изменить группу",
                    onClose = component::onDismissRequest
                )
            },
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
                TextButton(component::onSaveClick, enabled = allowSave) { Text("Сохранить") }
            },
            dismissButton = {
                TextButton({ component.onDismissRequest() }) { Text("Отмена") }
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
            {}, {}, {}, {}, {}, {}
        )
    }
}