package com.denchic45.kts.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.platform.Font
import androidx.compose.ui.unit.ExperimentalUnitApi
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.kts.ui.MainComponent.Child
import com.denchic45.kts.ui.group.GroupScreen
import com.denchic45.kts.ui.theme.toDrawablePath
import com.denchic45.kts.ui.timetable.TimetableScreen


@OptIn(ExperimentalUnitApi::class)
@Preview
@Composable
fun MainContent(mainComponent: MainComponent) {
    Surface(tonalElevation = 1.dp) {
        val childStack by mainComponent.stack.subscribeAsState()
        val activeComponent = childStack.active.instance

        Row {
            NavigationRail {
                Spacer(Modifier.weight(1f))
                NavigationRailItem(icon = {
                    Icon(painterResource("ic_timetable".toDrawablePath()), null)
                },
                    selected = activeComponent is Child.Timetable,
                    onClick = { mainComponent.onTimetableClick() })
                NavigationRailItem(
                    icon = { Icon(painterResource("ic_group".toDrawablePath()), null) },
                    selected = activeComponent is Child.Group,
                    onClick = { mainComponent.onGroupClick() })
                Spacer(Modifier.weight(1f))
            }

            Column {
                val appBarMediator = AppBarMediator()
                SmallTopAppBar(title = {
                    Row {
                        Text(text = appBarMediator.title,
                            fontSize = TextUnit(32F, TextUnitType.Sp),
                            style = MaterialTheme.typography.headlineLarge)
                        appBarMediator.content(this)
                    }
                }, modifier = Modifier.padding(top = 24.dp, bottom = 8.dp, end = 24.dp), actions = {
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Outlined.Notifications,
                            tint = Color.DarkGray,
                            contentDescription = "previous week arrow icon")
                    }
                    Spacer(Modifier.width(12.dp))
                    IconButton(onClick = {}) {
                        Icon(imageVector = Icons.Outlined.AccountCircle,
                            tint = Color.DarkGray,
                            contentDescription = "previous week arrow icon")
                    }
                })

                when (val child = childStack.active.instance) {
                    is Child.Timetable -> {
                        TimetableScreen(appBarMediator, child.timetableComponent)
                    }
                    is Child.Group -> {
                        GroupScreen(appBarMediator, child.groupRootComponent)
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
    Row(Modifier.padding(top = 40.dp, bottom = 24.dp, end = 24.dp),
        verticalAlignment = Alignment.CenterVertically) {
        Text(title,
            Modifier,
            fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Medium.ttf")),
            fontSize = TextUnit(32F, TextUnitType.Sp))

        Box(Modifier.weight(1f)) { content() }

        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Outlined.Notifications,
                tint = Color.DarkGray,
                contentDescription = "previous week arrow icon")
        }
        Spacer(Modifier.width(24.dp))
        IconButton(onClick = {}, modifier = Modifier.size(32.dp)) {
            Icon(imageVector = Icons.Outlined.AccountCircle,
                tint = Color.DarkGray,
                contentDescription = "previous week arrow icon")
        }
    }
}

class AppBarMediator {
    var title by mutableStateOf("")
    var content by mutableStateOf<@Composable RowScope.() -> Unit>({})
}