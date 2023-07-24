package com.denchic45.studiversity.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDialog(onCloseRequest: () -> Unit, component: SettingsComponent) {
    Dialog(
        onCloseRequest = onCloseRequest,
        title = "Настройки",
        resizable = false
    ) {
        Row {
            Column(Modifier.width(192.dp)) {
                ListItem(
                    headlineText = { Text("Аккаунт") },
                    leadingContent = { Icon(Icons.Outlined.AccountCircle, "account settings") }
                )
                ListItem(
                    headlineText = { Text("Уведомления") },
                    leadingContent = { Icon(Icons.Outlined.Notifications, "alarm settings") }
                )
            }
            Box(Modifier.aspectRatio(1f)) {
                val childSlot by component.childSlot.subscribeAsState()
                when (childSlot.child?.instance) {
                    is SettingsComponent.OverlayChild.Account -> TODO()
                    is SettingsComponent.OverlayChild.Notifications -> TODO()
                    SettingsComponent.OverlayChild.ThemePicker -> TODO()
                    null -> {

                    }
                }
            }
        }
    }
}