package com.denchic45.kts.data.db.remote.source

import com.denchic45.firebasemultiplatform.api.*
import com.denchic45.kts.ApiKeys
import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.di.FirebaseHttpClient
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.parseDocument
import com.denchic45.kts.util.parseDocuments
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
actual class UserRemoteDataSource @Inject constructor(private val client: FirebaseHttpClient) {

    actual suspend fun findById(id: String): UserMap {
        return parseDocument(Json.parseToJsonElement(client.get {
            url("https://firestore.googleapis.com/v1/projects/kts-app-2ab1f/databases/(default)/documents/Users/$id")
        }.bodyAsText()), ::UserMap)
    }

    actual fun observeById(id: String): Flow<UserMap?> = flow {
        emit(
            client.get {
                url("https://firestore.googleapis.com/v1/projects/kts-app-2ab1f/databases/(default)/documents/Users/$id")
            }.run {
                if (status != HttpStatusCode.OK) null
                else parseDocument(Json.parseToJsonElement(bodyAsText()), ::UserMap)
            }
        )
    }

    actual fun findByContainsName(text: String): Flow<List<UserMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun findByEmail(email: String): UserMap {
        return client.post {
            url("https://firestore.googleapis.com/v1/projects/${ApiKeys.firebaseProjectId}/databases/(default)/documents:runQuery")
            contentType(ContentType.Application.Json)
            setBody(Request(
                structuredQuery = StructuredQuery(
                    from = CollectionSelector("Users"),
                    where = Filter(fieldFilter = FieldFilter(
                        field = FieldReference("email"),
                        op = FieldFilter.Operator.EQUAL,
                        value = Value(stringValue = email))
                    )
                )
            ))
        }.let {
            UserMap(parseDocuments(Json.parseToJsonElement(it.bodyAsText()))[0])
        }
    }

    actual fun findTeachersByContainsName(text: String): Flow<List<UserMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun addTeacher(map: FireMap) {

    }

    actual suspend fun updateTeacher(teacherMap: FireMap) {

    }

    actual suspend fun removeTeacher(teacher: FireMap) {

    }

    actual suspend fun addStudent(studentMap: UserMap) {
    }

    actual suspend fun updateStudent(
        oldStudentMap: UserMap,
        studentMap: UserMap,
    ) {
    }

    actual suspend fun removeStudent(studentId: String, groupId: String) {
    }

}