package com.denchic45.kts.ui.timetableLoader

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Switch
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.ui.timetable.DayTimetableContent
import com.denchic45.kts.util.collectWhenStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TimetablesPublisherScreen(component: TimetablesPublisherComponent) {
    val viewStates by component.viewStates.collectAsState()
    val studyGroups by component.studyGroups.collectAsState()
    val isEdit by component.isEdit.collectAsState()
    val selectedDate by component.selectedDate.collectAsState()
    val selectedGroup by component.selectedGroup.collectAsState()

    val coroutineScope = rememberCoroutineScope()
    val pagerState = rememberPagerState()

    LaunchedEffect(Unit) {
        component.selectedGroup.collect {index->
//            coroutineScope.launch {
                pagerState.animateScrollToPage(index)
//            }
        }
    }

    LaunchedEffect(pagerState) {
        // Collect from the pager state a snapshotFlow reading the currentPage
        snapshotFlow(pagerState::currentPage).collect { page ->
            component.onStudyGroupClick(page)
        }
    }

    Column {
        ListItem(
            headlineContent = { Text("Опубликовать") },
            leadingContent = {
                Icon(
                    imageVector = Icons.Outlined.Send,
                    contentDescription = "publish"
                )
            },
            modifier = Modifier.clickable { component.onPublishClick() }
        )
        ListItem(
            headlineContent = { Text("Режим редактирования") },
            leadingContent = {
                Switch(
                    checked = isEdit,
                    onCheckedChange = component::onEditEnableClick
                )
            },
            modifier = Modifier.clickable { component.onEditEnableClick(!isEdit) }
        )
    }

    TabRow(selectedTabIndex = pagerState.currentPage) {
        // Add tabs for all of our pages
        studyGroups.forEachIndexed { index, group ->
            Tab(
                text = { Text(group.name) },
                selected = pagerState.currentPage == index,
                onClick = {
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
        DayTimetableContent(selectedDate,
            resourceOf(viewState),
            onDateSelect = { component.onDateSelect(it) },
            onEditClick = { component.onPeriodEdit(position, it) }
        )
    }
}