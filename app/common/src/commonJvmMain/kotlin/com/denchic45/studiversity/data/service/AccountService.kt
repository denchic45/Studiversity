package com.denchic45.studiversity.data.service

import com.denchic45.studiversity.api.account.AccountApi
import com.denchic45.studiversity.api.account.model.AccountEmailResponse
import com.denchic45.studiversity.api.account.model.UpdateAccountPersonalRequest
import com.denchic45.studiversity.api.account.model.UpdatePasswordRequest
import com.denchic45.studiversity.api.common.EmptyResponseResult
import com.denchic45.studiversity.api.user.UserApi
import com.denchic45.studiversity.api.user.model.Account
import com.denchic45.studiversity.api.user.model.Gender
import com.denchic45.studiversity.api.user.model.UserResponse
import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.data.preference.UserPreferences
import com.denchic45.studiversity.domain.resource.ClientError
import com.denchic45.studiversity.domain.resource.EmptyResource
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.toResource
import com.github.michaelbull.result.mapBoth
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class AccountService(
    private val userPreferences: UserPreferences,
    private val appPreferences: AppPreferences,
    lazyAccountApi: Lazy<AccountApi>,
    lazyUserApi: Lazy<UserApi>,
) {
    private val userApi: UserApi by lazyUserApi
    private val accountApi: AccountApi by lazyAccountApi

    fun findMe(): UserResponse {
        println("check token: ${appPreferences.token}")
        return findSelfLocally()
    }

    private fun findSelfLocally() = UserResponse(
        UUID.fromString(userPreferences.id),
        userPreferences.firstName,
        userPreferences.surname,
        userPreferences.patronymic,
        Account(userPreferences.email),
        userPreferences.avatarUrl,
        userPreferences.isGeneratedAvatar,
        Gender.valueOf(userPreferences.gender)
    )

    private fun saveUserPreference(userResponse: UserResponse) {
        println("A save user data")
        userPreferences.apply {
            id = userResponse.id.toString()
            firstName = userResponse.firstName
            surname = userResponse.surname
            patronymic = userResponse.patronymic ?: ""
            gender = userResponse.gender.name
            avatarUrl = userResponse.avatarUrl
            isGeneratedAvatar = userResponse.generatedAvatar
            email = userResponse.account.email
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val observeCurrentUser: Flow<Resource<UserResponse>> = appPreferences.observeToken
        .filter {
            val notEmpty = it.isNotEmpty()
            println("OBSERVE TOKEN: VALUE: $it")
            println("OBSERVE TOKEN: FILTER: $notEmpty")
            notEmpty
        }
        .map { userApi.getMe() }
        .flatMapLatest {
            userApi.getMe().mapBoth(
                success = { userPreferences.observeLastUpdateTimestamp.map { resourceOf(it) } },
                failure = { flowOf(resourceOf(ClientError(it))) }
            )
        }
        .mapResource { findSelfLocally() }

    suspend fun updatePersonal(request: UpdateAccountPersonalRequest): EmptyResponseResult {
        return accountApi.updatePersonal(request).onSuccess {
            userApi.getMe().onSuccess(::saveUserPreference)
        }
    }

    suspend fun updatePassword(request: UpdatePasswordRequest): EmptyResource {
        return accountApi.updatePassword(request).toResource()
    }

    suspend fun updateEmail(email: String): EmptyResource {
        return accountApi.updateEmail(email).toResource()
    }

    suspend fun findEmail(): Resource<AccountEmailResponse> {
        return accountApi.getEmail().toResource()
    }
}