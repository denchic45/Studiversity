package com.denchic45.kts.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable

sealed interface GroupMembersConfig : Parcelable {
    object Unselected : GroupMembersConfig

}

sealed interface GroupMembersChild {
    object Unselected : GroupMembersChild

}