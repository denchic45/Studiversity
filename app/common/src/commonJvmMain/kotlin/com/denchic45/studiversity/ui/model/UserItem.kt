package com.denchic45.studiversity.ui.model

import com.denchic45.studiversity.domain.model.GroupMember
import com.denchic45.studiversity.domain.model.GroupMembers
import com.denchic45.stuiversity.api.timetable.model.PeriodMember
import com.denchic45.stuiversity.api.user.model.UserResponse
import java.util.UUID

data class UserItem(
    override val id: UUID,
    val firstName: String,
    val surname: String,
    val avatarUrl: String,
    val subtitle: String? = null,
) : UiModel {
    val title: String = "$firstName $surname"
}

fun GroupMember.toUserItem(groupMembers: GroupMembers): UserItem {
    return UserItem(
        id = id,
        firstName = firstName,
        surname = surname,
        avatarUrl = photoUrl,
        subtitle = if (groupMembers.isHeadman(this)) "Староста" else null
    )
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