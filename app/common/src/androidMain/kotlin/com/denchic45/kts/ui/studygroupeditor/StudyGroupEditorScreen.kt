package com.denchic45.kts.ui.studygroupeditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse

@Composable
fun StudyGroupEditorScreen(component: StudyGroupEditorComponent) {
    val viewState by component.viewState.collectAsState()
    val inputState = component.inputState
    val searchedSpecialties by component.searchedSpecialties.collectAsState(emptyList())
    val searchedSpecialtiesText by component.searchSpecialtiesText.collectAsState()

    StudyGroupEditorContent(
        viewState = viewState,
        inputState = inputState,
        searchedSpecialties = searchedSpecialties,
        onNameType = component::onNameType,
        onSpecialtyNameType = component::onSpecialtyNameType,
        onStartAcademicYear = component::onStartYearType,
        onEndAcademicYear = component::onEndYearType,
        searchedSpecialtiesText = searchedSpecialtiesText,
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
    searchedSpecialtiesText: String,
    onSpecialtySelect: (SpecialtyResponse) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val showList = expanded && searchedSpecialties.isNotEmpty()
    StudyGroupEditorContent(
        viewState = viewState,
        inputState = inputState,
        searchedSpecialties = searchedSpecialties,
        onNameType = onNameType,
        onStartAcademicYear = onStartAcademicYear,
        onEndAcademicYear = onEndAcademicYear,
    ) {
        viewState.onSuccess {
            ExposedDropdownMenuBox(
                expanded = showList,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                OutlinedTextField(
                    value = searchedSpecialtiesText.takeIf(String::isNotEmpty)
                        ?: it.specialty?.name ?: "",
                    onValueChange = { onSpecialtyNameType(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    label = { Text("Специальность") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = showList
                        )
                    },
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )

                ExposedDropdownMenu(
                    expanded = showList,
                    onDismissRequest = { expanded = false }) {
                    searchedSpecialties.forEach {
                        DropdownMenuItem(
                            text = { Text(it.name) },
                            onClick = {
                                onSpecialtySelect(it)
                                expanded = false
                            }
                        )
                    }
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
        searchedSpecialtiesText = "",
        onSpecialtySelect = {}
    )
}