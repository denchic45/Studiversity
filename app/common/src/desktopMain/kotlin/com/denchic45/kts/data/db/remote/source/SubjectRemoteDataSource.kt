package com.denchic45.kts.data.db.remote.source

import com.denchic45.firebasemultiplatform.ktor.getDocument
import com.denchic45.kts.data.db.remote.model.CourseMap
import com.denchic45.kts.data.db.remote.model.SubjectMap
import com.denchic45.kts.di.FirebaseHttpClient
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.parseDocument
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

@me.tatarka.inject.annotations.Inject
actual class SubjectRemoteDataSource(private val client: FirebaseHttpClient) {

    actual suspend fun add(subjectMap: SubjectMap) {

    }

    actual suspend fun isExistWithSameIconAndColor(subjectMap: SubjectMap) {
    }

    actual suspend fun update(subjectMap: SubjectMap) {
    }

    actual fun observeById(id: String): Flow<SubjectMap?> = flow {
        emit(
            client.getDocument { collection("Subjects").document(id) }.run {
                if (status != HttpStatusCode.OK) null
                else parseDocument(Json.parseToJsonElement(bodyAsText()), ::SubjectMap)
            }
        )
    }

    actual suspend fun findById(id: String): SubjectMap = parseDocument(
        Json.parseToJsonElement(
            client.getDocument { collection("Subjects").document(id) }.bodyAsText()
        ), ::SubjectMap
    )

    actual suspend fun remove(map: FireMap) {

    }

    actual suspend fun findByGroupId(groupId: String): List<CourseMap> {
        TODO("Not yet implemented")
    }

    actual fun findByContainsName(text: String): Flow<List<SubjectMap>> {
        TODO("Not yet implemented")
    }
}