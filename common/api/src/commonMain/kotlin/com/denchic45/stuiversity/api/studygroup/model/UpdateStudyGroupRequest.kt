package com.denchic45.stuiversity.api.studygroup.model

import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.OptionalPropertySerializer
import com.denchic45.stuiversity.util.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class UpdateStudyGroupRequest(
    @Serializable(with = OptionalPropertySerializer::class)
    val name: OptionalProperty<String> = OptionalProperty.NotPresent,
    @Serializable(with = OptionalPropertySerializer::class)
    val academicYear: OptionalProperty<AcademicYear> = OptionalProperty.NotPresent,
    @Serializable(with = OptionalPropertySerializer::class)
    val specialtyId: OptionalProperty<@Serializable(UUIDSerializer::class) UUID?> = OptionalProperty.NotPresent,
    @Serializable(with = OptionalPropertySerializer::class)
    val curatorId: OptionalProperty<@Serializable(UUIDSerializer::class) UUID?> = OptionalProperty.NotPresent
)