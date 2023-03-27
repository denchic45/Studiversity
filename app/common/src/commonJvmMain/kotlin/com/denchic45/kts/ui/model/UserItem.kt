package com.denchic45.kts.ui.model

import com.denchic45.kts.domain.model.GroupMember
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.domain.model.User
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import com.denchic45.stuiversity.api.user.model.UserResponse
import java.util.UUID

data class UserItem(
    override val id: UUID,
    val title: String,
    val avatarUrl: String,
    val subtitle: String? = null,
) : UiModel

fun GroupMember.toUserItem(groupMembers: GroupMembers): UserItem {
    return UserItem(
        id = id,
        title = fullName,
        avatarUrl = photoUrl,
        subtitle = if (groupMembers.isHeadman(this)) "Староста" else null
    )
}

fun UserResponse.toUserItem(): UserItem {
    return UserItem(
        id = id,
        title = fullName,
        avatarUrl = avatarUrl,
        subtitle = null
    )
}