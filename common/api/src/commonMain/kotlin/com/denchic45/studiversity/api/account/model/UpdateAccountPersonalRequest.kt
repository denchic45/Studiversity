package com.denchic45.studiversity.api.account.model

import com.denchic45.studiversity.api.user.model.Gender
import com.denchic45.studiversity.util.OptionalProperty
import com.denchic45.studiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccountPersonalRequest(
    @Serializable(OptionalPropertySerializer::class)
    val firstName: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val surname: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val patronymic: OptionalProperty<String?> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val gender: OptionalProperty<Gender> = OptionalProperty.NotPresent
)
