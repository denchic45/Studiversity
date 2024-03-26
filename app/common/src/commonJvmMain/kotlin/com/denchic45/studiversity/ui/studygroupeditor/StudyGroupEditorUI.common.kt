package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyGroupEditorContent(
    state: EditingStudyGroup,
    onNameChange: (String) -> Unit,
    onStartAcademicYearChange: (Int) -> Unit,
    onEndAcademicYearChange: (Int) -> Unit,
    onSpecialtyQueryChange: (String) -> Unit,
    onSpecialtySelect: (SpecialtyResponse) -> Unit,
    onRemoveStudyGroupClick: () -> Unit
) {
    Column(
        Modifier.padding(
            horizontal = MaterialTheme.spacing.medium,
            vertical = MaterialTheme.spacing.normal
        )
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Название") },
            isError = state.nameMessage != null,
            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
            supportingText = { Text(state.nameMessage ?: "") }
        )

        var specialtiesExpanded by remember { mutableStateOf(false) }
        val showList = specialtiesExpanded && state.foundSpecialties.isNotEmpty()
        ExposedDropdownMenuBox(
            expanded = showList,
            onExpandedChange = { specialtiesExpanded = !specialtiesExpanded },
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            OutlinedTextField(
                value = state.specialtyQuery.takeIf(String::isNotEmpty)
                    ?: state.specialty?.name ?: "",
                onValueChange = { onSpecialtyQueryChange(it) },
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
                onDismissRequest = { specialtiesExpanded = false }) {
                state.foundSpecialties.forEach {
                    DropdownMenuItem(
                        text = { Text(it.name) },
                        onClick = {
                            onSpecialtySelect(it)
                            specialtiesExpanded = false
                        }
                    )
                }
            }
        }

        val pattern = remember { Regex("^\\d+\$") }

        Row {
            OutlinedTextField(
                value = state.startAcademicYear.takeIf { it != 0 }?.toString() ?: "",
                onValueChange = {
                    if (it.matches(pattern) && it.length < 5 || it.isEmpty())
                        onStartAcademicYearChange(it.takeIf(String::isNotEmpty)?.toInt() ?: 0)
                },
                singleLine = true,
                label = { Text("Год начала") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(0.5f),
                isError = state.startYearMessage != null,
                supportingText = { Text(state.startYearMessage ?: "") }
            )
            Spacer(Modifier.width(MaterialTheme.spacing.normal))
            OutlinedTextField(
                value = state.endAcademicYear.takeIf { it != 0 }?.toString() ?: "",
                onValueChange = {
                    if (it.matches(pattern) && it.length < 5 || it.isEmpty())
                        onEndAcademicYearChange(it.takeIf(String::isNotEmpty)?.toInt() ?: 0)
                },
                singleLine = true,
                label = { Text("Год окончания") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.weight(0.5f),
                isError = state.endYearMessage != null,
                supportingText = { Text(state.endYearMessage ?: "") }
            )
        }

        if (!state.isNew) {
            var confirmRemove by remember { mutableStateOf(false) }

            ListItem(
                headlineContent = { Text("Удалить группу") },
                supportingContent = { Text("Восстановление будет невозможно") },
                leadingContent = {
                    Icon(
                        Icons.Outlined.Delete,
                        null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .clickable { confirmRemove = true }
            )
            if (confirmRemove)
                AlertDialog(
                    onDismissRequest = { confirmRemove = false },
                    icon = { Icon(Icons.Outlined.Delete, null) },
                    title = { Text("Удалить группу?") },
                    text = {
                        Text(
                            """
                                Удалятся все данные, связанные с группой.
                                Восстановить группу будет невозможно.
                            """.trimIndent()
                        )
                    },
                    confirmButton = {
                        FilledTonalButton(onClick = {
                            confirmRemove = false
                            onRemoveStudyGroupClick()
                        }) { Text("Удалить") }
                    },
                    dismissButton = {
                        TextButton(onClick = {
                            confirmRemove = false
                        }) { Text("Отмена") }
                    }
                )
        }
    }
}