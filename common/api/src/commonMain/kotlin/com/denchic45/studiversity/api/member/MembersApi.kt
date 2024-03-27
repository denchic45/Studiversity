package com.denchic45.studiversity.api.member

import com.denchic45.studiversity.api.common.ResponseResult
import com.denchic45.studiversity.api.common.toResult
import com.denchic45.studiversity.api.membership.model.ScopeMember
import io.ktor.client.*
import io.ktor.client.request.*
import java.util.UUID

interface MembersApi {
    suspend fun getByScope(scopeId:UUID):ResponseResult<List<ScopeMember>>

    suspend fun getScopeMember(scopeId:UUID,memberId:UUID):ResponseResult<ScopeMember>
}

class MembersApiImpl(private val client:HttpClient):MembersApi {
    override suspend fun getByScope(scopeId: UUID): ResponseResult<List<ScopeMember>> {
        return client.get("/scopes/${scopeId}/members").toResult()
    }

    override suspend fun getScopeMember(
        scopeId: UUID,
        memberId: UUID
    ): ResponseResult<ScopeMember> {
        return client.get("/scopes/${scopeId}/members/$memberId").toResult()
    }
}