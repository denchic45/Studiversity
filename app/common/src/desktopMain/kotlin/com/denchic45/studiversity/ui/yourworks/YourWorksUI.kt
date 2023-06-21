package com.denchic45.studiversity.ui.yourworks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.IconTitleBox
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar2.AppBarContent
import com.denchic45.studiversity.ui.appbar2.updateAppBarState
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.coursework.CourseWorkScreen
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YourWorksScreen(component: YourWorksComponent) {
    val children = component.tabChildren
    val childOverlay by component.childOverlay.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()

    Surface {
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            val pagerState = rememberPagerState()
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions -> TabIndicator(Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])) },
                edgePadding = 0.dp
            ) {
                // Add tabs for all of our pages
                children.forEachIndexed { index, child ->
                    Tab(
                        text = { Text(child.title) },
                        selected = pagerState.currentPage == index,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        },
                    )
                }
            }

            HorizontalPager(state = pagerState, pageCount = children.size) {
                Box(modifier = Modifier.fillMaxSize()) {
                    when (val child = children[it]) {
                        is YourWorksComponent.TabChild.Upcoming -> YourUpcomingWorksScreen(child.component)
                        is YourWorksComponent.TabChild.Overdue -> YourOverdueWorksScreen(child.component)
                        is YourWorksComponent.TabChild.Submitted -> YourSubmittedWorksScreen(child.component)
                    }
                }
            }
        }

        childOverlay.overlay?.let {
            when (val child = it.instance) {
                is YourWorksComponent.OverlayChild.CourseWork -> {
                    CourseWorkScreen(component = child.component)
                }

                is YourWorksComponent.OverlayChild.CourseWorkEditor -> {
                    CourseWorkEditorScreen(component = child.component)
                }
            }
        } ?: updateAppBarState(AppBarContent(uiTextOf("Мои задания")))
    }
}


@Composable
fun WorksListContent(works: Resource<List<CourseWorkResponse>>, onClick: (UUID, UUID) -> Unit) {
    ResourceContent(resource = works) {
        if (it.isNotEmpty())
            LazyColumn() {
                items(it) { work ->
                    CourseWorkListItem(work) { onClick(work.courseId, work.id) }
                }
            }
        else
            IconTitleBox(
                icon = { Icon(Icons.Outlined.Task, contentDescription = "empty tasks") },
                title = { Text(text = "Нет заданий") }
            )
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
                painter = painterResource("ic_assignment".toDrawablePath()),
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
                Text(text = it.toString("dd MMM"), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}