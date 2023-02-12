package com.stuiversity.api.membership

import com.stuiversity.api.membership.model.ManualJoinMemberRequest
import com.stuiversity.api.membership.model.ScopeMember
import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toResult
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface MembershipsApi {
    suspend fun joinToScopeManually(userId: UUID, scopeId: UUID, roleIds: List<Long>): ResponseResult<ScopeMember>

    suspend fun leaveFromScope(userId: UUID, scopeId: UUID, action: String): EmptyResponseResult
}

class MembershipsApiImpl(private val client: HttpClient) : MembershipsApi {
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