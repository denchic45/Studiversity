package com.denchic45.kts.domain

import com.denchic45.kts.data.db.local.DbHelper
import com.denchic45.kts.data.domain.NotFound
import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.data.repository.EventRepository
import com.denchic45.kts.data.repository.StudyGroupRepository
import com.denchic45.kts.data.repository.UserRepository
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.util.SystemDirs
import com.denchic45.kts.util.databaseFile
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class MainInteractor @Inject constructor(
    private val coroutineScope: CoroutineScope,
    private val studyGroupRepository: StudyGroupRepository,
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val courseRepository: CourseRepository,
    private val dbHelper: DbHelper,
    private val systemDirs: SystemDirs,
) : Interactor {

    val listenAuthState: Flow<Boolean> = authService.observeIsAuthenticated

    private fun clearAllData() {
        dbHelper.driver.close()
        systemDirs.databaseFile.delete()
        systemDirs.prefsDirectory.listFiles()!!.forEach { it.delete() }
    }

    override fun removeListeners() {

    }

//    suspend fun observeHasGroup(): Flow<Boolean> = flow {
//        coroutineScope {
//            groupRepository.observeHasGroup().collect {
//                if (it) {
//                    launch { eventRepository.observeEventsOfYourGroup() }
//                }
//                emit(it)
//            }
//        }
//    }

    fun findThisUser() = userRepository.findSelf()

    fun observeThisUser(): Flow<Resource<UserResponse>> = authService.observeCurrentUser

    suspend fun startListeners() {
        observeThisUser().collect { user ->
            coroutineScope.launch {
                listenAuthState.collect {
                    if (!it) {
                        clearAllData()
                    }
                }
            }
            user.onFailure {
                if (it is NotFound) {
                    clearAllData()
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
}