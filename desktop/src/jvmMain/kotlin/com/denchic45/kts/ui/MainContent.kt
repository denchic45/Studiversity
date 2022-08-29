package com.denchic45.kts.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.denchic45.kts.ui.root.RootComponent
import com.denchic45.kts.ui.timetable.TimetableScreen

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
                    icon = { Icon(Icons.Filled.Email, "") },
                    selected = selectedItem == 0,
                    onClick = { selectedItem = 0 }
                )
                NavigationRailItem(
                    icon = { Icon(Icons.Filled.Call, "") },
                    selected = selectedItem == 1,
                    onClick = { selectedItem = 1 }
                )
                Spacer(Modifier.weight(1f))
            }

            Column {
                TimetableScreen()
            }
        }
    }
}