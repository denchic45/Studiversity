package com.denchic45.kts.data.storage.remote

actual class UserRemoteStorage {
    actual suspend fun uploadAvatar(bytes: ByteArray, userId: String): String {
        TODO("Not yet implemented")
    }

    actual suspend fun deleteAvatar(userId: String) {
    }
}