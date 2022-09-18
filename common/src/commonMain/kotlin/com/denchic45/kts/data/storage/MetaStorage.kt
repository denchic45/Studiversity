package com.denchic45.kts.data.storage

import com.denchic45.kts.data.service.model.Meta
import com.denchic45.kts.data.storage.remote.MetaRemoteStorage
import com.denchic45.kts.di.FirebaseHttpClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.tatarka.inject.annotations.Inject

@Inject
class MetaStorage(
    private val metaRemoteStorage: MetaRemoteStorage,
    private val client: FirebaseHttpClient,
) {

    suspend fun getMeta(): Meta = client.get(metaRemoteStorage.getMetaUrl()).body()

    suspend fun getBellSchedule(): String {
        return client.get(metaRemoteStorage.getBellScheduleUrl()).bodyAsText()
    }
}