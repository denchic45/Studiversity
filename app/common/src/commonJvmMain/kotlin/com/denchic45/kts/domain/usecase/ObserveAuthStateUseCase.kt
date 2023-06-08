package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.db.local.DbHelper
import com.denchic45.kts.data.service.AuthService
import com.denchic45.kts.util.SystemDirs
import com.denchic45.kts.util.databaseFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveAuthStateUseCase(
    private val dbHelper: DbHelper,
    private val systemDirs: SystemDirs,
    private val authService: AuthService
) {

    operator fun invoke(): Flow<Boolean> {
        return authService.observeIsAuthenticated.onEach {
            if (!it) {
                clearAllData()
            }
        }
    }

    private fun clearAllData() {
        dbHelper.driver.close()
        systemDirs.databaseFile.delete()
        systemDirs.prefsDir.listFiles()!!.forEach { it.delete() }
    }
}