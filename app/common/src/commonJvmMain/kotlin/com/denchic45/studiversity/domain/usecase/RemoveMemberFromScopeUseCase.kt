package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.MemberRepository
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class RemoveMemberFromScopeUseCase @Inject constructor(
    private val roleRepository: MemberRepository
) {

    suspend operator fun invoke(userId: UUID, scopeId: UUID) {
        roleRepository.removeByManual(userId, scopeId)
    }
}