package com.denchic45.studiversity.ui.courseeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.ui.component.CustomExposedDropdownMenu
import com.denchic45.studiversity.ui.theme.spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseEditorContent(
    state: EditingCourse,
    onNameChange: (String) -> Unit,
    onSubjectQueryChange: (String) -> Unit,
    onSubjectSelect: (SubjectResponse) -> Unit,
    onSubjectRemove: () -> Unit,
) {
    Column {
        OutlinedTextField(
            value = state.name,
            onValueChange = onNameChange,
            singleLine = true,
            label = { Text(text = "Название") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(MaterialTheme.spacing.normal))
        state.subject?.let {
            ListItem(
                headlineContent = { Text(it.name) },
                modifier = Modifier.clip(MaterialTheme.shapes.medium),
                trailingContent = {
                    if (state.subject != null)
                        IconButton(onClick = onSubjectRemove) {
                            Icon(
                                imageVector = Icons.Outlined.Close,
                                contentDescription = "remove subject"
                            )
                        }
                }
            )
        } ?: run {
            var subjectsExpanded by remember { mutableStateOf(false) }
            ExposedDropdownMenuBox(
                expanded = subjectsExpanded,
                onExpandedChange = { subjectsExpanded = it }
            ) {
                OutlinedTextField(
                    value = state.subjectQuery,
                    onValueChange = onSubjectQueryChange,
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    placeholder = { Text("Найти предмет") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(subjectsExpanded) },
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors()
                )
                CustomExposedDropdownMenu(
                    expanded = state.foundSubjects.isNotEmpty(),
                    onDismissRequest = { }
                ) {
                    state.foundSubjects.forEach {
                        DropdownMenuItem(onClick = { onSubjectSelect(it) }) {
                            Text(it.name)
                        }
                    }
                }
            }
        }
    }
}