package com.denchic45.studiversity.ui.main

import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.SpaceDashboard
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.MainComponent
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.appbar.LocalAppBarState
import com.denchic45.studiversity.ui.appbar.NavigationIcon
import com.denchic45.studiversity.ui.appbar.expandAppBar
import com.denchic45.studiversity.ui.confirm.ConfirmDialog
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.course.CourseScreen
import com.denchic45.studiversity.ui.coursework.CourseWorkScreen
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorScreen
import com.denchic45.studiversity.ui.get
import com.denchic45.studiversity.ui.getPainter
import com.denchic45.studiversity.ui.navigation.OverlayChild
import com.denchic45.studiversity.ui.profile.ProfileScreen
import com.denchic45.studiversity.ui.root.RootStackScreen
import com.denchic45.studiversity.ui.schedule.ScheduleScreen
import com.denchic45.studiversity.ui.settings.SettingsScreen
import com.denchic45.studiversity.ui.studygroup.StudyGroupScreen
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.yourworks.YourWorksScreen
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.seiko.imageloader.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScreen(
    component: MainComponent,
    activity: ComponentActivity,
    confirmDialogInteractor: ConfirmDialogInteractor,
) {

    val systemUiController = rememberSystemUiController()
    val useDarkIcons = !isSystemInDarkTheme()
    val colorScheme = MaterialTheme.colorScheme

    val absoluteElevation = LocalAbsoluteTonalElevation.current
    CompositionLocalProvider(
        LocalAbsoluteTonalElevation provides absoluteElevation
    ) {
        LaunchedEffect(systemUiController, useDarkIcons) {
            // Update all of the system bar colors to be transparent, and use
            // dark icons if we're in light theme
            systemUiController.setStatusBarColor(
                color = colorScheme.surface,
                darkIcons = useDarkIcons
            )

            systemUiController.setNavigationBarColor(
                color = Color.Transparent,
                darkIcons = useDarkIcons
            )
        }
    }

    val sizeClass = calculateWindowSizeClass(activity)
    when (sizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            CompactMainScreen(
                component,
                activity = activity
            )
        }

        WindowWidthSizeClass.Medium -> {
            MediumMainScreen(component, activity = activity)
        }

        WindowWidthSizeClass.Expanded -> {
            MediumMainScreen(component, activity = activity)
        }
    }
    ConfirmDialog(confirmDialogInteractor)
}

@Composable
private fun CompactMainScreen(component: MainComponent, activity: ComponentActivity) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val availableScreens by component.availableScreens.collectAsState()

    BackHandler(drawerState.isOpen) {
        coroutineScope.launch {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(component, coroutineScope, drawerState)
        }
    ) {
        val stack by component.stack.subscribeAsState()
        val appBarState = LocalAppBarState.current
        appBarState.navigationIcon = when (val child = stack.active.instance) {
            is MainComponent.PrimaryChild -> {
                val hasChildren by child.component.hasChildrenFlow().collectAsState(initial = false)
                if (hasChildren) NavigationIcon.BACK
                else NavigationIcon.TOGGLE
            }

            else -> NavigationIcon.BACK
        }

        Scaffold(
            topBar = {
                TopBarContent(activity, drawerState)
            },
            bottomBar = {
                val instance = stack.active.instance
                if (instance is MainComponent.ExtraChild) return@Scaffold

                val moreNavItemThanOne = availableScreens.yourStudyGroups

                if (moreNavItemThanOne) {
                    NavigationBar {
                        NavigationBarItem(selected = instance is MainComponent.Child.YourTimetables,
                            onClick = component::onTimetableClick,
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_timetable),
                                    contentDescription = "your timetables menu"
                                )
                            },
                            label = { Text("Расписание") }
                        )

                        if (availableScreens.yourStudyGroups) {
                            NavigationBarItem(selected = instance is MainComponent.Child.YourStudyGroups,
                                onClick = component::onStudyGroupsClick,
                                icon = {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_study_group),
                                        contentDescription = "your groups menu"
                                    )
                                },
                                label = { Text("Группы") }
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            ScreenContainer(paddingValues, stack)
            Box(Modifier.padding(paddingValues)) {
                val overlay by component.childOverlay.subscribeAsState()
                overlay.overlay?.let {
                    when (val child = it.instance) {
                        is OverlayChild.Confirm -> with(child.config) {
                            TODO()
//                        ConfirmDialog(
//                            title = title,
//                            text = text,
//                            onConfirm = {},
//                            onDismiss = component::onOverlayDismiss
//                        )
                        }

                        is OverlayChild.YourProfile -> ProfileScreen(child.component)
                        is OverlayChild.Settings -> SettingsScreen(child.component)
                        is OverlayChild.Schedule -> ScheduleScreen(child.component)
                    }
                } ?: expandAppBar()
            }
        }
    }
}


@Composable
fun MediumMainScreen(
    component: MainComponent,
    activity: ComponentActivity,
) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(component, coroutineScope, drawerState)
        }
    ) {
        val stack by component.stack.subscribeAsState()

        val appBarState = LocalAppBarState.current
        appBarState.navigationIcon = when (val child = stack.active.instance) {
            is MainComponent.PrimaryChild -> {
                val hasChildren by child.component.hasChildrenFlow().collectAsState(initial = false)
                if (hasChildren) NavigationIcon.BACK
                else NavigationIcon.NOTHING
            }

            else -> NavigationIcon.BACK
        }

        Row {
            val instance = stack.active.instance
            if (instance is MainComponent.PrimaryChild) {
                val availableScreens by component.availableScreens.collectAsState()
                NavigationRail(header = {
                    Spacer(modifier = Modifier.height(1.dp))
                    IconButton(
                        onClick = { coroutineScope.launch { drawerState.open() } }) {
                        Icon(
                            imageVector = Icons.Outlined.Menu,
                            contentDescription = "menu"
                        )
                    }
                }, containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)) {
                    NavigationRailItem(selected = instance is MainComponent.Child.YourTimetables,
                        onClick = component::onTimetableClick,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_timetable),
                                contentDescription = "your timetables menu"
                            )
                        },
                        label = { Text("Расписание") }
                    )

                    if (availableScreens.yourStudyGroups) {
                        NavigationRailItem(selected = instance is MainComponent.Child.YourStudyGroups,
                            onClick = component::onStudyGroupsClick,
                            icon = {
                                Icon(
                                    painter = painterResource(R.drawable.ic_study_group),
                                    contentDescription = "your groups menu"
                                )
                            },
                            label = { Text("Группы") }
                        )
                    }
                }
            }

            Scaffold(topBar = { TopBarContent(activity, drawerState) }) {
                ScreenContainer(paddingValues = it, stack = stack)
            }
        }
    }
}


@Composable
private fun DrawerContent(
    component: MainComponent,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState,
) {
    fun closeDrawer() = coroutineScope.launch {
        delay(200)
        drawerState.close()
    }

    val availableScreens by component.availableScreens.collectAsState()
    val stack by component.stack.subscribeAsState()
    ModalDrawerSheet(Modifier.requiredWidth(300.dp)) {
        val scrollState = rememberScrollState()
        Column(
            Modifier
                .padding(horizontal = 8.dp)
                .verticalScroll(scrollState)
        ) {
            val userInfo by component.userInfo.collectAsState()
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clickable {
                        component.onProfileClick()
                        closeDrawer()
                    }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                userInfo.onSuccess { info ->
                    Image(
                        painter = rememberAsyncImagePainter(info.avatarUrl),
                        contentDescription = "user avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape),
                    )
                    Spacer(Modifier.width(16.dp))
                    Text(
                        text = info.fullName,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
            Divider(Modifier.padding(vertical = 4.dp))

            if (availableScreens.yourWorks) {
                NavigationDrawerItem(
                    label = {
                        Text(
                            "Задания",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    icon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_works),
                            contentDescription = "works"
                        )
                    },
                    selected = stack.active.instance is MainComponent.Child.YourWorks,
                    onClick = {
                        component.onWorksClick()
                        closeDrawer()
                    }
                )
            }
            val context = LocalContext.current
            val overlay by component.childOverlay.subscribeAsState()
            NavigationDrawerItem(
                label = {
                    Text(
                        "Расписание звонков",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = "schedule"
                    )
                },
                selected = overlay.overlay?.instance is OverlayChild.Schedule,
                onClick = {
                    component.onScheduleClick()
                    closeDrawer()
                }
            )

            val yourCourses by component.yourCourses.collectAsState()

            ResourceContent(resource = yourCourses) { courses ->
                if (courses.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Text(
                            text = "Курсы",
                            style = MaterialTheme.typography.titleSmall
                        )
                    }
                    courses.forEach { course ->
                        NavigationDrawerItem(
                            label = {
                                Text(
                                    course.name,
                                    style = MaterialTheme.typography.labelLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            icon = {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(colorResource(R.color.blue))
                                        .padding(8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = course.name.first().uppercase(),
                                        style = MaterialTheme.typography.labelLarge,
                                        color = Color.White
                                    )
                                }
                            },
                            selected = false,
                            onClick = {
                                component.onCourseClick(course.id)
                                closeDrawer()
                            })
                    }
                }
                Divider(Modifier.padding(vertical = 4.dp))
            }
            if (availableScreens.adminDashboard) {
                NavigationDrawerItem(
                    label = {
                        Text(
                            "Панель управления",
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    icon = {
                        Icon(
                            imageVector = Icons.Outlined.SpaceDashboard,
                            contentDescription = "admin dashboard"
                        )
                    },
                    selected = stack.active.instance is MainComponent.Child.AdminDashboard,
                    onClick = {
                        component.onAdminDashboardClick()
                        closeDrawer()
                    }
                )
            }

            NavigationDrawerItem(
                label = {
                    Text(
                        "Настройки",
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_settings),
                        contentDescription = "settings"
                    )
                },
                selected = overlay.overlay?.instance is OverlayChild.Settings,
                onClick = {
                    component.onSettingsClick()
                    closeDrawer()
                }
            )
            Spacer(modifier = Modifier.height(MaterialTheme.spacing.extraLarge))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBarContent(activity: ComponentActivity, drawerState: DrawerState) {
    val appBarState = LocalAppBarState.current
    val coroutineScope = rememberCoroutineScope()
    TopAppBar(
        title = { Text(appBarState.content.title.get(LocalContext.current)) },
        navigationIcon = {
            when (appBarState.navigationIcon) {
                NavigationIcon.TOGGLE -> IconButton(
                    onClick = { coroutineScope.launch { drawerState.open() } }) {
                    Icon(
                        imageVector = Icons.Outlined.Menu,
                        contentDescription = "menu"
                    )
                }

                NavigationIcon.BACK -> IconButton(
                    onClick = { activity.onBackPressedDispatcher.onBackPressed() }) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = "back"
                    )
                }

                NavigationIcon.NOTHING -> {}
            }
        },
        actions = {
            appBarState.content.actionItems.forEach { actionMenuItem ->
                val contentDescription = actionMenuItem.title
                    ?.get(LocalContext.current)
                IconButton(
                    onClick = { actionMenuItem.onClick() },
                    enabled = actionMenuItem.enabled
                ) {
                    Icon(
                        painter = actionMenuItem.icon.getPainter(),
                        contentDescription = contentDescription
                    )
                }
            }
            var menuExpanded by remember { mutableStateOf(false) }
            if (appBarState.content.dropdownItems.isNotEmpty()) {
                IconButton(onClick = { menuExpanded = !menuExpanded }) {
                    Icon(Icons.Filled.MoreVert, "menu")
                }
            }
            DropdownMenu(
                expanded = menuExpanded,
                onDismissRequest = { menuExpanded = false },
            ) {
                appBarState.content.dropdownItems.forEach { item ->
                    DropdownMenuItem(
                        text = { Text(item.title.get(LocalContext.current)) },
                        onClick = {
                            menuExpanded = false
                            item.onClick()
                        },
                    )
                }
            }
        },
        scrollBehavior = appBarState.scrollBehavior,
    )
}

@Composable
private fun ScreenContainer(
    paddingValues: PaddingValues,
    stack: ChildStack<MainComponent.Config, MainComponent.Child>,
) {
//    Children(
//        modifier = Modifier.padding(paddingValues), stack = stack,
//    ) {
    Crossfade(modifier = Modifier.padding(paddingValues), targetState = stack.active) {
        println("children: ${it.instance}")
        when (val child = it.instance) {
            is MainComponent.Child.YourStudyGroups -> RootStackScreen(child.component)
            is MainComponent.Child.YourTimetables -> RootStackScreen(child.component)
            is MainComponent.Child.AdminDashboard -> RootStackScreen(child.component)
            is MainComponent.Child.YourCourse -> CourseScreen(child.component)
            is MainComponent.Child.Course -> CourseScreen(child.component)
            is MainComponent.Child.StudyGroup -> StudyGroupScreen(child.component)
            is MainComponent.Child.YourWorks -> YourWorksScreen(child.component)
            is MainComponent.Child.CourseWork -> CourseWorkScreen(child.component)
            is MainComponent.Child.CourseWorkEditor -> CourseWorkEditorScreen(child.component)
        }
    }
//    }
}