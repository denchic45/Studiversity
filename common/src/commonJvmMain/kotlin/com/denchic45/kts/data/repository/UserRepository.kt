package com.denchic45.kts.data.repository

import com.denchic45.kts.data.db.local.source.UserLocalDataSource
import com.denchic45.kts.data.db.remote.source.UserRemoteDataSource
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.remote.UserRemoteStorage
import com.denchic45.kts.domain.model.User
import kotlinx.coroutines.flow.*
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
open class UserRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    private val userPreferences: UserPreferences,
    override val networkService: NetworkService,
    private val userLocalDataSource: UserLocalDataSource,
    private val userRemoteDataSource: UserRemoteDataSource,
    private val userRemoteStorage: UserRemoteStorage,
) : Repository(), FindByContainsNameRepository<User> {

    override fun findByContainsName(text: String): Flow<List<User>> {
        return userRemoteDataSource.findByContainsName(text)
            .map { users ->
                userLocalDataSource.upsert(users.mapsToUserEntities())
                users.mapsToUsers()
            }
    }

    private fun saveUserPreference(user: User) {
        userPreferences.apply {
            id = user.id
            firstName = user.firstName
            patronymic = user.patronymic ?: ""
            surname = user.surname
            role = user.role.toString()
            gender = user.gender
            photoUrl = user.photoUrl
            email = user.email
            isAdmin = user.admin
            timestamp = user.timestamp!!.time
            isGeneratedAvatar = user.generatedAvatar
            user.groupId?.let { groupId = it }
        }
    }

    fun findSelf(): User = User(
        userPreferences.id,
        userPreferences.firstName,
        userPreferences.surname,
        userPreferences.patronymic,
        userPreferences.groupId.let {
            if (userPreferences.groupId.isNotEmpty()) it else null
        },
        UserRole.valueOf(userPreferences.role),
        userPreferences.email,
        userPreferences.photoUrl,
        Date(userPreferences.timestamp), userPreferences.gender,
        userPreferences.isGeneratedAvatar, userPreferences.isAdmin
    )


    suspend fun loadAvatar(avatarBytes: ByteArray, userId: String): String {
        return userRemoteStorage.uploadAvatar(avatarBytes, userId)
    }

    suspend fun findAndSaveByEmail(email: String) {
        saveUserPreference(userRemoteDataSource.findByEmail(email).mapToUser())
    }

    val thisUserObserver: Flow<User?> = userPreferences.observeId
        .filter(String::isNotEmpty)
        .flatMapConcat(userRemoteDataSource::observeById)
        .map { value ->
            if (value == null) {
                return@map null
            } else {
                val user = value.mapToUser()
                if (user.timestamp != null) {
                    saveUserPreference(user)
                }
                return@map user
            }
        }


    fun observeById(userId: String): Flow<User?> = flow {
        if (!userLocalDataSource.isExist(userId)) {
            userRemoteDataSource.observeById(userId)
                .collect { map ->
                    map?.let {
                        userLocalDataSource.upsert(map.mapToUserEntity())
                    }
                }
        }
        emitAll(
            userLocalDataSource.observe(userId)
                .distinctUntilChanged()
                .map { it?.toUserDomain() }
        )
    }
}