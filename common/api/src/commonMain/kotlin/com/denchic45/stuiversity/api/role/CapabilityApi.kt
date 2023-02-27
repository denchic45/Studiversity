package com.denchic45.stuiversity.api.role

import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.CheckCapabilitiesResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CapabilityApi {
    suspend fun check(
        userId: UUID,
        scopeId: UUID,
         capabilities: List<Capability>
    ): ResponseResult<CheckCapabilitiesResponse>
}

class CapabilityApiImpl(private val client: HttpClient) : CapabilityApi {
    override suspend fun check(
        userId: UUID,
        scopeId: UUID,
        capabilities: List<Capability>
    ): ResponseResult<CheckCapabilitiesResponse> {
        return client.post("/users/$userId/scopes/$scopeId/capabilities/check") {
            contentType(ContentType.Application.Json)
            setBody(capabilities.map(Capability::resource))
        }.toResult()
    }

}

