package com.denchic45.kts.ui.yourstudygroups

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.LocalAppBarMediator
import com.denchic45.kts.ui.components.ExposedDropdownMenuDefaults
import com.denchic45.kts.ui.studygroup.StudyGroupScreen
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.util.UUID


@Composable
fun YourStudyGroupsScreen(component: YourStudyGroupsComponent) {
    val selectedStudyGroup by component.selectedStudyGroup.collectAsState()
    val studyGroups by component.studyGroups.collectAsState()

    val appBarMediator = LocalAppBarMediator.current

    appBarMediator.content = {
        studyGroups.onSuccess { groups ->
            StudyGroupSpinner(groups, selectedStudyGroup, component::onGroupSelect)
        }
    }

    val childStudyGroup by component.childStudyGroup.subscribeAsState()
    childStudyGroup.overlay?.instance?.let {
        StudyGroupScreen(it)
    }
}

@Composable
private fun StudyGroupSpinner(
    groups: List<StudyGroupResponse>,
    selectedStudyGroup: Resource<StudyGroupResponse>,
    onSelect: (UUID) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val showList = expanded && groups.isNotEmpty()

    selectedStudyGroup.onSuccess { selected ->
        OutlinedButton(onClick = { expanded = !expanded }) {
            Text(
                text = selected.name,
                modifier = Modifier.padding(end = MaterialTheme.spacing.small)
            )
            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }) {
            groups.forEach { group ->
                DropdownMenuItem(
                    text = { Text(group.name) },
                    onClick = {
                        onSelect(group.id)
                        expanded = false
                    }
                )
            }
        }
    }
}