package com.denchic45.kts.ui.studygroup

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Divider
import androidx.compose.material.TabRow
import androidx.compose.material.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.denchic45.kts.ui.component.TabIndicator

@Composable
fun StudyGroupScreen(component: StudyGroupComponent) {
    val selectedTab by component.selectedTab.collectAsState()
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