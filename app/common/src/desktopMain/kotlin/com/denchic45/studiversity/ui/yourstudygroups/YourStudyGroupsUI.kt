package com.denchic45.studiversity.ui.yourstudygroups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.components.ExposedDropdownMenuDefaults
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.ui.studygroup.StudyGroupContent
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.util.*


@Composable
fun YourStudyGroupsScreen(component: YourStudyGroupsComponent) {
    val selectedStudyGroup by component.selectedStudyGroup.collectAsState()
    val studyGroups by component.studyGroups.collectAsState()
    val childStudyGroup by component.childStudyGroup.subscribeAsState()

    ScreenScaffold(
        topBar = {
            CustomAppBar(
                title = {
                    AppBarTitle(
                        text = when (val selected = selectedStudyGroup) {
                            is Resource.Success -> "Группа ${selected.value.name}"
                            else -> ""
                        }
                    )
                    Spacer(Modifier.width(MaterialTheme.spacing.normal))
                    StudyGroupsSpinner(studyGroups, selectedStudyGroup, component)
                },
            )
        }
    ) {
        childStudyGroup.child?.instance?.let {
            YourStudyGroupScreen(it)
        }
    }
}

@Composable
private fun StudyGroupsSpinner(
    studyGroups: Resource<List<StudyGroupResponse>>,
    selectedStudyGroup: Resource<StudyGroupResponse>,
    component: YourStudyGroupsComponent
) {
    studyGroups.onSuccess { groups ->
        Spacer(Modifier.width(MaterialTheme.spacing.normal))
        StudyGroupSpinner(groups, selectedStudyGroup, component::onGroupSelect)
    }
}

@Composable
fun YourStudyGroupScreen(component: StudyGroupComponent) {
    val selectedTab by component.selectedTab.collectAsState()
    val childSidebar by component.childSidebar.subscribeAsState()

    StudyGroupContent(
        selectedTab = selectedTab,
        children = component.childTabs,
        sidebarChild = childSidebar.child?.instance,
        onTabSelect = component::onTabSelect,
        onSidebarClose = component::onSidebarClose
    )
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
        Column {
            OutlinedButton(onClick = { expanded = true }) {
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
}