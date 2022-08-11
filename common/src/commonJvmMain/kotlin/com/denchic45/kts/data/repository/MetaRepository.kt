package com.denchic45.kts.data.repository

import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.storage.MetaStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MetaRepository @Inject constructor(
    coroutineScope: CoroutineScope,
    private val appPreferences: AppPreferences,
    private val metaStorage: MetaStorage,
) {
    val lessonTime: Flow<Int> = appPreferences.observeLessonTime

    private suspend fun getMetaData() {
        metaStorage.get().let { meta ->
            appPreferences.lessonTime = meta.lesson_time
        }
    }

    init {
        coroutineScope.launch { getMetaData() }
    }
}