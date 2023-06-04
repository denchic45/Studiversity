package com.denchic45.kts.ui.studygroupeditor

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.ResourceContent
import com.denchic45.kts.ui.appbar2.ActionMenuItem2
import com.denchic45.kts.ui.appbar2.AppBarContent
import com.denchic45.kts.ui.appbar2.LocalAppBarState
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse

@Composable
fun StudyGroupEditorScreen(component: StudyGroupEditorComponent) {
    val stateResource by component.viewState.collectAsState()
    val inputState = component.inputState
    val searchedSpecialties by component.searchedSpecialties.collectAsState(emptyList())
    val searchedSpecialtiesText by component.searchSpecialtiesText.collectAsState()

    val allowSave by component.allowSave.collectAsState()
    val appBarState = LocalAppBarState.current

    LaunchedEffect(allowSave) {
        appBarState.animateUpdate {
            content = AppBarContent(
                title = uiTextOf(
                    if (component.isNew)
                        "Новая группа" else "Редактировать группу"
                ),
                actionItems = listOf(
                    ActionMenuItem2(
                        icon = uiIconOf(Icons.Default.Done),
                        enabled = allowSave,
                        onClick = component::onSaveClick
                    )
                )
            )
        }
    }

    ResourceContent(resource = stateResource) {
        StudyGroupEditorContent(
            state = it,
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StudyGroupEditorContent(
    state: EditingStudyGroup,
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
        state = state,
        inputState = inputState,
        searchedSpecialties = searchedSpecialties,
        onNameType = onNameType,
        onStartAcademicYear = onStartAcademicYear,
        onEndAcademicYear = onEndAcademicYear,
    ) {
        ExposedDropdownMenuBox(
            expanded = showList,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            OutlinedTextField(
                value = searchedSpecialtiesText.takeIf(String::isNotEmpty)
                    ?: state.specialty?.name ?: "",
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

@Preview
@Composable
fun StudyGroupEditorPreview() {
    StudyGroupEditorContent(
        state = EditingStudyGroup(),
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