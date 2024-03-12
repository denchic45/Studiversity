package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.components.dialog.AlertDialog

@Composable
fun StudyGroupEditorScreen(component: StudyGroupEditorComponent) {
    val resource by component.viewState.collectAsState()
    val inputState = component.inputState
    val searchedSpecialties by component.searchedSpecialties.collectAsState(emptyList())

    var expanded by remember { mutableStateOf(false) }

    ResourceContent(resource) {
        AlertDialog(
            onDismissRequest = component::onDismissClick,
            title = { Text("Создать пользователя") },
            text = {
                StudyGroupEditorContent(
                    state = it,
                    searchedSpecialties = searchedSpecialties,
                    onNameType = component::onNameType,
                    onStartAcademicYear = component::onStartYearType,
                    onEndAcademicYear = component::onEndYearType,
                    inputState = inputState
                ) {
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                    ) {
                        searchedSpecialties.forEach { response ->
                            DropdownMenuItem(
                                text = { Text(response.name) },
                                onClick = {
                                    expanded = false
                                    component.onSpecialtySelect(response)
                                })
                        }
                    }
                }
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