package com.denchic45.studiversity.ui.course

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Assignment
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Error
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.pages.ChildPages
import com.denchic45.studiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.domain.resource.ClientError
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.hideAppBar
import com.denchic45.studiversity.ui.component.IconTitleBox
import com.denchic45.studiversity.ui.component.TabIndicator
import com.denchic45.studiversity.ui.courseelements.CourseElementsScreen
import com.denchic45.studiversity.ui.coursemembers.CourseMembersScreen
import com.denchic45.studiversity.ui.coursestudygroups.CourseStudyGroupsScreen
import com.denchic45.studiversity.ui.coursetimetable.CourseTimetableScreen
import com.denchic45.studiversity.ui.theme.spacing
import io.ktor.http.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun CourseScreen(component: CourseComponent) {
    val course by component.course.collectAsState()
    val allowEdit by component.allowEdit.collectAsState(false)
    val childPages by component.childPages.subscribeAsState()

    hideAppBar()
    CourseContent(
        courseResource = course,
        allowEdit = allowEdit,
        childPages = childPages,
        onBackClick = component::onCloseClick,
        onCourseEditClick = component::onEditClick,
        onTopicsEditClick = component::onTopicsClick,
        onAddWorkClick = component::onAddWorkClick,
        onAddMaterialClick = component::onAddMaterialClick
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalDecomposeApi::class)
@Composable
fun CourseContent(
    courseResource: Resource<CourseResponse>,
    allowEdit: Boolean,
    childPages: ChildPages<CourseComponent.TabConfig, CourseComponent.TabChild>,
    onBackClick: () -> Unit,
    onCourseEditClick: () -> Unit,
    onTopicsEditClick: () -> Unit,
    onAddWorkClick: () -> Unit,
    onAddMaterialClick: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
    var fabExpanded by remember { mutableStateOf(false) }

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            LargeTopAppBar(
                title = {
                    courseResource.onSuccess {
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
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Изменить курс") },
                                onClick = {
                                    menuExpanded = false
                                    onCourseEditClick()
                                },
                            )
                            DropdownMenuItem(
                                text = { Text("Изменить темы") },
                                onClick = {
                                    menuExpanded = false
                                    onTopicsEditClick()
                                },
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            val rotation = remember { Animatable(0f) }
            LaunchedEffect(key1 = fabExpanded) {
                rotation.animateTo(if (fabExpanded) 180f else 0f)
            }

            BackHandler(enabled = fabExpanded) {
                fabExpanded = false
            }

            AnimatedVisibility(
                visible = allowEdit,
                enter = scaleIn(),
                exit = scaleOut(),
            ) {
                val containerColor by animateColorAsState(targetValue = if (!fabExpanded) FloatingActionButtonDefaults.containerColor else MaterialTheme.colorScheme.primary)
                val contentColor by animateColorAsState(
                    targetValue = if (!fabExpanded)
                        contentColorFor(backgroundColor = FloatingActionButtonDefaults.containerColor)
                    else MaterialTheme.colorScheme.surface
                )

                Column(horizontalAlignment = Alignment.End) {
                    if (fabExpanded) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier
                        ) {
                            Text(
                                "Материал",
                                modifier = Modifier.padding(end = MaterialTheme.spacing.normal)
                            )
                            Box(
                                modifier = Modifier.size(56.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                SmallFloatingActionButton(onClick = { onAddMaterialClick() }) {
                                    Icon(
                                        painter = rememberVectorPainter(Icons.AutoMirrored.Outlined.MenuBook),
                                        contentDescription = "add material",
                                        modifier = Modifier
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(MaterialTheme.spacing.normal))
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier
                    ) {
                        if (fabExpanded) {
                            Text(
                                "Задание",
                                modifier = Modifier.padding(end = MaterialTheme.spacing.normal)
                            )
                        }
                        FloatingActionButton(
                            onClick = {
                                if (fabExpanded) {
                                    fabExpanded = false
                                    onAddWorkClick()
                                } else {
                                    fabExpanded = true
                                }
                            },
                            containerColor = containerColor,
                            contentColor = contentColor,
                            modifier = Modifier
                        ) {
                            Crossfade(targetState = fabExpanded) {
                                if (it) {
                                    Icon(
                                        painter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Assignment),
                                        contentDescription = "add work",
                                        modifier = Modifier.rotate(rotation.value + 180f)
                                    )
                                } else {
                                    Icon(
                                        painter = rememberVectorPainter(Icons.Default.Add),
                                        contentDescription = "add element",
                                        modifier = Modifier.rotate(rotation.value)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        ResourceContent(resource = courseResource, onError = {
            when (it) {
                is ClientError -> {
                    if (it.response.code == HttpStatusCode.Forbidden.value)
                        IconTitleBox(
                            icon = {
                                Icon(
                                    Icons.Outlined.Lock,
                                    "forbidden",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            title = { Text("У вас нет доступа") }
                        )
                }

                else -> {
                    IconTitleBox(
                        icon = { Icon(Icons.Outlined.Error, "forbidden") },
                        title = { Text("Неизвестная ошибка") }
                    )
                }
            }
        }) {
            Column(Modifier.padding(paddingValues)) {
                val coroutineScope = rememberCoroutineScope()
                val pagerState = rememberPagerState(pageCount = childPages.items::size)

                TabRow(selectedTabIndex = pagerState.currentPage,
                    indicator = { positions ->
                        TabIndicator(Modifier.tabIndicatorOffset(positions[pagerState.currentPage]))
                    }) {
                    childPages.items.forEachIndexed { index, child ->
                        Tab(
                            text = { Text(child.configuration.tabTitle()) },
                            selected = pagerState.currentPage == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                        )
                    }
                }
                HorizontalPager(state = pagerState) {
                    Box(modifier = Modifier.fillMaxHeight()) {
                        childPages.items[childPages.selectedIndex].instance?.let { child ->
                            when (child) {
                                is CourseComponent.TabChild.Elements -> {
                                    CourseElementsScreen(child.component)
                                }

                                is CourseComponent.TabChild.Members -> {
                                    CourseMembersScreen(child.component, allowEdit)
                                }

                                is CourseComponent.TabChild.Timetable -> {
                                    CourseTimetableScreen(child.component)
                                }

                                is CourseComponent.TabChild.StudyGroups -> {
                                    CourseStudyGroupsScreen(child.component)
                                }
                            }
                        }
                    }
                }
            }
        }
        Overlap(fabExpanded, MaterialTheme.colorScheme.surface) { fabExpanded = false }
    }
}

@Composable
private fun Overlap(enable: Boolean, color: Color = Color.Black, onDismiss: () -> Unit) {
    if (enable) {
        val interactionSource = remember { MutableInteractionSource() }
        Canvas(
            Modifier
                .fillMaxSize()
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onDismiss
                )
        ) {
            drawRect(color.copy(alpha = 0.32f))
        }
    }
}