package com.denchic45.studiversity.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.ui.confirm.ConfirmDialog
import com.denchic45.studiversity.ui.course.CourseScreen
import com.denchic45.studiversity.ui.coursework.CourseWorkScreen
import com.denchic45.studiversity.ui.courseworkeditor.CourseWorkEditorScreen
import com.denchic45.studiversity.ui.navigation.OverlayChild
import com.denchic45.studiversity.ui.root.RootScreen
import com.denchic45.studiversity.ui.studygroup.StudyGroupScreen
import com.denchic45.studiversity.ui.theme.spacing
import com.denchic45.studiversity.ui.theme.toDrawablePath
import com.denchic45.studiversity.ui.yourworks.YourWorksScreen
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.seiko.imageloader.rememberAsyncImagePainter


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
                onNotificationsClick = {}
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
                overlay.overlay?.let {
                    when (val instance = it.instance) {
                        is OverlayChild.Confirm -> with(instance.config) {
                            ConfirmDialog(
                                title = title,
                                text = text,
                                onConfirm = {},
                                onDismiss = component::onOverlayDismiss
                            )
                        }

                        is OverlayChild.YourProfile -> TODO("Open profile dialog")
                        is OverlayChild.Settings -> TODO()
                        is OverlayChild.Schedule -> TODO()
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppBar(
    userInfo: Resource<UserResponse>,
    showBackButton: Boolean,
    onBackClick: () -> Unit,
    onHelpClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = MaterialTheme.spacing.medium),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (showBackButton) {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBack,
                    contentDescription = "back"
                )
            }
        }
        Spacer(Modifier.weight(1f))
        IconButton(onClick = onHelpClick) {
            Icon(
                painter = painterResource("ic_help_circle".toDrawablePath()),
                contentDescription = "help"
            )
        }
        var showNotificationsPopup by remember { mutableStateOf(false) }
        IconButton(onClick = { showNotificationsPopup = true }) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                contentDescription = "notifications"
            )
        }
        var showProfilePopup by remember { mutableStateOf(false) }
        IconButton(onClick = { showProfilePopup = true }) {
            ResourceContent(userInfo, onLoading = { CircularProgressIndicator(Modifier.size(24.dp)) }) {
                Image(
                    painter = rememberAsyncImagePainter(it.avatarUrl),
                    contentDescription = "avatar",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(40.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomAppBar(
    title: @Composable RowScope.() -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable () -> Unit = {},
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
//    TopAppBar(
//        title = title,
//        modifier = modifier.height(80.dp).padding(end = 24.dp).background(Color.DarkGray),
//        actions = actions
//    )
}

@Composable
fun AppBarTitle(text: String) {
    Box(Modifier.fillMaxHeight(), contentAlignment = Alignment.CenterStart) {
        Text(
            text = text,
            color = MaterialTheme.colorScheme.onSurface,
            style = MaterialTheme.typography.headlineLarge
        )
    }
}