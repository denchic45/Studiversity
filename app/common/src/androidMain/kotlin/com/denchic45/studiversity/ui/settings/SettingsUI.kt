package com.denchic45.studiversity.ui.settings

import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.*
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
                    IconButton(onClick = current.onBackPressedDispatcher::onBackPressed) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "back")
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
                title = "Безопасность и вход",
                painter = rememberVectorPainter(Icons.Outlined.VerifiedUser),
                contentDescription = "Security",
                onClick = component::onSecurityClick
            )
            CustomListItem(
                title = "Уведомления",
                painter = rememberVectorPainter(Icons.Outlined.Notifications),
                contentDescription = "Notifications",
                onClick = component::onNotificationsClick
            )
            CustomListItem(
                title = "Внешний вид",
                painter = rememberVectorPainter(Icons.Outlined.Colorize),
                contentDescription = "Appearance",
                onClick = component::onThemePickerClick
            )
            HorizontalDivider()
            CustomListItem(
                title = "Выйти",
                painter = rememberVectorPainter(Icons.AutoMirrored.Outlined.Logout),
                contentDescription = "Logout",
                onClick = component::onSignOutClick
            )
        }
    }
}