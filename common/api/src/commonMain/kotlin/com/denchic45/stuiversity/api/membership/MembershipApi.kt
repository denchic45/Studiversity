package com.denchic45.stuiversity.api.membership

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.membership.model.ManualJoinMemberRequest
import com.denchic45.stuiversity.api.membership.model.ScopeMember
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface MembershipApi {
    suspend fun joinToScopeManually(userId: UUID, scopeId: UUID, roleIds: List<Long>): ResponseResult<ScopeMember>

    suspend fun leaveFromScope(userId: UUID, scopeId: UUID, action: String): EmptyResponseResult
}

class MembershipApiImpl(private val client: HttpClient) : MembershipApi {
    override suspend fun joinToScopeManually(
        userId: UUID,
        scopeId: UUID,
        roleIds: List<Long>
    ): ResponseResult<ScopeMember> {
        return client.post("/scopes/$scopeId/members?action=manual") {
            contentType(ContentType.Application.Json)
            setBody(ManualJoinMemberRequest(userId, roleIds = roleIds))
        }.toResult()
    }

    override suspend fun leaveFromScope(userId: UUID, scopeId: UUID, action: String): EmptyResponseResult {
        return client.delete("/scopes/$scopeId/members/$userId") {
            parameter("action", action)
        }.toResult()
    }
}