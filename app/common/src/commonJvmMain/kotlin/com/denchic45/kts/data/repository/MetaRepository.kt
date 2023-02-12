package com.denchic45.kts.data.repository

import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.storage.MetaStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
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

    val observeBellSchedule: Flow<BellSchedule> = appPreferences.observeBellSchedule
        .filter(String::isNotEmpty)
        .map(Json::decodeFromString)

    init {
        coroutineScope.launch {
            metaStorage.getBellSchedule()?.also {
                appPreferences.bellSchedule = it
            }
        }
    }
}