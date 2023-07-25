package com.denchic45.studiversity.ui.studygroup

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.resource.onLoading
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.profile.ProfileSideBar


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

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun StudyGroupContent(
    selectedTab: Int,
    children: List<StudyGroupComponent.TabChild>,
    sidebarChild: StudyGroupComponent.OverlayChild?,
    onTabSelect: (Int) -> Unit,
    onSidebarClose: () -> Unit,
) {
    Row {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxHeight().weight(3f).padding(end = 24.dp, bottom = 24.dp),
            colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                TabRow(selectedTabIndex = selectedTab,
                    modifier = Modifier.width(396.dp),
                    indicator = { tabPositions ->
                        TabIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab])
                        )
                    },
                    divider = {}
                ) {
                    children.forEachIndexed { index, item ->
                        val selected = selectedTab == index
                        Tab(
                            selected,
                            onClick = { onTabSelect(index) },
                            text = {
                                Text(text = item.title)
                            },
                            unselectedContentColor = Color.DarkGray
                        )
                    }
                }
                Divider()
                Row {
                    Box(
                        modifier = Modifier,
                        contentAlignment = Alignment.TopCenter
                    ) {
                        AnimatedContent(children[selectedTab],
                            transitionSpec = {
                                slideInVertically { -it / 8 } + fadeIn() with slideOutVertically { it / 8 } + fadeOut()
                            }
                        ) {
                            Box(Modifier.fillMaxSize()) {
                                when (val child = it) {
                                    is StudyGroupComponent.TabChild.Members -> {
                                        SelectableStudyGroupMembersScreen(
                                            component = child.component,
                                            selectedItemId = (sidebarChild as? StudyGroupComponent.OverlayChild.Member)?.component?.userId
                                        )
                                    }

                                    is StudyGroupComponent.TabChild.Courses -> {
                                        StudyGroupCoursesScreen(child.component)
                                    }

                                    is StudyGroupComponent.TabChild.Timetable -> {
                                        StudyGroupTimetableScreen(child.component)
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        sidebarChild?.let {
//            Spacer(Modifier.width(MaterialTheme.spacing.small))
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxHeight()
                    .width(360.dp)
                    .padding(end = 24.dp, bottom = 24.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(Modifier.fillMaxWidth(1f)) {
                    when (val child = it) {
                        is StudyGroupComponent.OverlayChild.Member -> ProfileSideBar(
                            Modifier,
                            child.component,
                            onSidebarClose
                        )

                        is StudyGroupComponent.OverlayChild.StudyGroupEditor -> TODO()
                        is StudyGroupComponent.OverlayChild.ScopeMemberEditor -> TODO()
                    }
                }
            }
        }
    }
}