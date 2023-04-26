package com.denchic45.kts.data.repository

import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.stuiversity.api.member.MembersApi
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class MemberRepository(
    override val networkService: NetworkService,
    private val membersApi: MembersApi
):NetworkServiceOwner {

    suspend fun findMembersByScopeId(scopeId:UUID) = fetchResource {
        membersApi.getByScope(scopeId)
    }
}