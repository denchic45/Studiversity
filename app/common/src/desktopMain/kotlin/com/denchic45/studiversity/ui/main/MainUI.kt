package com.denchic45.studiversity.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.extensions.compose.jetbrains.lifecycle.LifecycleController
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.arkivanov.essenty.backhandler.BackDispatcher
import com.arkivanov.essenty.lifecycle.LifecycleRegistry
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.AppBarMediator
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.confirm.ConfirmDialog
import com.denchic45.studiversity.ui.course.CourseScreen
import com.denchic45.studiversity.ui.coursework.CourseWorkScreen
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorScreen
import com.denchic45.studiversity.ui.navigation.OverlayChild
import com.denchic45.studiversity.ui.root.RootScreen
import com.denchic45.studiversity.ui.settings.SettingsDialog
import com.denchic45.studiversity.ui.studygroup.StudyGroupScreen
import com.denchic45.studiversity.ui.theme.DesktopApp
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.denchic45.studiversity.ui.yourworks.YourWorksScreen
import com.denchic45.stuiversity.api.user.model.UserResponse
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import java.awt.Toolkit

@OptIn(ExperimentalDecomposeApi::class)
@Composable
fun ApplicationScope.MainWindow(
    component: MainComponent,
    lifecycle: LifecycleRegistry,
    backDispatcher: BackDispatcher
) {
    val size = Toolkit.getDefaultToolkit().screenSize.run {
        DpSize((width - 124).dp, (height - 124).dp)
    }
    val state = rememberWindowState(
        size = size,
        position = WindowPosition(Alignment.Center)
    )
    LifecycleController(lifecycle, state)

    DesktopApp(
        title = "Studiversity",
        onCloseRequest = ::exitApplication,
        state = state,
        backDispatcher = backDispatcher
    ) {
        CompositionLocalProvider(LocalAppBarMediator provides AppBarMediator()) {
            MainScreen(component)
        }
    }
}

@Composable
fun MainScreen(component: MainComponent) {
    Surface(
        tonalElevation = 1.dp,
        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
    ) {
        val childStack by component.stack.subscribeAsState()
        val activeChild = childStack.active.instance
        val availableScreens by component.availableScreens.collectAsState()
        val userInfo by component.userInfo.collectAsState()

        Column {
            MainAppBar(
                userInfo = userInfo,
                showBackButton = false,
                onBackClick = component::onBackClick,
                onHelpClick = {},
                onNotificationsClick = {},
                onSettingsClick = component::onSettingsClick
            )

            Row {
                NavigationRail(
                    header = {
                        Spacer(Modifier.height(MaterialTheme.spacing.small))
                        IconButton(onClick = {}) {
                            Icon(
                                imageVector = Icons.Outlined.Menu,
                                contentDescription = "expandable navigation menu"
                            )
                        }
                    }) {
                    Spacer(Modifier.height(60.dp))
                    NavigationRailItem(
                        icon = { Icon(painterResource("ic_timetable".toDrawablePath()), null) },
                        selected = activeChild is MainComponent.Child.YourTimetables,
                        onClick = component::onTimetableClick
                    )
                    NavigationRailItem(
                        icon = { Icon(painterResource("ic_study_group".toDrawablePath()), null) },
                        selected = activeChild is MainComponent.Child.YourStudyGroups,
                        onClick = component::onStudyGroupsClick
                    )

                    if (availableScreens.yourWorks) {
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    painter = painterResource("ic_works".toDrawablePath()),
                                    contentDescription = "works"
                                )
                            },
                            selected = activeChild is MainComponent.Child.YourWorks,
                            onClick = {
                                component.onWorksClick()
                            }
                        )
                    }

                    Divider(Modifier.width(48.dp).align(Alignment.CenterHorizontally))

                    NavigationRailItem(
                        icon = {
                            Icon(
                                painter = painterResource("ic_time".toDrawablePath()),
                                contentDescription = "schedule"
                            )
                        },
                        selected = activeChild is MainComponent.Child.AdminDashboard,
                        onClick = component::onAdminDashboardClick
                    )
                    if (availableScreens.adminDashboard) {
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Dashboard,
                                    contentDescription = "admin dashboard"
                                )
                            },
                            selected = activeChild is MainComponent.Child.AdminDashboard,
                            onClick = component::onAdminDashboardClick
                        )
                    }

                    NavigationRailItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Settings,
                                contentDescription = "settings"
                            )
                        },
                        selected = activeChild is MainComponent.Child.AdminDashboard,
                        onClick = component::onSettingsClick
                    )
                }
                when (val child = childStack.active.instance) {
                    is MainComponent.Child.YourTimetables -> RootScreen(child.component)
                    is MainComponent.Child.YourStudyGroups -> RootScreen(child.component)
                    is MainComponent.Child.YourWorks -> YourWorksScreen(child.component)
                    is MainComponent.Child.StudyGroup -> StudyGroupScreen(child.component)
                    is MainComponent.Child.Course -> CourseScreen(child.component)
                    is MainComponent.Child.AdminDashboard -> RootScreen(child.component)
                    is MainComponent.Child.YourCourse -> CourseScreen(child.component)
                    is MainComponent.Child.CourseWork -> CourseWorkScreen(child.component)
                    is MainComponent.Child.CourseWorkEditor -> CourseWorkEditorScreen(child.component)
                }

                val overlay by component.childOverlay.subscribeAsState()
                overlay.child?.let {
                    when (val instance = it.instance) {
                        is OverlayChild.Confirm -> with(instance.config) {
                            ConfirmDialog(
                                title = title,
                                text = text,
                                onConfirm = {},
                                onDismiss = component::onDialogClose
                            )
                        }

                        is OverlayChild.YourProfile -> TODO("Open profile dialog")
                        is OverlayChild.Settings -> SettingsDialog(
                            onCloseRequest = component::onDialogClose,
                            component = instance.component
                        )

                        is OverlayChild.Schedule -> TODO()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppBar(
    userInfo: Resource<UserResponse>,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = MaterialTheme.spacing.normal),
        verticalAlignment = Alignment.CenterVertically
    ) {
//        if (showBackButton) {
        IconButton(onClick = onBackClick, enabled = showBackButton) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "back"
            )
        }
//        }
        Spacer(Modifier.weight(1f))
        var showNotificationsPopup by remember { mutableStateOf(false) }
        IconButton(onClick = { showNotificationsPopup = true }) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "notifications"
            )
        }
        var showProfilePopup by remember { mutableStateOf(false) }
        IconButton(onClick = { showProfilePopup = true }) {
            ResourceContent(
                userInfo,
                onLoading = { CircularProgressIndicator(Modifier.size(24.dp)) }) { user ->
                Column {
                    KamelImage(
                        resource = asyncPainterResource(user.avatarUrl),
                        contentDescription = "avatar",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray),
                    )

                    DropdownMenu(
                        expanded = showProfilePopup,
                        onDismissRequest = { showProfilePopup = false },
                        modifier = Modifier.width(336.dp)
                    ) {
                        val interactionSource = remember(::MutableInteractionSource)
                        val isHovered by interactionSource.collectIsHoveredAsState()
                        val profilePadding = MaterialTheme.spacing.small
                        Card(
                            colors = if (isHovered)
                                CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                            else CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.1f)
                            ),
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier
                                .padding(
                                    start = profilePadding,
                                    end = profilePadding,
                                    bottom = profilePadding
                                )
                                .hoverable(interactionSource)
                        ) {
                            Row(
                                Modifier.padding(MaterialTheme.spacing.normal),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                KamelImage(
                                    resource = asyncPainterResource(user.avatarUrl),
                                    null,
                                    Modifier.size(40.dp).clip(CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                                Column(Modifier.padding(start = 16.dp)) {
                                    Text(user.fullName, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        "Посмотреть профиль",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                                Spacer(Modifier.weight(1f))
                                Icon(
                                    imageVector = Icons.Rounded.ChevronRight,
                                    contentDescription = "show profile",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        ListItem(
                            headlineText = { Text("Настройки") },
                            leadingContent = { Icon(Icons.Outlined.Settings, null) },
                            modifier = Modifier.clickable(onClick = {
                                showProfilePopup = false
                                onSettingsClick()
                            })
                        )
                        Column {
                            var expandedThemePicker by remember { mutableStateOf(false) }
                            ListItem(
                                headlineText = {
                                    Row {
                                        Text("Тема: ")
                                        Text(
                                            "По умолчанию",
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                },
                                leadingContent = { Icon(Icons.Outlined.DarkMode, null) },
                                modifier = Modifier.clickable {
                                    expandedThemePicker = !expandedThemePicker
                                }
                            )
                            DropdownMenu(
                                expanded = expandedThemePicker,
                                onDismissRequest = { expandedThemePicker = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Светлая") },
                                    onClick = {}
                                )
                                DropdownMenuItem(
                                    text = { Text("Темная") },
                                    onClick = {}
                                )
                                DropdownMenuItem(
                                    text = { Text("По умолчанию") },
                                    onClick = {}
                                )
                            }
                        }
                        ListItem(
                            headlineText = { Text("Помощь") },
                            leadingContent = { Icon(Icons.Rounded.HelpOutline, null) },
                            modifier = Modifier.clickable {
                                showProfilePopup = false
                                onHelpClick()
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CustomAppBar(
    title: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier.height(80.dp).padding(end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        title()
        Spacer(Modifier.weight(1f))
        actions()
    }
}

@Composable
fun AppBarTitle(text: String) {
    Box(
        modifier = Modifier.fillMaxHeight(),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}