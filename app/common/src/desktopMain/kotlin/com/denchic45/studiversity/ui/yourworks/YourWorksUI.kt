package com.denchic45.studiversity.ui.yourworks

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.AppBarTitle
import com.denchic45.studiversity.ui.CardContent
import com.denchic45.studiversity.ui.CustomAppBar
import com.denchic45.studiversity.ui.IconTitleBox
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.Scaffold
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun YourWorksScreen(component: YourWorksComponent) {
    val children = component.tabChildren
//    val childOverlay by component.childOverlay.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()

    Scaffold(topBar = {
        CustomAppBar(title = {
            AppBarTitle("Мои задания")
        })
    }) {
        CardContent {
            Box {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    val pagerState = rememberPagerState(initialPage = component.selectedTab.value)
                    TabRow(
                        selectedTabIndex = pagerState.currentPage,
                        modifier = Modifier.widthIn(max = 864.dp),
                        indicator = { tabPositions ->
                            TabIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage])
                            )
                        },
                        divider = {}
                    ) {
                        // Add tabs for all of our pages
                        children.forEachIndexed { index, child ->
                            Tab(
                                text = { Text(child.title) },
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
                    Divider()

                    HorizontalPager(state = pagerState, pageCount = children.size) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopCenter
                        ) {
                            when (val child = children[it]) {
                                is YourWorksComponent.TabChild.Upcoming -> {
                                    YourUpcomingWorksScreen(child.component)
                                }

                                is YourWorksComponent.TabChild.Overdue -> {
                                    YourOverdueWorksScreen(child.component)
                                }

                                is YourWorksComponent.TabChild.Submitted -> {
                                    YourSubmittedWorksScreen(child.component)
                                }
                            }
                        }
                    }
                }
//                childOverlay.overlay?.let {
//                    when (val child = it.instance) {
//                        is YourWorksComponent.OverlayChild.CourseWork -> {
//                            CourseWorkScreen(component = child.component)
//                        }
//
//                        is YourWorksComponent.OverlayChild.CourseWorkEditor -> {
//                            CourseWorkEditorScreen(component = child.component)
//                        }
//                    }
//                }
            }
        }
    }
}


@Composable
fun WorksListContent(works: Resource<List<CourseWorkResponse>>, onClick: (UUID, UUID) -> Unit) {
    ResourceContent(resource = works) {
        if (it.isNotEmpty())
            LazyColumn(
                modifier = Modifier.widthIn(max = 960.dp),
                contentPadding = PaddingValues(
                    horizontal = MaterialTheme.spacing.normal,
                    vertical = MaterialTheme.spacing.medium
                )
            ) {
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
    TonalListItem(onClick = onClick) {
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
        Spacer(Modifier.weight(1f))
    }
}

@Composable
fun TonalListItem(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
    interactionSource: MutableInteractionSource = remember(::MutableInteractionSource),
    content: @Composable RowScope.() -> Unit
) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier = modifier.run {
            if (selected) background(MaterialTheme.colorScheme.secondaryContainer, shape)
            else this
        }.clip(shape).height(64.dp).selectable(
            onClick = onClick,
            selected = selected,
            interactionSource = interactionSource,
            indication = rememberRipple(color = MaterialTheme.colorScheme.secondary)
        ).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}