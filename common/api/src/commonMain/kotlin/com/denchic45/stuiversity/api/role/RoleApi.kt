package com.denchic45.stuiversity.api.role

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.role.model.UserRolesResponse
import com.denchic45.stuiversity.util.UUIDWrapper
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.util.UUID

interface RoleApi {
    suspend fun assignRoleToUserInScope(userId: UUID, scopeId: UUID, roleId: Long): EmptyResponseResult

    suspend fun assignRolesToUserInScope(userId: UUID, scopeId: UUID, roleIds: List<Long>): EmptyResponseResult

    suspend fun deleteRoleFromUserInScope(userId: UUID, scopeId: UUID, roleId: Long): EmptyResponseResult

    suspend fun getUserRolesInScope(userId: UUIDWrapper, scopeId: UUID): ResponseResult<UserRolesResponse>
}

class RoleApiImpl(private val client: HttpClient) : RoleApi {
    override suspend fun assignRoleToUserInScope(userId: UUID, scopeId: UUID, roleId: Long): EmptyResponseResult {
        return client.put("/users/$userId/scopes/$scopeId/roles/$roleId").toResult()
    }

    override suspend fun assignRolesToUserInScope(
        userId: UUID,
        scopeId: UUID,
        roleIds: List<Long>
    ): EmptyResponseResult {
        return client.put("/users/$userId/scopes/$scopeId/roles") {
            contentType(ContentType.Application.Json)
            setBody(roleIds)
        }.toResult()
    }

    override suspend fun deleteRoleFromUserInScope(userId: UUID, scopeId: UUID, roleId: Long): EmptyResponseResult {
        return client.delete("/users/$userId/scopes/$scopeId/roles/$roleId").toResult()
    }

    override suspend fun getUserRolesInScope(userId: UUIDWrapper, scopeId: UUID): ResponseResult<UserRolesResponse> {
        return client.get("/users/${userId.value}/scopes/$scopeId/roles").toResult()
    }
}