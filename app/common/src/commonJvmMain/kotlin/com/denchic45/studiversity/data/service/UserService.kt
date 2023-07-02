package com.denchic45.studiversity.data.service

import com.denchic45.studiversity.data.pref.AppPreferences
import com.denchic45.studiversity.data.pref.UserPreferences
import com.denchic45.studiversity.domain.EmptyResource
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.toEmptyResource
import com.denchic45.studiversity.domain.toResource
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

@Inject
class UserService(
    private val userPreferences: UserPreferences,
    private val appPreferences: AppPreferences,
    userApiLazy: Lazy<UserApi>,
) {

    private val userApi: UserApi by userApiLazy

    suspend fun findMe(): EmptyResource {
        println("check token: ${appPreferences.token}")
        return userApi.getMe().onSuccess(::saveUserPreference)
            .toEmptyResource()
    }

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

    val observeCurrentUser: Flow<Resource<UserResponse>> = appPreferences.observeToken
        .filter {
            val notEmpty = it.isNotEmpty()
            println("OBSERVE TOKEN: VALUE: $it")
            println("OBSERVE TOKEN: FILTER: $notEmpty")
            notEmpty
        }
        .map { userApi.getMe() }
        .onEach {
            it.onSuccess(::saveUserPreference)
                .onFailure { println("OBSERVE USER: NOT SAVE USER: $it") }
        }
        .map { value -> value.toResource() }
}