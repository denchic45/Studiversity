package com.denchic45.kts.ui.course

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.essenty.lifecycle.doOnStart
import com.arkivanov.essenty.lifecycle.doOnStop
import com.denchic45.kts.R
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.UiIcon
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.courseelements.CourseElementsScreen
import com.denchic45.kts.ui.coursemembers.CourseMembersScreen
import com.denchic45.kts.ui.fab.FabInteractor
import com.denchic45.kts.ui.fab.FabState
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.launch


@Composable
fun CourseScreen(
    component: CourseComponent,
    fabInteractor: FabInteractor,
    appBarInteractor: AppBarInteractor
) {
    component.lifecycle.apply {
        doOnStart {
            appBarInteractor.set(AppBarState(visible = false))
            fabInteractor.set(
                FabState(
                    icon = UiIcon.Resource(R.drawable.ic_add),
                    onClick = component::onFabClick
                )
            )
        }
        doOnStop {
            appBarInteractor.set(AppBarState(visible = true))
            fabInteractor.update { it.copy(visible = false) }
        }
    }

    val course by component.course.collectAsState()
    val allowEdit by component.allowEdit.collectAsState(false)
    val children by component.children.collectAsState()

    CourseContent(
        course = course,
        allowEdit = allowEdit,
        childrenResource = children,
        onCourseEditClick = component::onCourseEditClick,
        onTopicsEditClick = component::onTopicEditClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun CourseContent(
    course: Resource<CourseResponse>,
    allowEdit: Boolean,
    childrenResource: Resource<List<CourseComponent.Child>>,
    onCourseEditClick: () -> Unit,
    onTopicsEditClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState()
    )

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    course.onSuccess {
                        Text(text = it.name)
                    }
                },
                actions = {
                    if (allowEdit) {
                        var menuExpanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { menuExpanded = !menuExpanded }) {
                            Icon(Icons.Filled.MoreVert, "Меню")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            offset = DpOffset(x = (-84).dp, y = 0.dp),
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = {
                                    Text("Изменить курс")
                                },
                                onClick = {
                                    menuExpanded = false
                                    onCourseEditClick()
                                },
                            )
                            DropdownMenuItem(
                                text = {
                                    Text("Изменить темы")
                                },
                                onClick = {
                                    menuExpanded = false
                                    onTopicsEditClick()
                                },
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { paddingValues ->
        childrenResource.onSuccess { children ->
            Column(Modifier.padding(paddingValues)) {
                val coroutineScope = rememberCoroutineScope()
                val pagerState = rememberPagerState()
                TabRow(selectedTabIndex = pagerState.currentPage,
                    indicator = { positions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(positions[pagerState.currentPage])
                        )
                    }) {
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
                HorizontalPager(
                    state = pagerState,
                    pageCount = children.size,
                ) {
                    Box(modifier = Modifier.fillMaxHeight()) {
                        when (val child = children[it]) {
                            is CourseComponent.Child.Elements -> CourseElementsScreen(
                                component = child.component
                            )

                            is CourseComponent.Child.Members -> CourseMembersScreen(
                                component = child.component
                            )
                        }
                    }
                }
            }
        }
    }
}