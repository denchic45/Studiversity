package com.denchic45.studiversity.ui.yourworks

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.AppBarContent
import com.denchic45.studiversity.ui.appbar.updateAppBarState
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.util.toString
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class)
@Composable
fun YourWorksScreen(component: YourCourseWorksComponent) {
    val childPages by component.childPages.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()

    updateAppBarState(AppBarContent(uiTextOf("Мои задания")))

    Surface {
        Column(Modifier.fillMaxSize()) {
            val selectedTab = childPages.selectedIndex
            val pagerState = rememberPagerState(selectedTab, pageCount = childPages.items::size)
            LaunchedEffect(pagerState) {
                snapshotFlow { pagerState.currentPage }.collect { position ->
                    component.onTabSelect(position)
                }
            }
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])) },
                edgePadding = 0.dp
            ) {
                // Add tabs for all of our pages
                childPages.items.forEachIndexed { index, child ->
                    Tab(
                        text = { Text(child.configuration.tabTitle()) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                component.onTabSelect(index)
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }

            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) {
                childPages.items[selectedTab].instance?.let { child ->
                    when (child) {
                        is YourCourseWorksComponent.TabChild.Upcoming -> {
                            YourUpcomingWorksScreen(child.component)
                        }

                        is YourCourseWorksComponent.TabChild.Overdue -> {
                            YourOverdueWorksScreen(child.component)
                        }

                        is YourCourseWorksComponent.TabChild.Submitted -> {
                            YourSubmittedWorksScreen(child.component)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WorksListContent(
    works: Resource<List<CourseWorkResponse>>,
    onClick: (UUID, UUID) -> Unit,
    refreshing: Boolean,
    refreshState: PullRefreshState
) {
    Box(modifier = Modifier.pullRefresh(refreshState)) {
        ResourceContent(resource = works) {
            if (it.isNotEmpty())
                LazyColumn(Modifier.fillMaxSize()) {
                    items(it) { work ->
                        CourseWorkListItem(work) { onClick(work.courseId, work.id) }
                    }
                }
            else {
                val state = rememberScrollState()
                IconTitleBox(
                    icon = { Icon(Icons.Outlined.Task, contentDescription = "empty tasks") },
                    title = { Text(text = "Нет заданий") }
                )
                Box(
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(state)
                )
            }
        }
        PullRefreshIndicator(refreshing, refreshState, Modifier.align(Alignment.TopCenter))
    }
}


@Composable
fun CourseWorkListItem(response: CourseWorkResponse, onClick: () -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(height = 64.dp)
            .clickable(
                onClick = { onClick() })
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Assignment,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
            )
        }
        Spacer(Modifier.width(12.dp))
        Column {
            Text(
                response.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.titleMedium
            )
            response.dueDate?.let {
                Text(
                    text = it.toString("dd MMM"),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (response.late) MaterialTheme.colorScheme.error else Color.Unspecified
                )
            }
        }
    }
}