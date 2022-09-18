package com.denchic45.kts.data.storage.remote

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

actual class MetaRemoteStorage(
    private val storage: FirebaseStorage,
) {
    actual suspend fun getMetaUrl(): String {
        return storage.getReference("meta.json").downloadUrl.await().toString()
    }

    actual suspend fun getBellScheduleUrl(): String {
        return storage.getReference("schedule.json").downloadUrl.await().toString()
    }
}