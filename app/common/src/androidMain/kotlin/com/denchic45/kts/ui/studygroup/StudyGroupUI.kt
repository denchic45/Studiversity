package com.denchic45.kts.ui.studygroup

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.arkivanov.essenty.lifecycle.doOnStart
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.component.TabIndicator
import com.denchic45.kts.ui.uiTextOf

@Composable
fun StudyGroupScreen(component: StudyGroupComponent, appBarInteractor: AppBarInteractor) {
    val selectedTab by component.selectedTab.collectAsState()
    val studyGroup by component.studyGroup.collectAsState()
    val allowEdit by component.allowEdit.collectAsState()

    component.lifecycle.doOnStart {
        studyGroup.onSuccess { studyGroup ->
            appBarInteractor.set(
                when (val resource = allowEdit) {
                    is Resource.Success -> AppBarState(
                        title = uiTextOf(studyGroup.name),
                        actionsUI = {
                            if (resource.value) {
                                IconButton(onClick = component::onEditClick) {
                                    Icon(Icons.Outlined.Edit, null)
                                }
                            }
                        }
                    )

                    is Resource.Error, Resource.Loading -> AppBarState(
                        title = uiTextOf(studyGroup.name)
                    )
                }
            )
        }
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
            backgroundColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.primary,
            indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab])) },
            divider = {}) {
//            val tabs by response.tabs.collectAsState()
            children.forEachIndexed { index, item ->
                Tab(
                    selectedTab == index,
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