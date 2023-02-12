package com.stuiversity.api.role

import com.stuiversity.api.common.ResponseResult
import com.stuiversity.api.common.toResult
import com.stuiversity.api.role.model.Capability
import com.stuiversity.api.role.model.CheckCapabilitiesResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import java.util.*

interface CapabilityApi {
    suspend fun check(
        userId: UUID,
        scopeId: UUID,
        vararg capabilities: Capability
    ): ResponseResult<CheckCapabilitiesResponse>
}

class CapabilityApiImpl(private val client: HttpClient) : CapabilityApi {
    override suspend fun check(
        userId: UUID,
        scopeId: UUID,
        vararg capabilities:Capability
    ): ResponseResult<CheckCapabilitiesResponse> {
        return client.post("/users/$userId/scopes/$scopeId/capabilities/check") {
            contentType(ContentType.Application.Json)
            setBody(capabilities.map(Capability::resource))
        }.toResult()
    }

}

