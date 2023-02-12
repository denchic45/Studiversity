package com.denchic45.kts.data.storage.remote

expect class SubjectRemoteStorage {

    suspend fun findAllRefsOfSubjectIcons(): List<String>
}