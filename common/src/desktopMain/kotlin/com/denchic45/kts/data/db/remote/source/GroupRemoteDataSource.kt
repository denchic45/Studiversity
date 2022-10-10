package com.denchic45.kts.data.db.remote.source

import com.denchic45.firebasemultiplatform.api.*
import com.denchic45.kts.ApiKeys
import com.denchic45.kts.data.db.remote.model.GroupMap
import com.denchic45.kts.di.FirebaseHttpClient
import com.denchic45.kts.util.MutableFireMap
import com.denchic45.kts.util.parseDocument
import com.denchic45.kts.util.parseDocuments
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

@me.tatarka.inject.annotations.Inject
actual class GroupRemoteDataSource(private val client: FirebaseHttpClient) {

    actual fun observeById(id: String): Flow<GroupMap?> = flow {
        emit(
            client.get("https://firestore.googleapis.com/v1/projects/kts-app-2ab1f/databases/(default)/documents/Groups/$id")
                .run {
                    if (status != HttpStatusCode.OK) null
                    else parseDocument(Json.parseToJsonElement(bodyAsText()), ::GroupMap)
                }
        )
    }

    actual fun findByContainsName(text: String): Flow<List<GroupMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun findById(id: String): GroupMap {
        return parseDocument(
            Json.parseToJsonElement(
                client.get("https://firestore.googleapis.com/v1/projects/kts-app-2ab1f/databases/(default)/documents/Groups/$id")
                    .bodyAsText()
            ), ::GroupMap)
    }

    actual suspend fun findCoursesByGroupId(groupId: String): List<String> {
        TODO("Not yet implemented")
    }

    actual suspend fun removeGroupAndRemoveStudentsAndRemoveGroupIdInCourses(
        groupId: String,
        studentIds: Set<String>,
        groupCourseIds: List<String>,
    ) {
    }

    actual suspend fun updateGroupsOfCourse(groupIds: List<String>) {
    }

    actual suspend fun findByIdIn(groupIds: List<String>): List<GroupMap>? {
        TODO("Not yet implemented")
    }

    actual suspend fun setHeadman(studentId: String, groupId: String) {
        client.patch("https://firestore.googleapis.com/v1/projects/kts-app-2ab1f/databases/(default)/documents/Groups/$groupId") {
            parameter("updateMask.fieldPaths", "headmanId")
            contentType(ContentType.Application.Json)
            setBody(DocumentRequest(fields = mapOf("headmanId" to Value(stringValue = studentId))))
        }.bodyAsText().apply {
            println(this)
        }
    }

    actual suspend fun removeHeadman(groupId: String) {
        client.patch("https://firestore.googleapis.com/v1/projects/kts-app-2ab1f/databases/(default)/documents/Groups/$groupId") {
            parameter("updateMask.fieldPaths", "headmanId")
            contentType(ContentType.Application.Json)
            setBody(DocumentRequest(fields = mapOf("headmanId" to Value(nullValue = null))))
        }.bodyAsText().apply {
            println("remove headman $this")
        }
    }

    actual suspend fun findBySpecialtyId(specialtyId: String): List<GroupMap> {
        TODO("Not yet implemented")
    }

    actual fun findByTeacherIdAndTimestamp(
        teacherId: String,
        timestampGroups: Long,
    ): Flow<List<GroupMap>> {
        TODO("Not yet implemented")
    }

    actual suspend fun findByCuratorId(id: String): GroupMap {
        return client.post {
            url("https://firestore.googleapis.com/v1/projects/${ApiKeys.firebaseProjectId}/databases/(default)/documents:runQuery")
            setBody(Request(structuredQuery = StructuredQuery(
                from = CollectionSelector("Groups"),
                where = Filter(fieldFilter = FieldFilter(
                    field = FieldReference("curator.id"),
                    op = FieldFilter.Operator.EQUAL,
                    value = Value(stringValue = id)
                ))
            )))
        }.let {
            GroupMap(parseDocuments(Json.parseToJsonElement(it.bodyAsText()))[0])
        }
    }

    actual fun observeByCuratorId(id: String): Flow<GroupMap?> = flow {
        emit(
            client.post {
                url("https://firestore.googleapis.com/v1/projects/${ApiKeys.firebaseProjectId}/databases/(default)/documents:runQuery")
                setBody(Request(structuredQuery = StructuredQuery(
                    from = CollectionSelector("Groups"),
                    where = Filter(fieldFilter = FieldFilter(
                        field = FieldReference("curator.id"),
                        op = FieldFilter.Operator.EQUAL,
                        value = Value(stringValue = id)
                    ))
                )))
            }.let {
                GroupMap(parseDocuments(Json.parseToJsonElement(it.bodyAsText()))[0])
            }
        )
    }

    actual suspend fun findByCourse(course: Int): List<GroupMap> {
        TODO("Not yet implemented")
    }

    actual suspend fun add(groupMap: MutableFireMap) {
        TODO("Not yet implemented")
    }

    actual suspend fun update(groupMap: MutableFireMap) {
        TODO("Not yet implemented")
    }

    actual suspend fun updateGroupCurator(groupId: String, teacherMap: MutableFireMap) {
        TODO("Not yet implemented")
    }
}