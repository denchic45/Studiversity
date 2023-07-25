package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.data.storage.MetaStorage
import com.denchic45.studiversity.domain.resource.onSuccess
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject

@Inject
class MetaRepository constructor(
    coroutineScope: CoroutineScope,
    private val appPreferences: AppPreferences,
    private val metaStorage: MetaStorage,
) {

    private val json = Json { ignoreUnknownKeys = true }

    val observeBellSchedule: Flow<BellSchedule> = appPreferences.observeBellSchedule
        .filter(String::isNotEmpty)
        .map(json::decodeFromString)

    init {
        coroutineScope.launch {
            metaStorage.getBellSchedule().onSuccess {
                appPreferences.bellSchedule = json.encodeToString(it)
            }
        }
    }
}