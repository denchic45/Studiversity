package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.stuiversity.util.UUIDWrapper
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class FindCoursesUseCase(private val courseRepository: CourseRepository) {
    operator fun invoke(
        memberId: UUIDWrapper? = null,
        studyGroupId: UUID? = null,
        subjectId: UUID? = null,
        archived: Boolean = false,
        query: String? = null,
    ) = courseRepository.findBy(memberId, studyGroupId, subjectId, archived, query)
}