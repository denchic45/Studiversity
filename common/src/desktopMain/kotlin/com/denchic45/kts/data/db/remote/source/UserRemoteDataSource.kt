package com.denchic45.kts.data.db.remote.source

import com.denchic45.firebasemultiplatform.api.*
import com.denchic45.kts.ApiKeys
import com.denchic45.kts.data.db.remote.model.UserMap
import com.denchic45.kts.di.FirebaseHttpClient
import com.denchic45.kts.util.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json
import java.util.*
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
            setBody(
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
            )
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
        client.post {
            url("https://firestore.googleapis.com/v1/projects/kts-app-2ab1f/databases/(default)/documents:commit")

            val writes = buildList {

                val replace = "students.`${studentMap["id"]}`"

                if (changePersonalData(studentMap, oldStudentMap)) {
                    add(
                        Write(
                            updateMask = null,
                            update = DocumentRequest(
                                name = "projects/kts-app-2ab1f/databases/(default)/documents/Users/${studentMap.id}",
                                fields = studentMap.toMapValues()
                            )
                        )
                    )
                }

                if (changeGroup(studentMap, oldStudentMap)) {
                    add(
                        Write(
                            updateMask = DocumentMask(
                                listOf(
                                    replace,
                                    "timestamp"
                                )
                            ),
                            updateTransforms = listOf(
                                FieldTransform(
                                    fieldPath = "timestamp",
                                    setToServerValue = ServerValue.REQUEST_TIME
                                )
                            ),
                            update = DocumentRequest(
                                name = "projects/kts-app-2ab1f/databases/(default)/documents/Group/${oldStudentMap.groupId}",
                                fields = emptyMap()
                            )
                        )
                    )
                }

                println("STRING!!! $replace")
                add(
                    Write(
                        updateMask = DocumentMask(
                            listOf(
                                replace,
                                "timestamp",
                                "abc"
                            )
                        ),
                        update = DocumentRequest(
                            name = "projects/kts-app-2ab1f/databases/(default)/documents/Group/${studentMap.groupId}",
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
                                "timestamp" to Value(timestampValue = Date().toTimestampValue()),
                                "abc" to Value(stringValue = "123")
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
            contentType(ContentType.Application.Json)
            setBody(Commit(writes))
        }.bodyAsText().apply { println(this) }

//        firestore.batch().apply {
//            if (changePersonalData(studentMap, oldStudentMap)) {
//                this[usersRef.document(studentMap.id)] = studentMap.map
//            }
//        if (changeGroup(studentMap, oldStudentMap)) {
//            deleteStudentFromGroup(studentMap.id, oldStudentMap.groupId!!, this)
//        }
//        updateStudentFromGroup(studentMap.map, studentMap.groupId!!, this)
//        commit().await()
//        }
    }

//    private fun updateStudentFromGroup(studentMap: FireMap, groupId: String, batch: WriteBatch) {
//        val updateGroupMap: MutableMap<String, Any> = HashMap()
//        updateGroupMap["students." + studentMap["id"]] = studentMap
//        updateGroupMap["timestamp"] = FieldValue.serverTimestamp()
//        batch.update(groupsRef.document(groupId), updateGroupMap)
//    }

//    private fun deleteStudentFromGroup(studentId: String, groupId: String, batch: WriteBatch) {
//        val updateGroupMap: MutableMap<String, Any> = HashMap()
//        updateGroupMap["students.$studentId"] = FieldValue.delete()
//        updateGroupMap["timestamp"] = FieldValue.serverTimestamp()
//        batch.update(groupsRef.document(groupId), updateGroupMap)
//    }

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