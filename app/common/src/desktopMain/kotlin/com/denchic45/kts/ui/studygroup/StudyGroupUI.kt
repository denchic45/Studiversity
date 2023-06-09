package com.denchic45.kts.ui.studygroup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.graphics.Color
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
                            Modifier.tabIndicatorOffset(
                                tabPositions[selectedTab]
                            )
                        )
                    },
                    divider = {}) {
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
                modifier = Modifier.fillMaxHeight().weight(1f).padding(end = 24.dp, bottom = 24.dp),
                colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Box(Modifier.weight(1f)) {
                    when (val child = it) {
                        is StudyGroupComponent.OverlayChild.Member -> ProfileSideBar(
                            Modifier,
                            child.component,
                            onSidebarClose
                        )

                        is StudyGroupComponent.OverlayChild.StudyGroupEditor -> TODO()
                    }
                }
            }
        }
    }
}