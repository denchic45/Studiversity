package com.denchic45.studiversity.ui.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.data.service.AuthService
import com.denchic45.studiversity.ui.account.AccountComponent
import com.denchic45.studiversity.ui.notifications.NotificationsComponent
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsComponent(
    private val authService: AuthService,
    private val accountComponent: (ComponentContext) -> AccountComponent,
    private val notificationsComponent: (ComponentContext) -> NotificationsComponent,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val slotNavigation = SlotNavigation<OverlayConfig>()
    val childSlot = childSlot(
        handleBackButton = true,
        source = slotNavigation,
        childFactory = { config, context ->
            when (config) {
                OverlayConfig.Account -> {
                    OverlayChild.Account(accountComponent(context))
                }

                OverlayConfig.Notifications -> {
                    OverlayChild.Notifications(notificationsComponent(context))
                }

                OverlayConfig.ThemePicker -> {
                    OverlayChild.ThemePicker
                }
            }
        }
    )

    fun onAccountClick() {
        slotNavigation.activate(OverlayConfig.Account)
    }

    fun onNotificationsClick() {
        slotNavigation.activate(OverlayConfig.Notifications)
    }

    fun onThemePickerClick() {
        slotNavigation.activate(OverlayConfig.ThemePicker)
    }

    fun onSignOutClick() {
        authService.signOut()
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        object Account : OverlayConfig()

        object Notifications : OverlayConfig()

        object ThemePicker : OverlayConfig()
    }

    sealed class OverlayChild {
        class Account(val component: AccountComponent) : OverlayChild()

        class Notifications(val component: NotificationsComponent) : OverlayChild()

        // TODO: May be the component is not needed
        object ThemePicker : OverlayChild()
    }
}