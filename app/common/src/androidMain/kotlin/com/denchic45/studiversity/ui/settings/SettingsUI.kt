package com.denchic45.studiversity.ui.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.denchic45.studiversity.ui.admindashboard.CustomListItem
import com.denchic45.studiversity.ui.appbar2.hideAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(component: SettingsComponent) {
//    updateAppBarState(AppBarContent(uiTextOf("Настройки")))
    hideAppBar()
    Surface(
        Modifier.fillMaxSize()
    ) {
        Column {
            val current = LocalOnBackPressedDispatcherOwner.current!!
            TopAppBar(
                title = { Text(text = "Настройки") },
                navigationIcon = {
                    IconButton(onClick = {
                        current.onBackPressedDispatcher.onBackPressed()
                    }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                }
            )
            CustomListItem(
                title = "Выйти",
                painter = rememberVectorPainter(Icons.Outlined.Logout),
                contentDescription = "Logout",
                onClick = component::onSignOutClick
            )
        }
    }
}