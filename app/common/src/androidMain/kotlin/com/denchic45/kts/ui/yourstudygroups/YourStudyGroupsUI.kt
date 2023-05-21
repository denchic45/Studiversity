package com.denchic45.kts.ui.yourstudygroups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.RowScope
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
import androidx.compose.runtime.LaunchedEffect
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
import com.denchic45.kts.ui.studygroup.StudyGroupComponent
import com.denchic45.kts.ui.studygroup.StudyGroupContent
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorScreen
import com.denchic45.kts.ui.theme.spacing
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun YourStudyGroupsScreen(
    component: YourStudyGroupsComponent,
    appBarInteractor: AppBarInteractor
) {
    val groups by component.studyGroups.collectAsState()
    val selectedStudyGroup by component.selectedStudyGroup.collectAsState()
    val allowEditSelected by component.allowEditSelected.collectAsState()

    Surface {
        val actions: @Composable RowScope.() -> Unit = @Composable {
            val allow = when (val resource = allowEditSelected) {
                is Resource.Success -> resource.value
                else -> false
            }
            if (allow)
                IconButton(onClick = component::onEditStudyGroupClick) {
                    Icon(Icons.Outlined.Edit, null)
                }
        }

        LaunchedEffect(allowEditSelected) {
            appBarInteractor.set(
                AppBarState(
                    actionsUI = actions
                )
            )
        }

        selectedStudyGroup.onSuccess { group ->

        }

        Column {
            var showSpinner by remember { mutableStateOf(true) }
            if (showSpinner)
                StudyGroupSpinner(groups, selectedStudyGroup, component)

            val childStudyGroup by component.childStudyGroup.subscribeAsState()

            childStudyGroup.overlay?.instance?.let {
                val childSidebar by it.childSidebar.subscribeAsState()
                showSpinner = childSidebar.overlay == null
                when (val child = childSidebar.overlay?.instance) {
                    is StudyGroupComponent.OverlayChild.Member -> TODO()
                    is StudyGroupComponent.OverlayChild.StudyGroupEditor -> {
                        StudyGroupEditorScreen(child.component)
                    }

                    is StudyGroupComponent.OverlayChild.UserEditor -> TODO()
                    null -> YourStudyGroupScreen(component = it)
                }
            }
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun StudyGroupSpinner(
    groupsResource: Resource<List<StudyGroupResponse>>,
    selectedStudyGroup: Resource<StudyGroupResponse>,
    component: YourStudyGroupsComponent
) {
    groupsResource.onSuccess { groups ->
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
}

@Composable
fun YourStudyGroupScreen(component: StudyGroupComponent) {
    val selectedTab by component.selectedTab.collectAsState()
    StudyGroupContent(
        selectedTab = selectedTab,
        children = component.childTabs,
        onTabSelect = component::onTabSelect
    )
}
