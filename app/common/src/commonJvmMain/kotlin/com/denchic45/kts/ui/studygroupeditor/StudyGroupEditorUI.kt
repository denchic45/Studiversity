package com.denchic45.kts.ui.studygroupeditor

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyGroupEditorUI(
    viewState: Resource<EditingStudyGroup>,
    inputState: StudyGroupEditorComponent.InputState,
    searchedSpecialties: List<SpecialtyResponse>,
    onNameType: (String) -> Unit,
    onSpecialtyNameType: (String) -> Unit,
    onStartAcademicYear: (Int) -> Unit,
    onEndAcademicYear: (Int) -> Unit,
    specialties: @Composable (List<SpecialtyResponse>) -> Unit
) {
    viewState.onSuccess { group ->
        Column {
            TextField(
                value = group.name,
                onValueChange = onNameType,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Название") },
                isError = inputState.nameMessage.isNotEmpty(),
                supportingText = { Text(inputState.nameMessage) }
            )

//            TextField(
//                value = group.specialty?.name ?: "",
//                onValueChange = onSpecialtyNameType,
//                modifier = Modifier.fillMaxWidth(),
//                placeholder = { Text("Специальность") },
//                supportingText = { Text("") }
//            )

            specialties(searchedSpecialties)

            Row {
                TextField(
                    value = group.startAcademicYear.takeIf { it != 0 }?.toString() ?: "",
                    onValueChange = {
                        onStartAcademicYear(it.takeIf(String::isNotEmpty)?.toInt() ?: 0)
                    },
                    placeholder = { Text("Год начала") },
                    supportingText = { Text("") },
                    modifier = Modifier.weight(0.5f)
                )
                Spacer(Modifier.width(MaterialTheme.spacing.normal))
                TextField(
                    value = group.endAcademicYear.takeIf { it != 0 }?.toString() ?: "",
                    onValueChange = {
                        onEndAcademicYear(it.takeIf(String::isNotEmpty)?.toInt() ?: 0)
                    },
                    placeholder = { Text("Год окончания") },
                    supportingText = { Text("") },
                    modifier = Modifier.weight(0.5f)
                )
            }
        }
    }
}