package com.denchic45.kts.data.storage.remote

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

actual class MetaRemoteStorage @Inject constructor(
    private val storage: FirebaseStorage,
) {
    actual suspend fun getMetaUrl(): String {
        return storage.getReference("meta.json").downloadUrl.await().toString()
    }

    actual suspend fun getBellScheduleUrl(): String {
        return storage.getReference("schedule.json").downloadUrl.await().toString()
    }
}