package com.denchic45.kts.data.repository

import com.denchic45.kts.data.db.local.source.UserLocalDataSource
import com.denchic45.kts.data.fetchResourceFlow
import com.denchic45.kts.data.mapper.toEntity
import com.denchic45.kts.data.mapper.toUserResponse
import com.denchic45.kts.data.observeResource
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.EmptyResource
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
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class UserRepository @Inject constructor(
    private val userPreferences: UserPreferences,
    override val networkService: NetworkService,
    private val userLocalDataSource: UserLocalDataSource,
    private val userApi: UserApi,
) : FindByContainsNameRepository<UserResponse>, NetworkServiceOwner {

    override fun findByContainsName(text: String) = fetchResourceFlow {
        userApi.getList(text)
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

    suspend fun updateUserAvatar(avatarBytes: ByteArray, userId: UUID): String {
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

    suspend fun remove(userId: UUID): EmptyResource {
        return userApi.delete(userId).toResource()
    }
}