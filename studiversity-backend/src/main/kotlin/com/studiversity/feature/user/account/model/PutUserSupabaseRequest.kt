package com.studiversity.feature.user.account.model

import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.OptionalPropertySerializer
import kotlinx.serialization.Serializable


@Serializable
data class PutUserSupabaseRequest(
    @Serializable(OptionalPropertySerializer::class)
    val email: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val password: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(OptionalPropertySerializer::class)
    val phone: OptionalProperty<String> = OptionalProperty.NotPresent
)