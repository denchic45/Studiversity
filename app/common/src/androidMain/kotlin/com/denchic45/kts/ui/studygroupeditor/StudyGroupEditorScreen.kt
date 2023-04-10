package com.denchic45.kts.ui.studygroupeditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse

@Composable
fun StudyGroupEditorScreen(
    component: StudyGroupEditorComponent,
) {
    val viewState by component.viewState.collectAsState()
    val inputState by component.inputState.collectAsState()
    val searchedSpecialties by component.searchedSpecialties.collectAsState(emptyList())

    StudyGroupEditorContent(
        viewState = viewState,
        inputState = inputState,
        searchedSpecialties = searchedSpecialties,
        onNameType = component::onNameType,
        onSpecialtyNameType = component::onSpecialtyNameType,
        onStartAcademicYear = component::onStartYearType,
        onEndAcademicYear = component::onEndYearType,
        onSpecialtySelect = component::onSpecialtySelect
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudyGroupEditorContent(
    viewState: Resource<EditingStudyGroup>,
    inputState: StudyGroupEditorComponent.InputState,
    searchedSpecialties: List<SpecialtyResponse>,
    onNameType: (String) -> Unit,
    onSpecialtyNameType: (String) -> Unit,
    onStartAcademicYear: (Int) -> Unit,
    onEndAcademicYear: (Int) -> Unit,
    onSpecialtySelect: (SpecialtyResponse) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    StudyGroupEditorUI(
        viewState = viewState,
        inputState = inputState,
        searchedSpecialties = searchedSpecialties,
        onNameType = onNameType,
        onSpecialtyNameType = onSpecialtyNameType,
        onStartAcademicYear = onStartAcademicYear,
        onEndAcademicYear = onEndAcademicYear
    ) {
       viewState.onSuccess {
           ExposedDropdownMenuBox(
               expanded = expanded,
               onExpandedChange = {expanded = it}) {

               TextField(
                   readOnly = true,
                   value = it.searchedSpecialtiesText,
                   onValueChange = { },
                   modifier = Modifier.fillMaxWidth(),
                   placeholder = { Text("Специальность") },
                   supportingText = { Text("") },
                   trailingIcon = {
                       ExposedDropdownMenuDefaults.TrailingIcon(
                           expanded = expanded
                       )
                   },
                   colors = ExposedDropdownMenuDefaults.textFieldColors()
               )

               ExposedDropdownMenu(expanded = expanded,
                   onDismissRequest = { expanded = false }) {

               }
           }
       }
    }
}

@Preview
@Composable
fun StudyGroupEditorScreen() {
    StudyGroupEditorContent(
        viewState = Resource.Success(EditingStudyGroup()),
        inputState = StudyGroupEditorComponent.InputState(),
        searchedSpecialties = emptyList(),
        onNameType = {},
        onSpecialtyNameType = {},
        onStartAcademicYear = {},
        onEndAcademicYear = {},
        onSpecialtySelect = {}
    )
}