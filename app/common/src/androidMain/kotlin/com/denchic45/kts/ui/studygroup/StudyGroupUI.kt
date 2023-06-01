package com.denchic45.kts.ui.studygroup

import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.domain.ifSuccess
import com.denchic45.kts.ui.appbar2.ActionMenuItem2
import com.denchic45.kts.ui.appbar2.AppBarContent
import com.denchic45.kts.ui.appbar2.LocalAppBarState
import com.denchic45.kts.ui.component.TabIndicator
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.kts.ui.uiTextOf

@Composable
fun StudyGroupScreen(component: StudyGroupComponent) {
    val selectedTab by component.selectedTab.collectAsState()
    val studyGroup by component.studyGroup.collectAsState()
    val allowEditResource by component.allowEdit.collectAsState()

    val appBarState = LocalAppBarState.current

    val studyGroupName = studyGroup.ifSuccess { it.name } ?: ""
    val allowEdit = allowEditResource.ifSuccess { it } ?: false



    component.lifecycle.doOnStart {
        appBarState.content = AppBarContent(
            title = uiTextOf(studyGroupName),
            actionItems = if (allowEdit) listOf(
                ActionMenuItem2(
                    icon = uiIconOf(Icons.Outlined.Edit),
                    onClick = component::onEditClick
                )
            ) else emptyList()
        )
//        studyGroup.onSuccess { studyGroup ->
//            .set(
//                when (val resource = allowEditResource) {
//                    is Resource.Success -> AppBarState(
//                        title = uiTextOf(studyGroup.name),
//                        actionsUI = {
//                            if (resource.value) {
//                                IconButton(onClick = component::onEditClick) {
//                                    Icon(Icons.Outlined.Edit, null)
//                                }
//                            }
//                        }
//                    )
//
//                    is Resource.Error, Resource.Loading -> AppBarState(
//                        title = uiTextOf(studyGroup.name)
//                    )
//                }
//            )
//        }
    }

    StudyGroupContent(
        selectedTab = selectedTab,
        children = component.childTabs,
        onTabSelect = component::onTabSelect
    )
}

@Composable
fun StudyGroupContent(
    selectedTab: Int,
    children: List<StudyGroupComponent.TabChild>,
    onTabSelect: (Int) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TabRow(
            selectedTabIndex = selectedTab,
            indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab])) },
            divider = {}) {
            children.forEachIndexed { index, item ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { onTabSelect(index) },
                    text = { Text(item.title) }
                )
            }
        }
        Divider()
        when (val child = children[selectedTab]) {
            is StudyGroupComponent.TabChild.Members -> StudyGroupMembersScreen(child.component)
            is StudyGroupComponent.TabChild.Courses -> StudyGroupCoursesScreen(child.component)
            is StudyGroupComponent.TabChild.Timetable -> StudyGroupTimetableScreen(child.component)
        }
    }
}