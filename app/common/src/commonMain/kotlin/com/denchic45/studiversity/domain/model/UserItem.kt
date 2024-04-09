package com.denchic45.studiversity.domain.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.timetable.model.PeriodMember
import com.denchic45.studiversity.api.user.model.UserResponse
import java.util.*

@Parcelize
data class UserItem(
    override val id: UUID,
    val firstName: String,
    val surname: String,
    val avatarUrl: String,
    val subtitle: String? = null,
) : UiModel, Parcelable {
    val title: String = "$firstName $surname"
}

fun UserResponse.toUserItem(subtitle: String? = null): UserItem {
    return UserItem(
        id = id,
        firstName = firstName,
        surname = surname,
        avatarUrl = avatarUrl,
        subtitle = subtitle
    )
}

fun PeriodMember.toUserItem(): UserItem {
    return UserItem(
        id = id,
        firstName = firstName,
        surname = surname,
        avatarUrl = avatarUrl,
        subtitle = null
    )
}

fun UserItem.toPeriodMember() = PeriodMember(
    id = id,
    firstName = firstName,
    surname = surname,
    avatarUrl = avatarUrl
)