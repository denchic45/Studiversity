package com.studiversity.client.course

import com.studiversity.KtorClientTest
import com.stuiversity.api.studygroup.model.AcademicYear
import com.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.stuiversity.api.studygroup.model.StudyGroupResponse
import com.studiversity.util.toUUID
import com.stuiversity.api.course.model.CourseResponse
import com.stuiversity.api.course.model.CreateCourseRequest
import com.stuiversity.api.membership.model.ManualJoinMemberRequest
import com.stuiversity.api.membership.model.ScopeMember
import com.stuiversity.api.role.model.Role
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CourseWithStudyGroupMembershipTest : KtorClientTest() {
    private lateinit var studyGroup1: StudyGroupResponse
    private lateinit var studyGroup2: StudyGroupResponse
    private lateinit var course: CourseResponse

    private val user1Id = "7a98cdcf-d404-4556-96bd-4ce9137c8cbe".toUUID()
    private val user2Id = "77129e28-bf01-4dca-b19f-9fbcf576345e".toUUID()


    @BeforeEach
    fun initData(): Unit = runBlocking {
        studyGroup1 = client.post("/studygroups") {
            contentType(ContentType.Application.Json)
            setBody(CreateStudyGroupRequest("Test group 1", AcademicYear(2022, 2023)))
        }.body<StudyGroupResponse>().apply {
            assertEquals(name, "Test group 1")
        }

        studyGroup2 = client.post("/studygroups") {
            contentType(ContentType.Application.Json)
            setBody(CreateStudyGroupRequest("Test group 2", AcademicYear(2022, 2025)))
        }.body()

        course = client.post("/courses") {
            contentType(ContentType.Application.Json)
            setBody(CreateCourseRequest("Test course 1"))
        }.run {
            val body = body<CourseResponse>()
            assertEquals(body.name, "Test course 1")
            body
        }
    }

    @AfterEach
    fun clearData(): Unit = runBlocking {
        // delete data
        assertEquals(
            HttpStatusCode.NoContent,
            client.delete("/studygroups/${studyGroup1.id}").status
        )
        assertEquals(
            HttpStatusCode.NoContent,
            client.delete("/studygroups/${studyGroup2.id}").status
        )
        assertEquals(
            HttpStatusCode.OK,
            client.put("/courses/${course.id}/archived").status
        )
        assertEquals(
            HttpStatusCode.NoContent,
            client.delete("/courses/${course.id}").status
        )
    }

    @Test
    fun testMembersOnAttachDetachStudyGroupsToCourse(): Unit = runBlocking {
        attachGroupsToCourse(client, course, studyGroup1, studyGroup2)

        client.get("/courses/${course.id}/studygroups")
            .body<List<String>>().apply {
                assertEquals(listOf(studyGroup1.id, studyGroup2.id).sorted(), map(String::toUUID).sorted())
            }
        enrolStudentsToGroups()
        syncMembership(course.id)
        delay(10000)

        client.get("/scopes/${course.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(listOf(user1Id, user2Id).sorted(), map(ScopeMember::userId).sorted())
        }

        // delete first group and check members of course
        client.delete("/courses/${course.id}/studygroups/${studyGroup1.id}")

        syncMembership(course.id)
        delay(12000)

        client.get("/scopes/${course.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(listOf(user1Id), map(ScopeMember::userId))
        }

        // delete second group and check members of course
        client.delete("/courses/${course.id}/studygroups/${studyGroup2.id}")

        syncMembership(course.id)
        delay(12000)

        client.get("/scopes/${course.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(emptyList(), map(ScopeMember::userId))
        }
    }

    @Test
    fun testMembersOnEnrolUnrollFromGroupsInCourse(): Unit = runBlocking {

        val user1Id = "7a98cdcf-d404-4556-96bd-4ce9137c8cbe".toUUID()
        val user2Id = "77129e28-bf01-4dca-b19f-9fbcf576345e".toUUID()

        enrolStudentsToGroups()
        attachGroupsToCourse(client, course, studyGroup1, studyGroup2)
        syncMembership(course.id)
        // assert two attached study groups to course
        client.get("/courses/${course.id}/studygroups").body<List<String>>().apply {
            assertEquals(listOf(studyGroup1.id, studyGroup2.id).sorted(), map(String::toUUID).sorted())
        }
        // assert two users in course membership
        client.get("/scopes/${course.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(listOf(user1Id, user2Id).sorted(), map(ScopeMember::userId).sorted())
            assertTrue(all { it.roles.contains(Role.Student) })
        }

        // delete first user from first group
        client.delete("/scopes/${studyGroup1.id}/members/$user1Id") { parameter("action", "manual") }

        syncMembership(course.id)
        delay(12000)

        // assert only second member in first group
        client.get("/scopes/${studyGroup1.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(listOf(user2Id), map(ScopeMember::userId))
        }
        // assert two members of course
        client.get("/scopes/${course.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(listOf(user1Id, user2Id).sorted(), map(ScopeMember::userId).sorted())
        }

        // delete second user from first group
        client.delete("/scopes/${studyGroup1.id}/members/$user2Id") { parameter("action", "manual") }

        syncMembership(course.id)
        delay(10000)

        // assert zero members in first group
        client.get("/scopes/${studyGroup1.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(emptyList(), map(ScopeMember::userId))
        }
        // assert only first member of course
        client.get("/scopes/${course.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(listOf(user1Id), map(ScopeMember::userId))
        }

        // delete first user from second group
        client.delete("/scopes/${studyGroup2.id}/members/$user1Id")  { parameter("action", "manual") }
        syncMembership(course.id)
        delay(10000)

        // assert zero members of course
        client.get("/scopes/${course.id}/members").body<List<ScopeMember>>().apply {
            assertEquals(emptyList(), map(ScopeMember::userId))
        }
    }

    private suspend fun enrolStudentsToGroups(
    ) {
        client.post("/scopes/${studyGroup1.id}/members?action=manual") {
            contentType(ContentType.Application.Json)
            setBody(ManualJoinMemberRequest(user1Id, roleIds = listOf(3)))
        }.apply { assertEquals(HttpStatusCode.Created, status) }
        client.post("/scopes/${studyGroup2.id}/members?action=manual") {
            contentType(ContentType.Application.Json)
            setBody(ManualJoinMemberRequest(user1Id, roleIds = listOf(3)))
        }.apply { assertEquals(HttpStatusCode.Created, status) }
        client.post("/scopes/${studyGroup1.id}/members?action=manual") {
            contentType(ContentType.Application.Json)
            setBody(ManualJoinMemberRequest(user2Id, roleIds = listOf(3)))
        }.apply { assertEquals(HttpStatusCode.Created, status) }
        delay(10000)
    }

    private suspend fun syncMembership(courseId: UUID) {
        client.get("/scopes/${courseId}/memberships") {
            parameter("type", "by_group")
        }.run {
            if (status == HttpStatusCode.OK)
                client.post("/scopes/${courseId}/memberships/${bodyAsText()}/sync").apply {
                    assertEquals(HttpStatusCode.Accepted, status)
                }
        }
    }

    private suspend fun attachGroupsToCourse(
        client: HttpClient,
        course: CourseResponse,
        studyGroup1: StudyGroupResponse,
        studyGroup2: StudyGroupResponse
    ) {
        client.put("/courses/${course.id}/studygroups/${studyGroup1.id}")
        delay(14000)
        client.put("/courses/${course.id}/studygroups/${studyGroup2.id}")
        delay(10000)
    }
}