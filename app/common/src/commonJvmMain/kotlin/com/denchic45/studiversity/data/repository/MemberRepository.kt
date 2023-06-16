package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.member.MembersApi
import com.denchic45.stuiversity.api.membership.MembershipApi
import com.github.michaelbull.result.map
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class MemberRepository(
    override val networkService: NetworkService,
    private val membersApi: MembersApi,
    private val membershipApi: MembershipApi,
) : NetworkServiceOwner {

    suspend fun findMembersByScopeId(scopeId: UUID) = fetchResource {
        membersApi.getByScope(scopeId)
    }

    suspend fun findMemberByScopeIdAndMemberId(scopeId: UUID, memberId: UUID) = fetchResource {
        membersApi.getByScope(scopeId).map { members -> members.first { it.user.id == memberId } }
    }

    suspend fun addByManual(memberId: UUID, scopeId: UUID, roleIds: List<Long>) = fetchResource {
        membershipApi.joinToScopeManually(memberId, scopeId, roleIds)
    }

    suspend fun removeByManual(memberId: UUID, scopeId: UUID) = fetchResource {
        membershipApi.leaveFromScope(memberId, scopeId, "manual")
    }
}