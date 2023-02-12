package com.stuiversity.api.role

import com.stuiversity.api.common.EmptyResponseResult
import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toResult
import com.stuiversity.api.role.model.UserRolesResponse
import io.ktor.client.*
import io.ktor.client.request.*
import java.util.*

interface RoleApi {
    suspend fun assignRoleToUserInScope(userId: UUID, scopeId: UUID, roleId: Long): EmptyResponseResult

    suspend fun deleteRoleFromUserInScope(userId: UUID, scopeId: UUID, roleId: Long): EmptyResponseResult

    suspend fun getUserRolesInScope(userId: UUID, scopeId: UUID): ResponseResult<UserRolesResponse>
}

class RoleApiImpl(private val client: HttpClient) : RoleApi {
    override suspend fun assignRoleToUserInScope(userId: UUID, scopeId: UUID, roleId: Long): EmptyResponseResult {
        return client.put("/users/$userId/scopes/$scopeId/roles/$roleId").toResult()
    }

    override suspend fun deleteRoleFromUserInScope(userId: UUID, scopeId: UUID, roleId: Long): EmptyResponseResult {
        return client.delete("/users/$userId/scopes/$scopeId/roles/$roleId").toResult()
    }

    override suspend fun getUserRolesInScope(userId: UUID, scopeId: UUID): ResponseResult<UserRolesResponse> {
        return client.get("/users/$userId/scopes/$scopeId/roles").toResult()
    }
}