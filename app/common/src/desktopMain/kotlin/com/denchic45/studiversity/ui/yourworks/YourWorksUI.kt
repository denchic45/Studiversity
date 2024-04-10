package com.denchic45.studiversity.ui.yourworks

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Assignment
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.ui.BlockContent
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.ScreenScaffold
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.main.AppBarTitle
import com.denchic45.studiversity.ui.main.CustomAppBar
import com.denchic45.studiversity.ui.main.NavigationIconBack
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.util.toString
import kotlinx.coroutines.launch
import java.util.*


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun YourWorksScreen(component: YourCourseWorksComponent) {
    val childPages by component.childPages.subscribeAsState()
    val coroutineScope = rememberCoroutineScope()

    ScreenScaffold(topBar = {
        CustomAppBar(
            title = { AppBarTitle("Мои задания") },
            navigationContent = { NavigationIconBack(component::onBack) })
    }) {
        BlockContent {
            Box {
                Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    val selectedTab = childPages.selectedIndex
                    TabRow(
                        selectedTabIndex = selectedTab,
                        modifier = Modifier.widthIn(max = 864.dp),
                        indicator = { tabPositions ->
                            TabIndicator(Modifier.tabIndicatorOffset(tabPositions[selectedTab]))
                        },
                        divider = {}
                    ) {
                        // Add tabs for all of our pages
                        childPages.items.forEachIndexed { index, child ->
                            Tab(
                                text = { Text(child.configuration.tabTitle()) },
                                selected = selectedTab == index,
                                onClick = {
                                    coroutineScope.launch {
                                        component.onTabSelect(index)
                                    }
                                },
                            )
                        }
                    }
                    Divider()
                    childPages.items[selectedTab].instance?.let { child ->
                        AnimatedContent(child,
                            transitionSpec = {
                                (slideInVertically { -it / 8 } + fadeIn()).togetherWith(slideOutVertically { it / 8 } + fadeOut())
                            }
                        ) {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.TopCenter
                            ) {
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
        }
    }
}

@Composable
fun WorksContent(works: Resource<List<CourseWorkResponse>>, onClick: (UUID, UUID) -> Unit) {
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
//                painter = painterResource("ic_assignment".toDrawablePath()), // TODO: использовать ресурсы compose multiplatform
                Icons.Outlined.Assignment,
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
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
) {
    val shape = MaterialTheme.shapes.large
    Row(
        modifier = modifier.run {
            if (selected) background(MaterialTheme.colorScheme.secondaryContainer, shape)
            else this
        }.clip(shape)
            .height(64.dp)
            .selectable(
                onClick = onClick,
                selected = selected,
                interactionSource = interactionSource,
                indication = rememberRipple(color = MaterialTheme.colorScheme.secondary)
            ).padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}