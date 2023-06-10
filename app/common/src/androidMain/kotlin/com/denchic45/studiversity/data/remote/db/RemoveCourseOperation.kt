package com.denchic45.studiversity.data.remote.db

import com.denchic45.studiversity.data.db.remote.source.CourseRemoteDataSource
import com.denchic45.studiversity.data.repository.PreconditionsRepository
import com.denchic45.studiversity.util.deleteCollection
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.tasks.await

interface RemoveCourseOperation : PreconditionsRepository {
    val courseRemoteDataSource: CourseRemoteDataSource

    suspend fun removeCourse(courseId: String, groupIds: List<String>) {
        requireAllowWriteData()
        courseRemoteDataSource.removeCourse(courseId, groupIds)
    }
}