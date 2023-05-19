package com.denchic45.kts.ui.studygroup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.LocalAppBarMediator
import com.denchic45.kts.ui.component.TabIndicator
import com.denchic45.kts.ui.profile.ProfileSideBar


@Composable
fun StudyGroupScreen(component: StudyGroupComponent) {
    val studyGroupResource by component.studyGroup.collectAsState()
    val selectedTab by component.selectedTab.collectAsState()
    val childSidebar by component.childSidebar.subscribeAsState()
    val allowEdit by component.allowEdit.collectAsState()

    val appBarMediator = LocalAppBarMediator.current
    studyGroupResource.onSuccess {
        appBarMediator.title = "Группа ${it.name}"
    }.onLoading {
        appBarMediator.title = "Группа"
    }


    StudyGroupContent(
        selectedTab = selectedTab,
        children = component.childTabs,
        sidebarChild = childSidebar.overlay?.instance,
        onTabSelect = component::onTabSelect,
        onSidebarClose = component::onSidebarClose
    )
}

@Composable
fun StudyGroupContent(
    selectedTab: Int,
    children: List<StudyGroupComponent.TabChild>,
    sidebarChild: StudyGroupComponent.OverlayChild?,
    onTabSelect: (Int) -> Unit,
    onSidebarClose: () -> Unit,
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxSize().padding(end = 24.dp, bottom = 24.dp),
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            TabRow(selectedTabIndex = selectedTab,
                modifier = Modifier.width(396.dp),
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab])) },
                divider = {}) {
                children.forEachIndexed { index, item ->
                    Tab(
                        selectedTab == index,
                        onClick = { onTabSelect(index) },
                        text = { Text(item.title) }
                    )
                }
            }
            Divider()
            Row {
                Box(modifier = Modifier.weight(3f)) {
                    when (val child = children[selectedTab]) {
                        is StudyGroupComponent.TabChild.Members -> {
                            SelectableStudyGroupMembersScreen(child.component)
                        }

                        is StudyGroupComponent.TabChild.Courses -> {
                            StudyGroupCoursesScreen(child.component)
                        }

                        is StudyGroupComponent.TabChild.Timetable -> {
                            StudyGroupTimetableScreen(child.component)
                        }
                    }
                }
                sidebarChild?.let {
                    Box(Modifier.weight(1f)) {
                        when (val child = it) {
                            is StudyGroupComponent.OverlayChild.Member -> ProfileSideBar(
                                Modifier,
                                child.component,
                                onSidebarClose
                            )

                            is StudyGroupComponent.OverlayChild.StudyGroupEditor -> TODO()
                            is StudyGroupComponent.OverlayChild.UserEditor -> TODO()
                        }
                    }
                }
            }
        }
    }
}