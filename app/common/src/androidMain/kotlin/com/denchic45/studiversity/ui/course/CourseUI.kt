package com.denchic45.studiversity.ui.course

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.essenty.lifecycle.doOnStop
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.appbar2.LocalAppBarState
import com.denchic45.studiversity.ui.appbar2.hideAppBar
import com.denchic45.studiversity.ui.courseeditor.CourseEditorScreen
import com.denchic45.studiversity.ui.courseelements.CourseElementsScreen
import com.denchic45.studiversity.ui.coursemembers.CourseMembersScreen
import com.denchic45.studiversity.ui.coursetimetable.CourseTimetableScreen
import com.denchic45.studiversity.ui.coursetopics.CourseTopicsScreen
import com.denchic45.studiversity.ui.coursework.CourseWorkScreen
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorScreen
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.launch


@Composable
fun CourseScreen(component: CourseComponent) {
//    val appBarState = LocalAppBarState.current
//    component.lifecycle.apply {
//        doOnStop {
//            appBarState.expand()
//        }
//    }


    val course by component.course.collectAsState()
    val allowEdit by component.allowEdit.collectAsState(false)
    val children = component.children

    val childStack by component.childStack.subscribeAsState()
    Children(stack = component.childStack) {
        when (val child = childStack.active.instance) {
            is CourseComponent.Child.Topics -> CourseTopicsScreen(
                component = child.component
            )

            is CourseComponent.Child.CourseEditor -> CourseEditorScreen(
                component = child.component,
            )

            is CourseComponent.Child.CourseWork -> CourseWorkScreen(
                component = child.component,
            )

            is CourseComponent.Child.CourseWorkEditor -> CourseWorkEditorScreen(
                component = child.component,
            )

            CourseComponent.Child.None -> {
                hideAppBar()
                CourseContent(
                    course = course,
                    allowEdit = allowEdit,
                    children = children,
                    onBackClick = component::onCloseClick,
                    onCourseEditClick = component::onCourseEditClick,
                    onTopicsEditClick = component::onOpenTopicsClick,
                    onAddWorkClick = component::onAddWorkClick
                )
            }
        }
    }
}

@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalAnimationApi::class
)
@Composable
fun CourseContent(
    course: Resource<CourseResponse>,
    allowEdit: Boolean,
    children: List<CourseComponent.TabChild>,
    onBackClick:()->Unit,
    onCourseEditClick: () -> Unit,
    onTopicsEditClick: () -> Unit,
    onAddWorkClick: () -> Unit,
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
                            Icon(Icons.Filled.MoreVert, "Menu")
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
                navigationIcon = { IconButton(onClick = onBackClick) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "back")
                }},
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = allowEdit,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                FloatingActionButton(onClick = onAddWorkClick) {
                    Icon(
                        painter = rememberVectorPainter(Icons.Default.Add),
                        contentDescription = "add work"
                    )
                }
            }
        }
    ) { paddingValues ->
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
                        is CourseComponent.TabChild.Elements -> CourseElementsScreen(
                            component = child.component
                        )

                        is CourseComponent.TabChild.Members -> CourseMembersScreen(
                            component = child.component
                        )

                        is CourseComponent.TabChild.Timetable -> CourseTimetableScreen(
                            component = child.component
                        )
                    }
                }
            }
        }
    }
}