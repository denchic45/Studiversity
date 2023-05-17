package com.denchic45.kts.ui.yourstudygroups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.studygroup.StudyGroupScreen
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.kts.ui.uiTextOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourStudyGroupsScreen(component: YourStudyGroupsComponent, appBarInteractor: AppBarInteractor) {
    val groups by component.studyGroups.collectAsState()
    val selectedStudyGroup by component.selectedStudyGroup.collectAsState()

    Surface {
        selectedStudyGroup.onSuccess { group ->


        }

        Column {
            groups.onSuccess { groups ->
                var expanded by remember { mutableStateOf(false) }
                val showList = expanded && groups.isNotEmpty()

                selectedStudyGroup.onSuccess { selected ->
                    ExposedDropdownMenuBox(
                        expanded = showList,
                        onExpandedChange = { expanded = it }) {
                        OutlinedTextField(
                            value = selected.name,
                            readOnly = true,
                            onValueChange = {},
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = showList)
                            },
                            singleLine = true,
                            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(MaterialTheme.spacing.small)
                                .menuAnchor(),
                        )
                        ExposedDropdownMenu(
                            expanded = showList,
                            onDismissRequest = { expanded = false }) {

                            groups.forEachIndexed { index, group ->
                                DropdownMenuItem(
                                    text = { Text(group.name) },
                                    onClick = {
                                        component.onGroupSelect(group.id)
                                        expanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
            val childStudyGroup by component.childStudyGroup.subscribeAsState()

            childStudyGroup.overlay?.instance?.let {
                StudyGroupScreen(component = it,appBarInteractor = appBarInteractor)
            }
        }
    }
}