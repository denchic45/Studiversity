package com.denchic45.kts.domain.model

import com.denchic45.kts.data.domain.model.DomainModel
import java.util.*

data class Course(
    override var id: UUID,
    val name: String,
    val subject: Subject,
    val groupHeaders: List<GroupHeader>,
) : DomainModel {

    companion object {
        fun createEmpty() = Course()
    }

    private constructor() : this(UUID.randomUUID(), "", Subject.createEmpty(), emptyList())
}

data class CourseHeader(
    override var id: UUID,
    val name: String,
    val subject: Subject,
) : DomainModel