package com.denchic45.studiversity.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.schedule.ScheduleComponent
import com.denchic45.studiversity.ui.settings.SettingsComponent

@Parcelize
sealed interface SlotConfig : Parcelable {
    data class Confirm(
        val title: String,
        val text: String? = null,
        val icon: String? = null,
        val onConfirm: () -> Unit
    ) : SlotConfig

    object YourProfile : SlotConfig

    object Schedule : SlotConfig

    object Settings : SlotConfig

}

sealed interface SlotChild {
    class Confirm(val config: SlotConfig.Confirm) : SlotChild

    class Schedule(val component: ScheduleComponent) : SlotChild

    class YourProfile(val component: ProfileComponent) : SlotChild

    class Settings(val component: SettingsComponent) : SlotChild
}


