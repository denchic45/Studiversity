package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.data.repository.CourseMemberRepository
import com.denchic45.studiversity.domain.resource.EmptyResource
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class EnrolCourseMemberUseCase(
    private val courseMemberRepository: CourseMemberRepository
) {

    suspend operator fun invoke(courseId: UUID, request: CreateMemberRequest): EmptyResource {
        return courseMemberRepository.enrol(courseId, request)
    }
}