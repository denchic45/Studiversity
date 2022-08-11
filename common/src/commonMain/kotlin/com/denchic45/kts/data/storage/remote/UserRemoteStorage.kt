package com.denchic45.kts.data.storage.remote

expect class UserRemoteStorage {

    suspend  fun uploadAvatar(bytes: ByteArray, userId: String):String

    suspend fun deleteAvatar(userId: String)
}