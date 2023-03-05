package com.denchic45.kts.domain.model

import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.domain.model.CourseHeader
import com.denchic45.kts.domain.model.GroupHeader
import java.util.UUID

data class GroupCourses(
    val groupHeader: GroupHeader,
    val courses: List<CourseHeader>
) : DomainModel {

    override val id: UUID = groupHeader.id
}

