package com.denchic45.kts.ui.main

import android.content.Context
import com.denchic45.kts.data.Interactor
import com.denchic45.kts.data.db.local.DbHelper
import com.denchic45.kts.data.repository.*
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.di.module.IoDispatcher
import com.denchic45.kts.domain.model.CourseHeader
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.util.SystemDirs
import com.denchic45.kts.util.databaseFile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MainInteractor @Inject constructor(
    private val context: Context,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val coroutineScope: CoroutineScope,
    private val groupRepository: GroupRepository,
    private val authService: AuthService,
    private val userRepository: UserRepository,
    private val eventRepository: EventRepository,
    private val courseRepository: CourseRepository,
    private val dbHelper: DbHelper,
    private val systemDirs: SystemDirs
) : Interactor {

    val listenAuthState: Flow<Boolean> = authService.observeIsAuthenticated

    init {
        coroutineScope.launch(dispatcher) {
            listenAuthState.collect {
                if (!it) {
                    clearAllData(context)
                }
            }
        }
    }

    private fun clearAllData(context: Context) {
//        dataBase.clearAllTables()
        dbHelper.driver.close()
        systemDirs.databaseFile.delete()


        val sharedPreferenceFile =
            systemDirs.prefsDirectory
//            File("/data/data/" + context.packageName + "/shared_prefs/")
        val listFiles = sharedPreferenceFile.listFiles()!!
        for (file in listFiles) {
            val name = file.name
            val sharedPrefs =
                context.getSharedPreferences(name.replace(".xml", ""), Context.MODE_PRIVATE)
            val editor = sharedPrefs.edit()
            editor.clear()
            editor.apply()
        }
    }

    override fun removeListeners() {
        groupRepository.removeListeners()
    }

    suspend fun observeHasGroup(): Flow<Boolean> {
        return flow {
            coroutineScope {
                groupRepository.observeHasGroup().collect {
                    if (it) {
                        launch { eventRepository.observeEventsOfYourGroup() }
                    }
                    emit(it)
                }
            }
        }
    }

    fun findThisUser(): User = userRepository.findSelf()

    fun observeThisUser(): Flow<User?> = userRepository.thisUserObserver

    suspend fun startListeners() {
        observeThisUser().collect { user ->
            user?.let {
                if (user.isTeacher) {
                    coroutineScope.launch {
                        groupRepository.listenYouGroupByCurator()
                    }
                    coroutineScope.launch {
                        groupRepository.listenGroupsWhereThisUserIsTeacher(user)
                    }
                } else if (user.isStudent) {
                    coroutineScope.launch {
                        groupRepository.observeYourGroupById().collect()
                    }
                }
                courseRepository.observeByYourGroup()
            } ?: run {
                clearAllData(context)
                authService.signOut()
            }
        }
    }

    fun findOwnCourses(): Flow<List<CourseHeader>> {
        val thisUser = findThisUser()
        return when {
            thisUser.isTeacher -> courseRepository.findByYourAsTeacher()
            thisUser.isStudent -> courseRepository.findByYourGroup()
            else -> emptyFlow()
        }
    }
}