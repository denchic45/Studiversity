package com.denchic45.studiversity.ui.settings

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Colorize
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.rememberDialogState
import com.arkivanov.decompose.extensions.compose.jetbrains.subscribeAsState
import com.denchic45.studiversity.ui.theme.spacing

@Composable
fun SettingsDialog(onCloseRequest: () -> Unit, component: SettingsComponent) {
    val childSlot by component.childSlot.subscribeAsState()
    DialogWindow(onCloseRequest = onCloseRequest,
        title = "Настройки",
        resizable = false,
        state = rememberDialogState(size = DpSize(948.dp, 684.dp)),
        content = {
            Row {
                ModalDrawerSheet(
                    Modifier.requiredWidth(300.dp)
                        .padding(end = MaterialTheme.spacing.normal)
                ) {
                    val config = childSlot.child?.configuration
                    Column(Modifier.padding(MaterialTheme.spacing.normal)) {
                        NavigationDrawerItem(
                            selected = config == SettingsComponent.OverlayConfig.Account,
                            onClick = component::onAccountClick,
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.AccountCircle,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Аккаунт") }
                        )
                        NavigationDrawerItem(
                            selected = config == SettingsComponent.OverlayConfig.Security,
                            onClick = component::onSecurityClick,
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.VerifiedUser,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Безопасность и вход") }
                        )
                        NavigationDrawerItem(
                            selected = config == SettingsComponent.OverlayConfig.Notifications,
                            onClick = component::onNotificationsClick,
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Notifications,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Уведомления") }
                        )
                        NavigationDrawerItem(
                            selected = config == SettingsComponent.OverlayConfig.ThemePicker,
                            onClick = component::onThemePickerClick,
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Colorize,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Внешний вид") }
                        )

                        Spacer(Modifier.weight(1f))
                        NavigationDrawerItem(
                            selected = false,
                            onClick = component::onSignOutClick,
                            icon = {
                                Icon(
                                    imageVector = Icons.Outlined.Logout,
                                    contentDescription = null
                                )
                            },
                            label = { Text("Выйти") }
                        )
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    when (val child = childSlot.child?.instance) {
                        is SettingsComponent.OverlayChild.Personality -> {
                            PersonalityScreen(child.component)
                        }

                        is SettingsComponent.OverlayChild.Security -> {

                        }

                        is SettingsComponent.OverlayChild.Notifications -> TODO()
                        SettingsComponent.OverlayChild.ThemePicker -> TODO()
                        null -> {}
                    }
                }
            }
        })
}