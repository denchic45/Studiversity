package com.denchic45.kts.ui.main

import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetpack.stack.Children
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.arkivanov.decompose.router.stack.ChildStack
import com.denchic45.kts.R
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.MainComponent
import com.denchic45.kts.ui.ResourceContent
import com.denchic45.kts.ui.appbar.LocalAppBarInteractor
import com.denchic45.kts.ui.appbar.NavigationIcon
import com.denchic45.kts.ui.confirm.ConfirmDialog
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.get
import com.denchic45.kts.ui.getPainter
import com.denchic45.kts.ui.root.YourStudyGroupsRootScreen
import com.denchic45.kts.ui.root.YourTimetablesRootScreen
import com.seiko.imageloader.rememberAsyncImagePainter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun MainScreen(
    component: MainComponent,
    activity: AppCompatActivity,
    confirmDialogInteractor: ConfirmDialogInteractor
) {
    val sizeClass = calculateWindowSizeClass(activity)
    when (sizeClass.widthSizeClass) {
        WindowWidthSizeClass.Compact -> {
            CompactMainScreen(
                component = component,
                activity = activity
            )
        }

        WindowWidthSizeClass.Medium -> {
            MediumMainScreen(component = component, activity = activity)
        }
    }
    ConfirmDialog(confirmDialogInteractor)
}

@Composable
private fun CompactMainScreen(component: MainComponent, activity: AppCompatActivity) {
    val coroutineScope = rememberCoroutineScope()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(component, coroutineScope, drawerState)
        }
    ) {
        val stack by component.stack.subscribeAsState()
        Scaffold(
            topBar = {
                TopBarContent(activity)
            },
            bottomBar = {
                val instance = stack.active.instance
                if (instance is MainComponent.ExtraChild) return@Scaffold

                NavigationBar {
                    NavigationBarItem(selected = instance is MainComponent.RootChild.YourTimetables,
                        onClick = component::onTimetableClick,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_timetable),
                                contentDescription = "your timetables menu"
                            )
                        },
                        label = { Text("Расписание") }
                    )
                    NavigationBarItem(selected = instance is MainComponent.RootChild.YourStudyGroups,
                        onClick = component::onTimetableClick,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_group),
                                contentDescription = "your groups menu"
                            )
                        },
                        label = { Text("Группы") }
                    )
                }
            }
        ) { paddingValues ->
            ScreenContainer(paddingValues, stack)
        }
    }
}


@Composable
fun MediumMainScreen(
    component: MainComponent,
    activity: AppCompatActivity,
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
        Row {
            val instance = stack.active.instance
            if (instance is MainComponent.PrimaryChild) {
                val availableScreens by component.availableScreens.collectAsState()
                NavigationRail {
                    NavigationRailItem(selected = instance is MainComponent.RootChild.YourTimetables,
                        onClick = component::onTimetableClick,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_timetable),
                                contentDescription = "your timetables menu"
                            )
                        },
                        label = { Text("Расписание") }
                    )

                    if (availableScreens.yourStudyGroups)
                    NavigationRailItem(selected = instance is MainComponent.RootChild.YourStudyGroups,
                        onClick = component::onTimetableClick,
                        icon = {
                            Icon(
                                painter = painterResource(R.drawable.ic_group),
                                contentDescription = "your groups menu"
                            )
                        },
                        label = { Text("Группы") }
                    )
                }
            }

            Scaffold(topBar = { TopBarContent(activity = activity) }) {
                ScreenContainer(paddingValues = it, stack = stack)
            }
        }
    }
}


@Composable
private fun DrawerContent(
    component: MainComponent,
    coroutineScope: CoroutineScope,
    drawerState: DrawerState
) {
    val userInfo by component.userInfo.collectAsState()
    Row(
        Modifier
            .fillMaxWidth()
            .height(84.dp)
            .clickable {
                component.onProfileClick()
                coroutineScope.launch {
                    drawerState.close()
                }
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

    val yourCourses by component.yourCourses.collectAsState()

    ResourceContent(resource = yourCourses) { courses ->
        if (courses.isNotEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .padding(horizontal = 28.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = "Курсы",
                    style = MaterialTheme.typography.titleSmall
                )
            }

            Column(Modifier.padding(8.dp)) {
                courses.forEach { course ->
                    NavigationDrawerItem(
                        label = {
                            Text(
                                course.name,
                                style = MaterialTheme.typography.labelLarge
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
                           coroutineScope.launch {
                               drawerState.close()
                           }
                            component.onCourseClick(course.id)
                        })
                }
            }
            Divider(Modifier.padding(vertical = 4.dp))
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun TopBarContent(activity: AppCompatActivity) {
    val appBarInteractor = LocalAppBarInteractor.current
    val state by appBarInteractor.stateFlow.collectAsState()

    if (state.visible)
        TopAppBar(
            title = { Text(state.title.get(LocalContext.current)) },
            navigationIcon = {
                val icon by remember { appBarInteractor.navigationIcon }

                when (icon) {
                    NavigationIcon.TOGGLE -> IconButton(
                        onClick = { TODO("Open navigation drawer") }) {
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
                }
            },
            actions = {
                state.actions.forEach { actionMenuItem ->
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

                if (state.dropdown.isNotEmpty()) {
                    var menuExpanded by remember { mutableStateOf(false) }
                    IconButton(onClick = { menuExpanded = !menuExpanded }) {
                        Icon(Icons.Filled.MoreVert, "menu")
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        offset = DpOffset(x = (-84).dp, y = 0.dp),
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        state.dropdown.forEach { item ->
                            DropdownMenuItem(
                                text = {
                                    Text(item.title.get(LocalContext.current))
                                },
                                onClick = {
                                    menuExpanded = false
                                    item.onClick()
                                    state.onDropdownMenuItemClick(item)
                                },
                            )
                        }
                    }
                }
                state.actionsUI?.invoke(this)
            }
        )
}

@Composable
private fun ScreenContainer(
    paddingValues: PaddingValues,
    stack: ChildStack<MainComponent.RootConfig, MainComponent.RootChild>
) {
    Children(modifier = Modifier.padding(paddingValues), stack = stack) {
        when (val child = it.instance) {
            is MainComponent.RootChild.YourTimetables -> YourTimetablesRootScreen(
                component = child.component
            )

            is MainComponent.RootChild.YourStudyGroups -> YourStudyGroupsRootScreen(
                component = child.component
            )

            is MainComponent.RootChild.Works -> TODO()
            is MainComponent.RootChild.StudyGroup -> TODO()
            is MainComponent.RootChild.Course -> TODO()
        }
    }
}