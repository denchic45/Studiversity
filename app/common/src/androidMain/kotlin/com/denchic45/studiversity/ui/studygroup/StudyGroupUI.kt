package com.denchic45.studiversity.ui.studygroup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.HorizontalDivider
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
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.denchic45.studiversity.domain.resource.ifSuccess
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.DropdownMenuItem2
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorScreen
import com.denchic45.studiversity.ui.uiTextOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalDecomposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun StudyGroupScreen(component: StudyGroupComponent) {
    val childSlot by component.childSlot.subscribeAsState()
    val studyGroupResource by component.studyGroup.collectAsState()
    val allowEditResource by component.allowEdit.collectAsState()

    val studyGroupName = studyGroupResource.ifSuccess { it.name }.orEmpty()
    val allowEdit = allowEditResource.ifSuccess { it } ?: false

    val childPages by component.childPages.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = childPages.items::size)

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            indicator = { tabPositions ->
                TabIndicator(Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]))
            },
            divider = {}) {
            childPages.items.forEachIndexed { index, item ->
                Tab(
                    text = { Text(item.configuration.tabTitle()) },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(index)
                            component.onTabSelect(index)
                        }
                    },
                )
            }
        }
        HorizontalDivider()
        HorizontalPager(
            state = pagerState,
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                childPages.items[childPages.selectedIndex].instance?.let { child ->
                    when (child) {
                        is StudyGroupComponent.TabChild.Members -> StudyGroupMembersScreen(child.component)
                        is StudyGroupComponent.TabChild.Courses -> StudyGroupCoursesScreen(child.component)
                        is StudyGroupComponent.TabChild.Timetable -> StudyGroupTimetableScreen(child.component)
                    }
                }
            }
        }
    }

    childSlot.child.let {
        when (val child = it?.instance) {
            is StudyGroupComponent.SlotChild.StudyGroupEditor -> StudyGroupEditorScreen(child.component)
            StudyGroupComponent.SlotChild.Info -> studyGroupResource.onSuccess { studyGroup ->
                StudyGroupInfoDialog(studyGroup, component::onDialogClose)
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
//                            DropdownMenuItem2(
//                                title = uiTextOf("Добавить участника"),
//                                onClick = component::onAddMemberClick
//                            )
                        ) else emptyList()
                    )
                )
            }
        }
    }
}

fun StudyGroupComponent.TabConfig.tabTitle() = when (this) {
    StudyGroupComponent.TabConfig.Members -> "Участники"
    StudyGroupComponent.TabConfig.Courses -> "Курсы"
    StudyGroupComponent.TabConfig.Timetable -> "Расписание"
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun StudyGroupContent(
    children: List<StudyGroupComponent.TabChild>,
    onTabSelect: (Int) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = children::size)

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
        HorizontalDivider()
        HorizontalPager(
            state = pagerState,
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