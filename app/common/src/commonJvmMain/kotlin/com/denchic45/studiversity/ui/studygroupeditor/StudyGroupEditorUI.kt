package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyGroupEditorContent(
    state: EditingStudyGroup,
    inputState: StudyGroupEditorComponent.InputState,
    searchedSpecialties: List<SpecialtyResponse>,
    onNameType: (String) -> Unit,
    onStartAcademicYear: (Int) -> Unit,
    onEndAcademicYear: (Int) -> Unit,
    specialties: @Composable (List<SpecialtyResponse>) -> Unit,
) {
        Column(
            Modifier.padding(
                horizontal = MaterialTheme.spacing.medium,
                vertical = MaterialTheme.spacing.normal
            )
        ) {
            OutlinedTextField(
                value = state.name,
                onValueChange = onNameType,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Название") },
                isError = inputState.nameMessage != null,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                supportingText = { Text(inputState.nameMessage ?: "") }
            )

            specialties(searchedSpecialties)

            val pattern = remember { Regex("^\\d+\$") }

            Row {
                OutlinedTextField(
                    value = state.startAcademicYear.takeIf { it != 0 }?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(pattern) && it.length < 5 || it.isEmpty())
                            onStartAcademicYear(it.takeIf(String::isNotEmpty)?.toInt() ?: 0)
                    },
                    singleLine = true,
                    label = { Text("Год начала") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(0.5f),
                    isError = inputState.startYearMessage != null,
                    supportingText = { Text(inputState.startYearMessage ?: "") }
                )
                Spacer(Modifier.width(MaterialTheme.spacing.normal))
                OutlinedTextField(
                    value = state.endAcademicYear.takeIf { it != 0 }?.toString() ?: "",
                    onValueChange = {
                        if (it.matches(pattern) && it.length < 5 || it.isEmpty())
                            onEndAcademicYear(it.takeIf(String::isNotEmpty)?.toInt() ?: 0)
                    },
                    singleLine = true,
                    label = { Text("Год окончания") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(0.5f),
                    isError = inputState.endYearMessage != null,
                    supportingText = { Text(inputState.endYearMessage ?: "") }
                )
            }
        }
    }