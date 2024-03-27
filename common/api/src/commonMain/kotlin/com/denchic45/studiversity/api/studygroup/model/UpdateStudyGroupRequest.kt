package com.denchic45.studiversity.api.studygroup.model

import com.denchic45.studiversity.util.OptionalProperty
import com.denchic45.studiversity.util.OptionalPropertySerializer
import com.denchic45.studiversity.util.UUIDSerializer
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