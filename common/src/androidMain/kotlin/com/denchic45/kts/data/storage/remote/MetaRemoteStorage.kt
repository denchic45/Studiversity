package com.denchic45.kts.data.storage.remote

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

actual class MetaRemoteStorage(
    private val storage: FirebaseStorage,
) {
    actual suspend fun getUrl(): String {
        return storage.getReference("meta.json").downloadUrl.await().toString()
    }
}