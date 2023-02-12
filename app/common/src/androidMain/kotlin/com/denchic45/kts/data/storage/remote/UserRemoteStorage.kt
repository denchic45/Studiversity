package com.denchic45.kts.data.storage.remote

import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

actual class UserRemoteStorage @Inject constructor(storage: FirebaseStorage) {

    private val avatarsRef: StorageReference = storage.reference.child("avatars")

    actual suspend fun uploadAvatar(bytes: ByteArray, userId: String): String {
        val reference = avatarsRef.child(userId)
        reference.putBytes(bytes).await()
        return reference.downloadUrl.await().toString()
    }

    actual suspend fun deleteAvatar(userId: String) {
        avatarsRef.child(userId).delete().await()
    }

    actual suspend fun getAvatar(userId: String): ByteArray {
        TODO("Not yet implemented")
    }
}