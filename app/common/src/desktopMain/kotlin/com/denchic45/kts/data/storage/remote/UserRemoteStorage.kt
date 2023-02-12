package com.denchic45.kts.data.storage.remote

import com.denchic45.kts.ApiKeys
import com.denchic45.kts.di.FirebaseHttpClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import me.tatarka.inject.annotations.Inject

@Inject
actual class UserRemoteStorage(private val client: FirebaseHttpClient) {
    actual suspend fun uploadAvatar(bytes: ByteArray, userId: String): String {
        val url = getAvatarUrl(userId)
        client.post(url) { setBody(bytes) }
        return "$url?alt=media"
    }

    actual suspend fun getAvatar(userId: String): ByteArray {
        return client.get(getAvatarUrl(userId)).readBytes()
    }

    actual suspend fun deleteAvatar(userId: String) {
    }

    private fun getAvatarUrl(userId: String): String {
        return "https://firebasestorage.googleapis.com/v0/b/${ApiKeys.firebaseProjectId}.appspot.com/o/avatars%2F$userId"
    }
}