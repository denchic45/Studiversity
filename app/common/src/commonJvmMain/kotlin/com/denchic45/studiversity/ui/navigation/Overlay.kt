package com.denchic45.studiversity.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.schedule.ScheduleComponent
import com.denchic45.studiversity.ui.settings.SettingsComponent

@Parcelize
sealed interface OverlayConfig : Parcelable {
    data class Confirm(
        val title: String,
        val text: String? = null,
        val icon: String? = null,
        val onConfirm: () -> Unit
    ) : OverlayConfig

    object YourProfile : OverlayConfig

    object Schedule : OverlayConfig

    object Settings : OverlayConfig

}

sealed interface OverlayChild {
    class Confirm(val config: OverlayConfig.Confirm) : OverlayChild

    class Schedule(val component: ScheduleComponent) : OverlayChild

    class YourProfile(val component: ProfileComponent) : OverlayChild

    class Settings(val component: SettingsComponent) : OverlayChild
}


