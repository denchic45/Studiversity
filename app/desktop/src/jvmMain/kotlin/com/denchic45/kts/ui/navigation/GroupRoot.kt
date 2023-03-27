package com.denchic45.kts.ui.navigation

import com.arkivanov.essenty.parcelable.Parcelable
import com.denchic45.kts.ui.studygroup.GroupComponent
import java.util.UUID

sealed interface GroupConfig : Parcelable {
    class Group(val groupId: UUID) : GroupConfig
}

sealed interface GroupChild {
    class Group(val groupComponent: GroupComponent) : GroupChild
}