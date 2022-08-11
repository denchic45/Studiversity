package com.denchic45.kts.data.storage

import com.denchic45.kts.data.service.model.Meta
import com.denchic45.kts.data.storage.remote.MetaRemoteStorage
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class MetaStorage(
    private val metaRemoteStorage: MetaRemoteStorage,
    private val client: HttpClient,
) {

    suspend fun get(): Meta = client.get(metaRemoteStorage.getUrl()).body()
}