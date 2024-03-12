package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.UserLocalDataSource
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.mapper.toEntity
import com.denchic45.studiversity.data.mapper.toUserResponse
import com.denchic45.studiversity.data.observeResource
import com.denchic45.studiversity.data.preference.UserPreferences
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.resource.EmptyResource
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.toResource
import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class UserRepository(
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
        userPreferences.isGeneratedAvatar,
        Gender.valueOf(userPreferences.gender)
    )

    fun findById(userId: UUID): Flow<Resource<UserResponse>> = fetchResourceFlow {
        userApi.getById(userId)
    }

    suspend fun update(userId: UUID, request: UpdateUserRequest): Resource<UserResponse> = fetchResource {
        userApi.update(userId, request)
    }

    suspend fun updateAvatar(userId: UUID, request: CreateFileRequest): Resource<String> {
        return fetchResource { userApi.updateAvatar(userId, request) }.onSuccess {
            userLocalDataSource.updateAvatar(userId.toString(), it, false)
        }
    }

    suspend fun removeAvatar(userId: UUID): Resource<String> {
        return fetchResource { userApi.deleteAvatar(userId) }.onSuccess {
            userLocalDataSource.updateAvatar(userId.toString(), it, true)
        }
    }

    suspend fun add(createUserRequest: CreateUserRequest): Resource<UserResponse> {
        return userApi.create(createUserRequest).toResource()
    }

    fun observeById(userId: UUID): Flow<Resource<UserResponse?>> = observeResource(
        query = userLocalDataSource.observe(userId.toString())
            .distinctUntilChanged()
            .map { it?.toUserResponse() },
        fetch = { userApi.getById(userId) },
        saveFetch = {
            userLocalDataSource.upsert(it.toEntity())
        }
    )

    suspend fun remove(userId: UUID): EmptyResource {
        return userApi.delete(userId).toResource()
    }
}