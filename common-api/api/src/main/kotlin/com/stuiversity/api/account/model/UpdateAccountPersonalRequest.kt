package com.stuiversity.api.account.model

import com.stuiversity.util.OptionalProperty
import com.stuiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAccountPersonalRequest(
    @Serializable(OptionalPropertySerializer::class)
    val firstName: OptionalProperty<String>,
    @Serializable(OptionalPropertySerializer::class)
    val surname: OptionalProperty<String>,
    @Serializable(OptionalPropertySerializer::class)
    val patronymic: OptionalProperty<String?>,
)
