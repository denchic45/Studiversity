package com.denchic45.studiversity.ui.yourstudygroups

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.ui.CircularLoadingBox
import com.denchic45.studiversity.ui.ScopeMemberEditorScreen
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.DropdownMenuItem2
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.profile.ProfileScreen
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.ui.studygroup.StudyGroupContent
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorScreen
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse


@Composable
fun YourStudyGroupsScreen(component: YourStudyGroupsComponent) {
    val groups by component.studyGroups.collectAsState()
    val selectedStudyGroup by component.selectedStudyGroup.collectAsState()
    val allowEditSelectedRes by component.allowEditSelected.collectAsState()

    Column {
        var showSpinner by remember { mutableStateOf(true) }
        if (showSpinner)
            StudyGroupSpinner(groups, selectedStudyGroup, component)

        val childStudyGroup by component.childStudyGroup.subscribeAsState()

        childStudyGroup.child?.instance?.let { studyGroupComponent ->
            Box {
                YourStudyGroupScreen(studyGroupComponent)
                val childSidebar by studyGroupComponent.childSidebar.subscribeAsState()
                showSpinner = childSidebar.child == null
                when (val child = childSidebar.child?.instance) {
                    is StudyGroupComponent.OverlayChild.Member -> ProfileScreen(child.component)

                    is StudyGroupComponent.OverlayChild.StudyGroupEditor -> {
                        StudyGroupEditorScreen(child.component)
                    }

                    null -> {
                        val title = uiTextOf("")
                        val menuItems = if (allowEditSelectedRes.takeValueIfSuccess() == true) {
                            listOf(
                                DropdownMenuItem2(
                                    title = uiTextOf("Изменить"),
                                    onClick = component::onEditStudyGroupClick
                                ),
                                DropdownMenuItem2(
                                    title = uiTextOf("Добавить участника"),
                                    onClick = component::onAddMemberClick
                                )
                            )
                        } else emptyList()
                        updateAppBarState(
                            title,
                            menuItems,
                            AppBarContent(title = title, dropdownItems = menuItems)
                        )

                    }

                    is StudyGroupComponent.OverlayChild.ScopeMemberEditor -> {
                        ScopeMemberEditorScreen(child.component)
                    }
                }
            }
        } ?: run {
            updateAppBarState(AppBarContent())
            CircularLoadingBox(Modifier.fillMaxSize())
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun StudyGroupSpinner(
    groupsResource: Resource<List<StudyGroupResponse>>,
    selectedStudyGroup: Resource<StudyGroupResponse>,
    component: YourStudyGroupsComponent,
) {
    groupsResource.onSuccess { groups ->
        var expanded by remember { mutableStateOf(false) }
        val showList = expanded && groups.isNotEmpty()

        selectedStudyGroup.onSuccess { selected ->
            ExposedDropdownMenuBox(
                expanded = showList,
                onExpandedChange = { expanded = it }) {
                OutlinedTextField(
                    value = "Группа ${selected.name}",
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
    StudyGroupContent(
        children = component.childTabs,
        onTabSelect = component::onTabSelect
    )
}
