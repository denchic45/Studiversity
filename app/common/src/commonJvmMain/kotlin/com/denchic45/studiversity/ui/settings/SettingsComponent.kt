package com.denchic45.studiversity.ui.settings

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.data.service.AuthService
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsComponent(
    private val authService: AuthService,
    private val personalityComponent: (ComponentContext) -> PersonalityComponent,
    private val securityComponent: (ComponentContext) -> SecurityComponent,
    private val notificationsComponent: (ComponentContext) -> NotificationsComponent,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val slotNavigation = SlotNavigation<OverlayConfig>()
    val childSlot = childSlot(
        handleBackButton = true,
        source = slotNavigation,
        initialConfiguration = { OverlayConfig.Account },
        childFactory = { config, context ->
            when (config) {
                OverlayConfig.Account -> {
                    OverlayChild.Personality(personalityComponent(context))
                }

                OverlayConfig.Security -> {
                    OverlayChild.Security(securityComponent(context))
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

    fun onAccountClick() = slotNavigation.activate(OverlayConfig.Account)

    fun onSecurityClick() = slotNavigation.activate(OverlayConfig.Security)

    fun onNotificationsClick() = slotNavigation.activate(OverlayConfig.Notifications)

    fun onThemePickerClick() = slotNavigation.activate(OverlayConfig.ThemePicker)

    fun onSignOutClick() {
        authService.signOut()
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data object Account : OverlayConfig() {
            private fun readResolve(): Any = Account
        }

        data object Security : OverlayConfig() {
            private fun readResolve(): Any = Security
        }

        data object Notifications : OverlayConfig() {
            private fun readResolve(): Any = Notifications
        }

        data object ThemePicker : OverlayConfig() {
            private fun readResolve(): Any = ThemePicker
        }
    }

    sealed class OverlayChild {
        class Personality(val component: PersonalityComponent) : OverlayChild()

        class Security(val component: SecurityComponent) : OverlayChild()

        class Notifications(val component: NotificationsComponent) : OverlayChild()

        // TODO: May be the component is not needed
        data object ThemePicker : OverlayChild()
    }
}