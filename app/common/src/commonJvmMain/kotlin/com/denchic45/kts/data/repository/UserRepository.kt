package com.denchic45.kts.data.repository

import com.denchic45.kts.data.db.local.source.UserLocalDataSource
import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.mapper.toEntity
import com.denchic45.kts.data.mapper.toUserResponse
import com.denchic45.kts.data.observeResource
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.toResource
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.Account
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.Gender
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class UserRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    private val userPreferences: UserPreferences,
    override val networkService: NetworkService,
    private val userLocalDataSource: UserLocalDataSource,
    private val userApi: UserApi,
) : Repository(), FindByContainsNameRepository<UserResponse>, NetworkServiceOwner {

    override suspend fun findByContainsName(text: String): Resource<List<UserResponse>> {
        return fetchResource {
            userApi.search(text)
        }
    }

    fun findSelf() = UserResponse(
        UUID.fromString(userPreferences.id),
        userPreferences.firstName,
        userPreferences.surname,
        userPreferences.patronymic,
        Account(userPreferences.email),
        userPreferences.avatarUrl,
        Gender.valueOf(userPreferences.gender)
    )

    suspend fun updateUserAvatar(avatarBytes: ByteArray, userId: String): String {
        TODO("Not implemented")
    }

    suspend fun add(createUserRequest: CreateUserRequest): Resource<UserResponse> {
        return userApi.create(createUserRequest).toResource()
    }

    fun observeById(userId: UUID): Flow<Resource<UserResponse?>> = observeResource(
        query = userLocalDataSource.observe(userId.toString())
            .distinctUntilChanged()
            .map { it?.toUserResponse() },
        fetch = { userApi.getById(userId) },
        saveFetch = { userLocalDataSource.upsert(it.toEntity()) }
    )
}