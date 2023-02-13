package com.denchic45.kts.data.db.remote.source

import com.denchic45.firebasemultiplatform.api.*
import com.denchic45.firebasemultiplatform.ktor.PathReference
import com.denchic45.firebasemultiplatform.ktor.commit
import com.denchic45.firebasemultiplatform.ktor.runQuery
import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.di.FirebaseHttpClient
import com.denchic45.kts.util.FireMap
import com.denchic45.kts.util.parseDocument
import com.denchic45.kts.util.parseDocuments
import com.denchic45.kts.util.toMapValues
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
        return client.runQuery(
            Request(
                structuredQuery = StructuredQuery(
                    from = CollectionSelector("Users"),
                    where = Filter(
                        fieldFilter = FieldFilter(
                            field = FieldReference("email"),
                            op = FieldFilter.Operator.EQUAL,
                            value = Value(stringValue = email)
                        )
                    )
                )
            )
        ).let {
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

    actual suspend fun updateStudent(oldStudentMap: UserMap, studentMap: UserMap) {
        val writes = buildList {

            val studentField = "students.`${studentMap.id}`"

            if (changePersonalData(studentMap, oldStudentMap)) {
                add(
                    Write(
                        updateMask = null,
                        update = DocumentRequest(
                            name = PathReference()
                                .collection("Users")
                                .document(studentMap.id).path,
                            fields = studentMap.toMapValues()
                        )
                    )
                )
            }

            if (changeGroup(studentMap, oldStudentMap)) {
                add(
                    Write(
                        updateMask = DocumentMask(listOf(studentField, "timestamp")),
                        updateTransforms = listOf(
                            FieldTransform(
                                fieldPath = "timestamp",
                                setToServerValue = ServerValue.REQUEST_TIME
                            )
                        ),
                        update = DocumentRequest(
                            name = PathReference().collection("Groups")
                                .document(oldStudentMap.groupId!!).path,
                            fields = emptyMap()
                        )
                    )
                )
            }

            add(
                Write(
                    updateMask = DocumentMask(
                        listOf(
                            "timestamp",
                            studentField
                        )
                    ),
                    update = DocumentRequest(
                        name = PathReference().collection("Groups")
                            .document(studentMap.groupId!!).path,
                        fields = mapOf(
                            "students" to Value(
                                mapValue = MapValue(
                                    mapOf(
                                        studentMap.id to Value(
                                            mapValue = MapValue(studentMap.toMapValues())
                                        )
                                    )
                                )
                            ),
                        )
                    ),
                    updateTransforms = listOf(
                        FieldTransform(
                            fieldPath = "timestamp",
                            setToServerValue = ServerValue.REQUEST_TIME,
                        )
                    )
                )
            )
        }
        client.commit(Commit(writes)).bodyAsText().apply { println(this) }
    }

    private fun changePersonalData(student: UserMap, cacheStudent: UserMap): Boolean {
        return student.fullName != cacheStudent.fullName || student.gender != cacheStudent.gender ||
                student.firstName != cacheStudent.firstName ||
                student.email != cacheStudent.email ||
                student.photoUrl != cacheStudent.photoUrl || student.admin != cacheStudent.admin ||
                student.role != cacheStudent.role
    }

    private fun changeGroup(student: UserMap, oldStudent: UserMap): Boolean {
        return student.groupId != oldStudent.groupId
    }

    actual suspend fun removeStudent(studentId: String, groupId: String) {
    }

}