package com.denchic45.studiversity.feature.membership.usecase

import com.denchic45.studiversity.feature.membership.MembershipErrors
import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
import com.denchic45.studiversity.feature.role.repository.RoleRepository
import com.denchic45.studiversity.ktor.ForbiddenException
import com.denchic45.studiversity.transaction.DatabaseTransactionWorker
import io.ktor.server.plugins.*
import java.util.*

class RemoveSelfMemberFromScopeUseCase(
    private val transactionWorker: DatabaseTransactionWorker,
    private val userMembershipRepository: UserMembershipRepository,
    private val roleRepository: RoleRepository
) {
    operator fun invoke(userId: UUID, scopeId: UUID) = transactionWorker {
        if (!userMembershipRepository.existMemberByScopeIds(userId, listOf(scopeId))) {
            throw BadRequestException(MembershipErrors.USER_NOT_EXIST_IN_SCOPE)
        }
        val membershipTypesWithForbiddenSelfDeletion = listOf("by_group", "manual")
        val memberships = userMembershipRepository.findMemberByMembershipTypesAndScopeId(
            userId,
            membershipTypesWithForbiddenSelfDeletion,
            scopeId
        )
        memberships.forEach {
            when (it.type) {
                "by_group" -> throw ForbiddenException("MEMBER_IS_IN_EXTERNAL_MEMBERSHIP")
                "manual" -> throw ForbiddenException("MEMBER_IS_MANUAL_MEMBERSHIP")
            }
        }
        roleRepository.removeUserRolesFromScope(userId, scopeId)
        userMembershipRepository.removeMemberByScopeId(userId, scopeId)
    }
}