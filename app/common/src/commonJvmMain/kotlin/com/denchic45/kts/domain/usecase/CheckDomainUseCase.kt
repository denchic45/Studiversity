package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.Cause
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.resourceOf
import com.denchic45.stuiversity.api.Pong
import io.ktor.client.call.body
import me.tatarka.inject.annotations.Inject

@Inject
class CheckDomainUseCase(
    private val authService: AuthService,
) {
    suspend operator fun invoke(url: String): Resource<Pong> {
        val response = authService.checkDomain(url)
        return try {
            val pong = response.body<Pong>()
            resourceOf(pong)
        } catch (e: Exception) {
            resourceOf(Cause(e))
        }
    }
}