package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.denchic45.studiversity.ui.ResourceContent

@Composable
fun StudyGroupEditorScreen(component: StudyGroupEditorComponent) {
    val resource by component.viewState.collectAsState()
    val inputState = component.inputState
    val searchedSpecialties by component.searchedSpecialties.collectAsState(emptyList())

    var expanded by remember { mutableStateOf(false) }

    ResourceContent(resource) {
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
    }
}