package com.denchic45.kts.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.ui.group.GroupComponent

sealed interface GroupConfig : Parcelable {
    class Group(val groupId: String) : GroupConfig
}

sealed interface GroupChild {
    class Group(val groupComponent: GroupComponent) : GroupChild
}

sealed interface GroupOverlayConfig : Parcelable

sealed interface GroupOverlayChild