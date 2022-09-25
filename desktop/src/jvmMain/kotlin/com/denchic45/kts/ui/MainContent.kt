package com.denchic45.kts.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.denchic45.kts.ui.root.RootComponent
import com.denchic45.kts.ui.timetable.TimetableScreen


@OptIn(ExperimentalUnitApi::class)
@Preview
@Composable
fun MainContent(rootComponent: RootComponent) {
    Surface(
        tonalElevation = 1.dp
    ) {
        var selectedItem by remember { mutableStateOf(0) }

        Row {
            NavigationRail {
                Spacer(Modifier.weight(1f))
                NavigationRailItem(
                    icon = { Icon(painterResource("drawable/ic_timetable.xml"), null) },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 }
                )
                NavigationRailItem(
                    icon = { Icon(painterResource("drawable/ic_group.xml"), null) },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 }
                )
                Spacer(Modifier.weight(1f))
            }

            Column {

                val appBarMediator = AppBarMediator()
                SmallTopAppBar(
                    title = {
                        Row {
                            Text(appBarMediator.title,
                                fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Medium.ttf")),
                                fontSize = TextUnit(32F, TextUnitType.Sp))
                            appBarMediator.content(this)
                        }
                    },
                    modifier = Modifier.padding(top = 24.dp, bottom = 8.dp, end = 24.dp),
                    actions = {
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Notifications,
                                tint = Color.DarkGray,
                                contentDescription = "previous week arrow icon"
                            )
                        }
                        Spacer(Modifier.width(24.dp))
                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AccountCircle,
                                tint = Color.DarkGray,
                                contentDescription = "previous week arrow icon"
                            )
                        }
                    }
                )

                val state by rootComponent.childStack.subscribeAsState()
                when (val child = state.active.instance) {
                    is RootComponent.Child.Timetable -> {
                        TimetableScreen(appBarMediator, child.timetableComponent)
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
        Text(title,
            Modifier,
            fontFamily = FontFamily(Font(resource = "fonts/Gilroy-Medium.ttf")),
            fontSize = TextUnit(32F, TextUnitType.Sp))

        Box(Modifier.weight(1f)) { content() }

        IconButton(
            onClick = {},
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Notifications,
                tint = Color.DarkGray,
                contentDescription = "previous week arrow icon"
            )
        }
        Spacer(Modifier.width(24.dp))
        IconButton(
            onClick = {},
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                tint = Color.DarkGray,
                contentDescription = "previous week arrow icon"
            )
        }
    }
}

class AppBarMediator {
    var title by mutableStateOf("")
    var content by mutableStateOf<@Composable RowScope.() -> Unit>({})
}