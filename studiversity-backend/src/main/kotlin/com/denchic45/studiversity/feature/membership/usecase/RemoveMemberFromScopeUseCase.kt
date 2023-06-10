package com.denchic45.studiversity.feature.membership.usecase

import com.denchic45.studiversity.feature.membership.MembershipErrors
import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.ktor.ConflictException
import com.denchic45.studiversity.transaction.DatabaseTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RemoveMemberFromScopeUseCase(
    private val transactionWorker: DatabaseTransactionWorker,
    private val userMembershipRepository: UserMembershipRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, scopeId: UUID) = transactionWorker {
        if (!userMembershipRepository.existMemberByScopeIds(userId, listOf(scopeId))) {
            throw BadRequestException(MembershipErrors.USER_NOT_EXIST_IN_SCOPE)
        }
        val externalMembershipTypes = listOf("by_group") // Types who prevent remove member
        val memberships =
            userMembershipRepository.findMemberByMembershipTypesAndScopeId(userId, externalMembershipTypes, scopeId)
        if (memberships.isNotEmpty())
            throw ConflictException("MEMBER_IS_IN_EXTERNAL_MEMBERSHIP")
        roleRepository.removeUserRolesFromScope(userId, scopeId)
        userMembershipRepository.removeMemberByScopeId(userId,scopeId)
    }
}