package com.denchic45.kts.ui.studygroupeditor

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.*

@Composable
fun StudyGroupEditorScreen(component: StudyGroupEditorComponent, ) {
    val viewState by component.viewState.collectAsState()
    val inputState by component.inputState.collectAsState()
    val searchedSpecialties by component.searchedSpecialties.collectAsState(emptyList())

    var expanded by remember { mutableStateOf(false) }

    StudyGroupEditorUI(
        viewState = viewState,
        searchedSpecialties = searchedSpecialties,
        onNameType = component::onNameType,
        onStartAcademicYear = component::onStartYearType,
        onEndAcademicYear = component::onEndYearType,
        {
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
        },
        startYear
    )
}