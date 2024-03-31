package com.denchic45.studiversity.api.user.model

import com.denchic45.studiversity.util.OptionalProperty
import com.denchic45.studiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserRequest(
    @Serializable(OptionalPropertySerializer::class)
    val firstName: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val surname: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val patronymic: OptionalProperty<String?> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val gender: OptionalProperty<Gender> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val roleIds: OptionalProperty<List<Long>> = OptionalProperty.NotPresent
)