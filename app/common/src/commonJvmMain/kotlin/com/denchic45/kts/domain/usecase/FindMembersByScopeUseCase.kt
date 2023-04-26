package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.repository.MemberRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class FindMembersByScopeUseCase(
    private val memberRepository: MemberRepository
) {

    suspend operator fun invoke(scopeId:UUID): Resource<List<ScopeMember>> {
        return memberRepository.findMembersByScopeId(scopeId)
    }
}