package com.denchic45.kts.ui.timetableLoader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import com.denchic45.kts.ui.timetable.DayTimetableContent
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetablesPublisherScreen(component: TimetablesPublisherComponent) {
    val viewStates by component.viewStates.collectAsState()
    val studyGroups by component.studyGroups.collectAsState()
    val selectedDate by component.selectedDate.collectAsState()
    val selectedGroup by component.selectedGroup.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    TabRow(
        selectedTabIndex = pagerState.currentPage
    ) {
        // Add tabs for all of our pages
        studyGroups.forEachIndexed { index, group ->
            Tab(
                text = { Text(group.name) },
                selected = pagerState.currentPage == index,
                onClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                    component.onStudyGroupClick(index)
                },
            )
        }
    }

    HorizontalPager(
        pageCount = viewStates.size,
        state = pagerState
    ) { position ->
        val viewState by viewStates[position].collectAsState()
        DayTimetableContent(selectedDate, viewState)
    }
}