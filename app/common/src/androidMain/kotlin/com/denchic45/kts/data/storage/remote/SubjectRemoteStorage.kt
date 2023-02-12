package com.denchic45.kts.data.storage.remote

import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

actual class SubjectRemoteStorage @Inject constructor(
    private val storage: FirebaseStorage
) {
   actual suspend fun findAllRefsOfSubjectIcons(): List<String> {
        return storage.getReference("subjects")
            .listAll()
            .await()
            .run { items.map { it.downloadUrl.await().toString() } }
    }
}