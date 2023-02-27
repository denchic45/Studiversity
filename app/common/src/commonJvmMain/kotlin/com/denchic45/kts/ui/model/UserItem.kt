package com.denchic45.kts.ui.model

import com.denchic45.kts.domain.model.GroupMember
import com.denchic45.kts.domain.model.GroupMembers
import com.denchic45.kts.domain.model.User
import java.util.UUID

data class UserItem(
    override val id: UUID,
    val title: String,
    val photoUrl: String,
    val subtitle: String? = null,
) : UiModel

fun GroupMember.toUserItem(groupMembers: GroupMembers): UserItem {
    return UserItem(
        id = id,
        title = fullName,
        photoUrl = photoUrl,
        subtitle = if (groupMembers.isHeadman(this)) "Староста" else null
    )
}

fun User.toUserItem(): UserItem {
    return UserItem(
        id = id,
        title = fullName,
        photoUrl = photoUrl,
        subtitle = null
    )
}