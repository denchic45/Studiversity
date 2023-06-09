package com.denchic45.kts.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.domain.onLoading
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.confirm.ConfirmDialog
import com.denchic45.kts.ui.course.CourseScreen
import com.denchic45.kts.ui.navigation.OverlayChild
import com.denchic45.kts.ui.root.RootScreen
import com.denchic45.kts.ui.studygroup.StudyGroupScreen
import com.denchic45.kts.ui.theme.toDrawablePath
import com.seiko.imageloader.rememberAsyncImagePainter


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(component: MainComponent) {
    Surface(tonalElevation = 1.dp) {
        val childStack by component.stack.subscribeAsState()
        val activeChild = childStack.active.instance
        val availableScreens by component.availableScreens.collectAsState()
        val userInfo by component.userInfo.collectAsState()

        Row {
            NavigationRail(
                header = {
                    Spacer(Modifier.height(56.dp))

                    IconButton(onClick = {}) {
                        userInfo.onSuccess {
                            Image(
                                painter = rememberAsyncImagePainter(it.avatarUrl),
                                contentDescription = "Avatar",
                                modifier = Modifier.size(40.dp)
                                    .clip(CircleShape)
                                    .background(Color.LightGray),
                            )
                        }.onLoading {
                            CircularProgressIndicator(Modifier.size(24.dp))
                        }

                    }
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Outlined.Notifications,
                            tint = Color.DarkGray,
                            contentDescription = "Notifications"
                        )
                    }
                }) {
                Spacer(Modifier.height(56.dp))
                NavigationRailItem(icon = {
                    Icon(painterResource("ic_timetable".toDrawablePath()), null)
                },
                    selected = activeChild is MainComponent.Child.YourTimetables,
                    onClick = { component.onTimetableClick() })
                NavigationRailItem(
                    icon = { Icon(painterResource("ic_study_group".toDrawablePath()), null) },
                    selected = activeChild is MainComponent.Child.YourStudyGroups,
                    onClick = { component.onStudyGroupsClick() })

                if (availableScreens.yourWorks) {
                    NavigationRailItem(
                        icon = {
                            Icon(
                                painter = painterResource("ic_works".toDrawablePath()),
                                contentDescription = "works"
                            )
                        },
                        selected = activeChild is MainComponent.Child.AdminDashboard,
                        onClick = {
                            component.onAdminDashboardClick()
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
                    onClick = {
                        component.onAdminDashboardClick()
                    }
                )
                if (availableScreens.adminDashboard) {
                    NavigationRailItem(
                        icon = {
                            Icon(
                                imageVector = Icons.Outlined.Widgets,
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
//                Spacer(Modifier.weight(1f))
            }

            Column {
                val appBarMediator = LocalAppBarMediator.current
                TopAppBar(
                    title = {
                        Row {
                            Text(
                                text = appBarMediator.title,
                                fontSize = TextUnit(32F, TextUnitType.Sp),
                                style = MaterialTheme.typography.headlineLarge
                            )
                            appBarMediator.content?.let {
                                it(this)
                            }
                        }
                    },
                    modifier = Modifier.height(96.dp)
                        .padding(top = 36.dp, bottom = 8.dp, end = 24.dp),
                    actions = {
                        Spacer(Modifier.width(4.dp))
//                        IconButton(onClick = {}) {
//                            Icon(
//                                imageVector = Icons.Outlined.Notifications,
//                                tint = Color.DarkGray,
//                                contentDescription = "Notifications"
//                            )
//                        }
//                        IconButton(onClick = {}) {
//                            Icon(
//                                imageVector = Icons.Outlined.AccountCircle,
//                                tint = Color.DarkGray,
//                                contentDescription = "Avatar"
//                            )
//                        }
                    })
                Surface(
                    tonalElevation = (-1).dp,
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                ) {
                    when (val child = childStack.active.instance) {
                        is MainComponent.Child.YourTimetables -> RootScreen(child.component)
                        is MainComponent.Child.YourStudyGroups -> {
                            RootScreen(child.component)
                        }

                        is MainComponent.Child.Works -> TODO()
                        is MainComponent.Child.StudyGroup -> StudyGroupScreen(child.component)
                        is MainComponent.Child.Course -> CourseScreen(child.component)
                        is MainComponent.Child.AdminDashboard -> TODO()
                        is MainComponent.Child.YourCourse -> CourseScreen(child.component)
                    }
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
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalUnitApi::class)
@Composable
private fun MainAppBar(
    title: String,
    content: @Composable () -> Unit,
) {
    Row(
        Modifier.padding(top = 40.dp, bottom = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            title,
            Modifier,
            fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Medium.ttf")),
            fontSize = TextUnit(32F, TextUnitType.Sp)
        )

        Box(Modifier.weight(1f)) { content() }

        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                tint = Color.DarkGray,
                contentDescription = "previous week arrow icon"
            )
        }
        Spacer(Modifier.width(24.dp))
        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                tint = Color.DarkGray,
                contentDescription = "previous week arrow icon"
            )
        }
    }
}

