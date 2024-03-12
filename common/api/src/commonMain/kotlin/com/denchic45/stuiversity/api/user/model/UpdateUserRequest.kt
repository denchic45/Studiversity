package com.denchic45.stuiversity.api.user.model

import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    @Serializable(OptionalPropertySerializer::class)
    val firstName: OptionalProperty<String>,
    @Serializable(OptionalPropertySerializer::class)
    val surname: OptionalProperty<String>,
    @Serializable(OptionalPropertySerializer::class)
    val patronymic: OptionalProperty<String?>,
    @Serializable(OptionalPropertySerializer::class)
    val gender: OptionalProperty<Gender>,
    @Serializable(OptionalPropertySerializer::class)
    val roleIds: OptionalProperty<List<Long>>
)