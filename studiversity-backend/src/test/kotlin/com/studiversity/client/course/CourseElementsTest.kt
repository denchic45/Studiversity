package com.studiversity.client.course

import com.denchic45.stuiversity.api.common.SortOrder
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.element.CourseElementsApi
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.topic.CourseTopicApi
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.membership.MembershipApi
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.util.toUUID
import com.github.michaelbull.result.*
import com.studiversity.KtorClientTest
import com.studiversity.util.assertResultIsOk
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject
import java.io.File
import java.util.*
import kotlin.test.assertTrue

class CourseElementsTest : KtorClientTest() {

    private val student1Id = "7a98cdcf-d404-4556-96bd-4ce9137c8cbe".toUUID()
    private val student2Id = "77129e28-bf01-4dca-b19f-9fbcf576345e".toUUID()
    private val teacher1Id = "02f00b3e-3a78-4431-87d4-34128ebbb04c".toUUID()

    private val linkUrl =
        "https://developers.google.com/classroom/reference/rest/v1/courses.courseWork.studentSubmissions#StudentSubmission"

    private val file: File = File("data.txt").apply {
        writeText("Hello, Reader!")
    }

    private val teacherClient by lazy {
        createAuthenticatedClient(
            "stefan@gmail.com",
            "FSg54g45dg"
        )
    }
    private val studentClient by lazy {
        createAuthenticatedClient(
            "slavik@gmail.com",
            "GHBO043g54gh"
        )
    }

    private val coursesApi: CoursesApi by inject { parametersOf(client) }
    private val courseElementsApi: CourseElementsApi by inject { parametersOf(client) }
    private val CourseTopicApi: CourseTopicApi by inject { parametersOf(teacherClient) }
    private val courseWorkApi: CourseWorkApi by inject { parametersOf(teacherClient) }
    private val courseWorkApiOfTeacher: CourseWorkApi by inject { parametersOf(teacherClient) }
    private val courseWorkApiOfStudent: CourseWorkApi by inject { parametersOf(studentClient) }
    private val MembershipApi: MembershipApi by inject { parametersOf(client) }

    private lateinit var course: CourseResponse
    private lateinit var courseWork: CourseElementResponse

    override fun setup(): Unit = runBlocking {
        course = coursesApi.create(CreateCourseRequest("Test course"))
            .apply { assertResultIsOk(this) }.unwrap()
        enrolTeacher(teacher1Id)
    }

    override fun cleanup(): Unit = runBlocking {
        coursesApi.setArchive(course.id)
        coursesApi.delete(course.id)
    }

    @BeforeEach
    fun init(): Unit = runBlocking {
        courseWork = courseWorkApiOfTeacher.create(
            course.id,
            CreateCourseWorkRequest(
                name = "Test Assignment",
                description = "some desc",
                topicId = null,
                dueDate = null,
                dueTime = null,
                workType = CourseWorkType.ASSIGNMENT,
                maxGrade = 5
            )
        ).also(::assertResultIsOk).unwrap()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        // delete course element
        courseElementsApi.delete(course.id, courseWork.id).also(::assertResultIsOk)
        // unroll users
        unrollUser(student1Id)
        unrollUser(student2Id)
    }

    private suspend fun enrolStudent(userId: UUID) {
        enrolUser(userId, Role.Student.id)
    }

    private suspend fun enrolTeacher(userId: UUID) {
        enrolUser(userId, Role.Teacher.id)
    }

    private suspend fun enrolUser(userId: UUID, roleId: Long) {
        MembershipApi.joinToScopeManually(userId, course.id, listOf(roleId)).apply {
            assertResultIsOk(this)
        }
    }

    private suspend fun unrollUser(userId: UUID) {
        MembershipApi.leaveFromScope(userId, course.id, "manual")
            .onSuccess { println("Success unroll user: $userId") }
            .onFailure { println("Failed unroll user: $userId. Status: ${it.code}. Body: ${it.error}") }
    }

    @Test
    fun testGetElementsByCourse(): Unit = runBlocking {
        repeat(3) {
            courseWorkApi.create(
                course.id, CreateCourseWorkRequest(
                    name = "Assignment №$it",
                    description = null,
                    topicId = null,
                    workType = CourseWorkType.ASSIGNMENT,
                    maxGrade = 5
                )
            ).unwrap()
        }
        courseElementsApi.getByCourseId(course.id)
            .apply { assertResultIsOk(this) }
            .unwrap()
            .apply {
                forEachIndexed { index, response ->
                    assertEquals(index, response.order - 1)
                }
            }
    }

    @Test
    fun testGetElementsByCourseAndSortingByTopic(): Unit = runBlocking {
        val topic1 = CourseTopicApi.createTopic(course.id)
        repeat(3) {
            courseWorkApi.create(
                course.id, CreateCourseWorkRequest(
                    name = "Assignment №$it",
                    description = null,
                    topicId = topic1.id,
                    workType = CourseWorkType.ASSIGNMENT,
                    maxGrade = 5
                )
            ).unwrap()
        }
        val topic2 = CourseTopicApi.createTopic(course.id)
        repeat(3) {
            courseWorkApi.create(
                course.id, CreateCourseWorkRequest(
                    name = "Assignment №$it",
                    description = null,
                    topicId = topic2.id,
                    workType = CourseWorkType.ASSIGNMENT,
                    maxGrade = 5
                )
            ).unwrap()
        }

        // sort by topic asc
        courseElementsApi.getByCourseId(course.id, listOf(CourseElementsSorting.TopicId()))
            .apply { assertResultIsOk(this) }
            .unwrap()
            .map { it.topicId }
            .distinctBy { it }
            .apply { assertEquals(listOf(topic1.id, topic2.id), this) }

        // sort by topic desc
        courseElementsApi.getByCourseId(
            course.id,
            listOf(CourseElementsSorting.TopicId(SortOrder.DESC))
        )
            .apply { assertResultIsOk(this) }
            .unwrap()
            .map { it.topicId }
            .distinctBy { it }
            .apply { assertEquals(listOf(topic2.id, topic1.id), this) }
    }

    @Test
    fun testAddRemoveAttachment(): Unit = runBlocking {
        enrolStudent(student1Id)
        courseWorkApiOfTeacher.uploadFileToWork(course.id, courseWork.id, CreateFileRequest(file))
            .apply {
                assertNotNull(get()) { unwrapError().toString() }
                assertEquals("data.txt", unwrap().fileItem.name)
            }

        // Prevent upload attachment by student
        courseWorkApiOfStudent.uploadFileToWork(course.id, courseWork.id, CreateFileRequest(file))
            .apply {
                assertNotNull(getError()?.code == HttpStatusCode.Forbidden.value) { unwrap().toString() }
            }

        courseWorkApiOfTeacher.getAttachments(course.id, courseWork.id).unwrap().apply {
            assertEquals(1, size)
        }

        // Get attachments by student
        courseWorkApiOfStudent.getAttachments(course.id, courseWork.id)
            .apply { assertNotNull(get()) { unwrapError().error.toString() } }
            .unwrap().apply {
                assertEquals(1, size)
            }

        courseWorkApiOfTeacher.addLinkToWork(
            course.id,
            courseWork.id,
            CreateLinkRequest(linkUrl)
        ).apply {
            assertNotNull(get()) { unwrapError().error.toString() }
            assertEquals(
                linkUrl,
                unwrap().linkAttachmentResponse.url
            )
        }

        val attachments =
            courseWorkApiOfTeacher.getAttachments(course.id, courseWork.id).unwrap().apply {
                assertEquals(2, size)
                assertTrue(any { it is FileAttachmentHeader && it.fileItem.name == "data.txt" })
                assertTrue(any { it is LinkAttachmentHeader && it.linkAttachmentResponse.url == linkUrl })
            }

        deleteAttachment(attachments[0].id)

        courseWorkApiOfTeacher.getAttachments(course.id, courseWork.id).unwrap().apply {
            assertEquals(1, size)
        }
    }

    @Test
    fun testDownloadAttachments(): Unit = runBlocking {
        val fileAttachment = courseWorkApiOfTeacher.uploadFileToWork(
            course.id,
            courseWork.id,
            CreateFileRequest(file)
        ).apply {
            assertNotNull(get(), getError().toString())
            assertEquals("data.txt", unwrap().fileItem.name)
        }.unwrap()

        courseWorkApiOfTeacher.getAttachment(course.id, courseWork.id, fileAttachment.id).apply {
            val downloadedFile = unwrap() as FileAttachmentResponse
            assertEquals("data.txt", downloadedFile.name)
            assertEquals(file.readText(), downloadedFile.bytes.decodeToString())
        }

        val linkAttachment = courseWorkApiOfTeacher.addLinkToWork(
            course.id,
            courseWork.id,
            CreateLinkRequest(linkUrl)
        ).unwrap()

        courseWorkApiOfTeacher.getAttachment(course.id, courseWork.id, linkAttachment.id).apply {
            assertEquals(linkUrl, (unwrap() as LinkAttachmentResponse).url)
        }
    }

    private suspend fun deleteAttachment(attachmentId: UUID) {
        courseWorkApiOfTeacher.deleteAttachmentFromWork(course.id, courseWork.id, attachmentId)
            .apply {
                assertNotNull(get()) { unwrapError().toString() }
            }
    }

}