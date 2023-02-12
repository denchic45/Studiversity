package com.stuiversity.api.user.model

import com.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class User(
    @Serializable(UUIDSerializer::class)
    val id: UUID,
    val firstName: String,
    val surname: String,
    val patronymic: String?,
    val account: Account
)