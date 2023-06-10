package com.denchic45.studiversity.ui.yourstudygroups

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.components.ExposedDropdownMenuDefaults
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.ui.studygroup.StudyGroupContent
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import java.util.UUID


@Composable
fun YourStudyGroupsScreen(component: YourStudyGroupsComponent) {
    val selectedStudyGroup by component.selectedStudyGroup.collectAsState()
    val studyGroups by component.studyGroups.collectAsState()

    val appBarMediator = LocalAppBarMediator.current

    appBarMediator.title = when (val selected = selectedStudyGroup) {
        is Resource.Success -> "Группа ${selected.value.name}"
        else -> "Группа"
    }
    appBarMediator.content = {
        studyGroups.onSuccess { groups ->
            Spacer(Modifier.width(MaterialTheme.spacing.normal))
            StudyGroupSpinner(groups, selectedStudyGroup, component::onGroupSelect)
//            Spacer(Modifier.weight(1f))
//            IconButton(onClick = {}) {
//                Icon(
//                    painter = painterResource("ic_settings".toDrawablePath()),
//                    tint = Color.DarkGray,
//                    contentDescription = ""
//                )
//            }
        }
    }

    val childStudyGroup by component.childStudyGroup.subscribeAsState()
    childStudyGroup.overlay?.instance?.let {
        YourStudyGroupScreen(it)
    }
}

@Composable
fun YourStudyGroupScreen(component: StudyGroupComponent) {
    val selectedTab by component.selectedTab.collectAsState()
    val childSidebar by component.childSidebar.subscribeAsState()

    StudyGroupContent(
        selectedTab = selectedTab,
        children = component.childTabs,
        sidebarChild = childSidebar.overlay?.instance,
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