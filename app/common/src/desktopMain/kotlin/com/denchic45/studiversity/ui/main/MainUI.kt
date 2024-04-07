package com.denchic45.studiversity.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.ui.AppBarMediator
import com.denchic45.studiversity.ui.LocalAppBarMediator
import com.denchic45.studiversity.ui.ResourceContent
import com.denchic45.studiversity.ui.root.RootScreen
import com.denchic45.studiversity.ui.schedule.ScheduleDialog
import com.denchic45.studiversity.ui.settings.SettingsDialog
import com.denchic45.studiversity.ui.theme.DesktopApp
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
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
        state = state,
        onCloseRequest = ::exitApplication,
        title = "Studiversity",
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
        val yourStudyGroups by component.yourStudyGroups.collectAsState()

        Column {
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
                        Spacer(Modifier.height(MaterialTheme.spacing.normal))

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
                                                    containerColor = MaterialTheme.colorScheme.outline.copy(
                                                        alpha = 0.2f
                                                    )
                                                )
                                            else CardDefaults.cardColors(
                                                containerColor = MaterialTheme.colorScheme.outline.copy(
                                                    alpha = 0.1f
                                                )
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
                                                    Text(
                                                        user.fullName,
                                                        style = MaterialTheme.typography.bodyLarge
                                                    )
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
                                            headlineContent = { Text("Настройки") },
                                            leadingContent = {
                                                Icon(
                                                    Icons.Outlined.Settings,
                                                    null
                                                )
                                            },
                                            modifier = Modifier.clickable(onClick = {
                                                showProfilePopup = false
                                                component.onSettingsClick()
                                            })
                                        )
                                        Column {
                                            var expandedThemePicker by remember {
                                                mutableStateOf(
                                                    false
                                                )
                                            }
                                            ListItem(
                                                headlineContent = {
                                                    Row {
                                                        Text("Тема: ")
                                                        Text(
                                                            "По умолчанию",
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                },
                                                leadingContent = {
                                                    Icon(
                                                        Icons.Outlined.DarkMode,
                                                        null
                                                    )
                                                },
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
                                            headlineContent = { Text("Расписание") },
                                            leadingContent = {
                                                Icon(
                                                    Icons.Outlined.Schedule,
                                                    null
                                                )
                                            },
                                            modifier = Modifier.clickable(onClick = {
                                                showProfilePopup = false
                                                component.onScheduleClick()
                                            })
                                        )
                                        ListItem(
                                            headlineContent = { Text("Помощь") },
                                            leadingContent = {
                                                Icon(
                                                    Icons.Rounded.HelpOutline,
                                                    null
                                                )
                                            },
                                            modifier = Modifier.clickable {
                                                showProfilePopup = false
//                                                onHelpClick()
                                            }
                                        )
                                    }
                                }
                            }
                        }

                        var showNotificationsPopup by remember { mutableStateOf(false) }
                        IconButton(onClick = { showNotificationsPopup = true }) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                contentDescription = "notifications"
                            )
                        }
                    }) {
                    Spacer(Modifier.height(60.dp))
                    NavigationRailItem(
                        icon = { Icon(painterResource("ic_timetable".toDrawablePath()), null) },
                        selected = activeChild is MainComponent.Child.YourTimetables,
                        onClick = component::onTimetableClick
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
                    if (availableScreens.yourStudyGroups) {
                        NavigationRailItem(
                            icon = {
                                Icon(
                                    painterResource("ic_study_group".toDrawablePath()),
                                    null
                                )
                            },
                            selected = activeChild is MainComponent.Child.YourStudyGroups,
                            onClick = { yourStudyGroups.onSuccess { component.onStudyGroupsClick(it.first().id) } }
                        )
                    }

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
                }
                val child = childStack.active.instance
                val hasChildren by child.component.hasChildrenFlow().collectAsState(false)
                CompositionLocalProvider(LocalChildrenExistence provides hasChildren) {
                    when (child) {
                        is MainComponent.Child.YourTimetables -> RootScreen(child.component)
                        is MainComponent.Child.YourStudyGroups -> RootScreen(child.component)
                        is MainComponent.Child.YourWorks -> RootScreen(child.component)
                        is MainComponent.Child.AdminDashboard -> RootScreen(child.component)
                    }
                }

                val childSlot by component.childSlot.subscribeAsState()
                childSlot.child?.instance?.let { instance ->
                    when (instance) {
                        is MainComponent.SlotChild.Settings -> SettingsDialog(
                            component = instance.component,
                            onCloseRequest = component::onDialogClose
                        )

                        is MainComponent.SlotChild.Schedule -> ScheduleDialog(
                            component = instance.component,
                            onDismissRequest = component::onDialogClose
                        )
                    }
                }
            }
        }
    }
}

val LocalChildrenExistence = compositionLocalOf<Boolean>() { error("No RootComponent") }

@Composable
fun CustomAppBar(
    title: @Composable RowScope.() -> Unit,
    navigationContent: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
    actions: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier.height(80.dp).padding(end = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        navigationContent?.invoke()
        Spacer(Modifier.width(MaterialTheme.spacing.normal))
        title()
        Spacer(Modifier.weight(1f))
        actions()
    }
}

@Composable
fun CustomCenteredAppBar(
    modifier: Modifier = Modifier,
    navigationContent: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    Row(
        modifier = modifier.height(80.dp).padding(horizontal = MaterialTheme.spacing.normal),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        navigationContent()
        Spacer(Modifier.weight(1f))
        content()
        Spacer(Modifier.weight(1f))
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
            style = MaterialTheme.typography.headlineSmall
        )
    }
}

@Composable
fun NavigationIconBack(onClick: () -> Unit) {
    if (LocalChildrenExistence.current)
        IconButton(onClick = onClick) {
            Icon(Icons.Filled.ArrowBack, "pop")
        }
}