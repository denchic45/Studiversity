package com.denchic45.kts.data.repository

import com.denchic45.kts.data.network.model.BellSchedule
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.storage.MetaStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class MetaRepository @Inject constructor(
    coroutineScope: CoroutineScope,
    private val appPreferences: AppPreferences,
    private val metaStorage: MetaStorage,
) {
    val lessonTime: Flow<Int> = appPreferences.observeLessonTime

    val bellSchedule: BellSchedule = Json.decodeFromString(appPreferences.bellSchedule)

    init {
        coroutineScope.launch {
            metaStorage.getBellSchedule()?.apply {
                println("bell: $this")
                appPreferences.bellSchedule = this
            }
        }
    }
}