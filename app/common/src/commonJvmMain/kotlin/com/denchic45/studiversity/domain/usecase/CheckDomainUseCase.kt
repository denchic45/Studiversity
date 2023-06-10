package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.domain.Cause
import com.denchic45.studiversity.data.service.AuthService
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.stuiversity.api.Pong
import io.ktor.client.call.body
import me.tatarka.inject.annotations.Inject

@Inject
class CheckDomainUseCase(
    private val authService: AuthService,
) {
    suspend operator fun invoke(url: String): Resource<Pong> {
        val response = try {
            authService.checkDomain(url)
        } catch (e: Exception) {
            return resourceOf(Cause(e))
        }
        return try {
            val pong = response.body<Pong>()
            resourceOf(pong)
        } catch (e: Exception) {
            resourceOf(Cause(e))
        }
    }
}