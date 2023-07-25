package com.denchic45.studiversity.ui.studygroup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Divider
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.resource.ifSuccess
import com.denchic45.studiversity.ui.ScopeMemberEditorScreen
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.DropdownMenuItem2
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.profile.ProfileScreen
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorScreen
import com.denchic45.studiversity.ui.uiTextOf
import kotlinx.coroutines.launch

@Composable
fun StudyGroupScreen(component: StudyGroupComponent) {
    val childSidebar by component.childSidebar.subscribeAsState()
    val studyGroup by component.studyGroup.collectAsState()
    val allowEditResource by component.allowEdit.collectAsState()

    val studyGroupName = studyGroup.ifSuccess { it.name } ?: ""
    val allowEdit = allowEditResource.ifSuccess { it } ?: false

    StudyGroupContent(
        children = component.childTabs,
        onTabSelect = component::onTabSelect
    )

    childSidebar.overlay.let {
        when (val child = it?.instance) {
            is StudyGroupComponent.OverlayChild.Member -> ProfileScreen(child.component)
            is StudyGroupComponent.OverlayChild.StudyGroupEditor -> StudyGroupEditorScreen(child.component)
            is StudyGroupComponent.OverlayChild.ScopeMemberEditor -> {
                ScopeMemberEditorScreen(child.component)
            }
            null -> {
                updateAppBarState(
                    studyGroupName, allowEdit, AppBarContent(
                        title = uiTextOf(studyGroupName),
                        dropdownItems = if (allowEdit) listOf(
                            DropdownMenuItem2(
                                title = uiTextOf("Изменить"),
                                onClick = component::onEditClick
                            ),
                            DropdownMenuItem2(
                                title = uiTextOf("Добавить участника"),
                                onClick = component::onAddMemberClick
                            )
                        ) else emptyList()
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudyGroupContent(
    children: List<StudyGroupComponent.TabChild>,
    onTabSelect: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabIndicator(Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]))
            },
            divider = {}) {
            children.forEachIndexed { index, item ->
                Tab(
                    text = { Text(item.title) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                            onTabSelect(index)
                        }
                    },
                )
            }
        }
        Divider()
        HorizontalPager(
            state = pagerState,
            pageCount = children.size,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                when (val child = children[it]) {
                    is StudyGroupComponent.TabChild.Members -> StudyGroupMembersScreen(child.component)
                    is StudyGroupComponent.TabChild.Courses -> StudyGroupCoursesScreen(child.component)
                    is StudyGroupComponent.TabChild.Timetable -> StudyGroupTimetableScreen(child.component)
                }
            }
        }
    }
}