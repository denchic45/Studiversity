package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.api.member.ScopeMember
import com.denchic45.studiversity.data.repository.CourseMemberRepository
import com.denchic45.studiversity.domain.resource.Resource
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class FindCourseMembersUseCase(
    private val courseMemberRepository: CourseMemberRepository
) {

    operator fun invoke(scopeId: UUID): Flow<Resource<List<ScopeMember>>> {
        return courseMemberRepository.findMembersByCourseId(scopeId)
    }
}