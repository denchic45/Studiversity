package com.denchic45.stuiversity.api.user.model

import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UserResponse(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    val account: Account,
    val avatarUrl: String,
    val gender: Gender,
) {
    val fullName: String
        get() = "$firstName  $surname"
}