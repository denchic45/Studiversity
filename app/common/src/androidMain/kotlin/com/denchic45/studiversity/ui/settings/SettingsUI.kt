package com.denchic45.studiversity.ui.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.denchic45.studiversity.ui.admindashboard.CustomListItem
import com.denchic45.studiversity.ui.appbar.hideAppBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(component: SettingsComponent) {
    hideAppBar()
    Surface(Modifier.fillMaxSize()) {
        Column {
            val current = LocalOnBackPressedDispatcherOwner.current!!
            TopAppBar(
                title = { Text(text = "Настройки") },
                navigationIcon = {
                    IconButton(onClick = { current.onBackPressedDispatcher.onBackPressed() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "back")
                    }
                }
            )
            CustomListItem(
                title = "Аккаунт",
                painter = rememberVectorPainter(Icons.Outlined.AccountBox),
                contentDescription = "Account",
                onClick = component::onAccountClick
            )
            CustomListItem(
                title = "Уведомления",
                painter = rememberVectorPainter(Icons.Outlined.AccountBox),
                contentDescription = "Notifications",
                onClick = component::onNotificationsClick
            )
            CustomListItem(
                title = "Тема",
                subtitle = "По умолчанию (брать из настроек)",
                painter = rememberVectorPainter(Icons.Outlined.DarkMode),
                contentDescription = "Theme picker",
                onClick = component::onThemePickerClick
            )
            Divider()
            CustomListItem(
                title = "Выйти",
                painter = rememberVectorPainter(Icons.Outlined.Logout),
                contentDescription = "Logout",
                onClick = component::onSignOutClick
            )
        }
    }
}