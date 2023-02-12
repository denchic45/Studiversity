package com.denchic45.kts.data.storage.remote

import com.denchic45.kts.ApiKeys
import me.tatarka.inject.annotations.Inject

@Inject
actual class MetaRemoteStorage {

    actual suspend fun getMetaUrl(): String {
        TODO("Not yet implemented")
    }

    actual suspend fun getBellScheduleUrl(): String {
        return "https://firebasestorage.googleapis.com/v0/b/${ApiKeys.firebaseProjectId}.appspot.com/o/schedule.json?alt=media&token"
    }
}