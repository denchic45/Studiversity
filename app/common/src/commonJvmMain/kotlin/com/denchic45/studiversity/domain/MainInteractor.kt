package com.denchic45.studiversity.domain

import com.denchic45.studiversity.data.domain.NotFound
import com.denchic45.studiversity.data.pref.AppPreferences
import com.denchic45.studiversity.data.repository.CourseRepository
import com.denchic45.studiversity.data.repository.EventRepository
import com.denchic45.studiversity.data.repository.StudyGroupRepository
import com.denchic45.studiversity.data.repository.UserRepository
import com.denchic45.studiversity.data.service.AuthService
import com.denchic45.studiversity.data.service.UserService
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn

@me.tatarka.inject.annotations.Inject
class MainInteractor (
    private val coroutineScope: CoroutineScope,
    private val studyGroupRepository: StudyGroupRepository,
    private val authService: AuthService,
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val appPreferences: AppPreferences,
    ) {
    val listenAuthState: Flow<Boolean> = authService.observeIsAuthenticated

    fun observeThisUser(): Flow<Resource<UserResponse>> = userService.observeCurrentUser
        .shareIn(coroutineScope, SharingStarted.Lazily)


    fun observeHasStudyGroups(): Flow<Boolean> = studyGroupRepository.findByMe()
        .filterSuccess()
        .map { it.value.isNotEmpty() }

    fun findThisUser() = userRepository.findSelf()


    suspend fun startListeners() {
        observeThisUser().collect { user ->
//            coroutineScope.launch {
//                listenAuthState.collect {
//                    if (!it) {
//                        clearAllData()
//                    }
//                }
//            }
            user.onFailure {
                if (it is NotFound) {
//                    clearAllData()
                    authService.signOut()
                }
            }
        }

        // TODO: Make observable
//        studyGroupRepository.findByMe()
    }

//    fun findOwnCourses(): Flow<List<CourseHeader>> {
//        val thisUser = findThisUser()
//        return when {
//            thisUser.isTeacher -> courseRepository.findByYourAsTeacher()
//            thisUser.isStudent -> courseRepository.findByYourGroup()
//            else -> emptyFlow()
//        }
//    }

    val yourSelectedStudyGroup = combine(
        studyGroupRepository.findByMe(),
        appPreferences.selectedStudyGroupIdFlow
    ) { yourStudyGroups, selectedId ->
        yourStudyGroups.map { groups ->
            if (selectedId == null) {
                groups.firstOrNull()?.also {
                    appPreferences.selectedStudyGroupId = it.id.toString()
                }
            } else {
                groups.firstOrNull { it.id == selectedId.toUUID() }
                    .also { if (it == null) appPreferences.selectedStudyGroupId = null }
            }
        }
    }.stateInResource(coroutineScope, SharingStarted.Eagerly)

}