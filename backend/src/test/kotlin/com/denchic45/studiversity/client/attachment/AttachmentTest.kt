package com.denchic45.studiversity.client.attachment

import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.util.assertResultIsOk
import com.denchic45.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.attachment.AttachmentApi
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.element.CourseElementsApi
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.work.CourseWorkApi
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.membership.MembershipApi
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.util.toUUID
import com.github.michaelbull.result.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject
import java.io.File
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AttachmentTest : KtorClientTest() {

    private val linkUrl =
        "https://developers.google.com/classroom/reference/rest/v1/courses.courseWork.studentSubmissions#StudentSubmission"

    private val file: File = File("data.txt").apply {
        writeText("Hello, Reader!")
    }

    private val teacher1Id = "02f00b3e-3a78-4431-87d4-34128ebbb04c".toUUID()

    private val teacherClient by lazy {
        createAuthenticatedClient("stefan@gmail.com", "FSg54g45dg")
    }

    private val membershipApi: MembershipApi by inject { parametersOf(client) }
    private val coursesApi: CoursesApi by inject { parametersOf(client) }
    private val courseElementsApi: CourseElementsApi by inject { parametersOf(client) }
    private val courseWorkApiOfTeacher: CourseWorkApi by inject { parametersOf(teacherClient) }
    private val attachmentApi: AttachmentApi by inject { parametersOf(client) }


    private lateinit var course: CourseResponse
    private lateinit var courseWork: CourseWorkResponse

    private suspend fun enrolTeacher(userId: UUID) {
        enrolUser(userId, Role.Teacher.id)
    }

    private suspend fun enrolUser(userId: UUID, roleId: Long) {
        membershipApi.joinToScopeManually(userId, course.id, listOf(roleId)).apply {
            assertResultIsOk(this)
        }
    }

    private suspend fun unrollUser(userId: UUID) {
        membershipApi.leaveFromScope(userId, course.id, "manual")
            .onSuccess { println("Success unroll user: $userId") }
            .onFailure { println("Failed unroll user: $userId. Status: ${it.code}. Body: ${it.error}") }
    }

    private suspend fun deleteAttachment(attachmentId: UUID) {
        courseWorkApiOfTeacher.deleteAttachmentFromWork(course.id, courseWork.id, attachmentId)
            .apply {
                Assertions.assertNotNull(get()) { unwrapError().toString() }
            }
    }

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
    }

    @Test
    fun testAddRemoveAttachment(): Unit = runBlocking {
        courseWorkApiOfTeacher.uploadFileToWork(course.id, courseWork.id, CreateFileRequest(file))
            .apply {
                Assertions.assertNotNull(get()) { unwrapError().toString() }
                Assertions.assertEquals("data.txt", unwrap().item.name)
            }


        val attachmentHeaders = courseWorkApiOfTeacher.getAttachments(course.id, courseWork.id).unwrapAsserted().apply {
            Assertions.assertEquals(1, size)
        }


        attachmentApi.getById(attachmentHeaders[0].id).unwrapAsserted().apply {
            assertEquals(file.name, (this as FileAttachmentResponse).name)
        }

        val linkAttachmentHeader = courseWorkApiOfTeacher.addLinkToWork(
            course.id,
            courseWork.id,
            CreateLinkRequest(linkUrl)
        ).unwrapAsserted().apply {
            Assertions.assertEquals(linkUrl, item.url)
        }

        attachmentApi.getById(linkAttachmentHeader.id).unwrapAsserted().apply {
            assertEquals(linkAttachmentHeader.item.url, (this as LinkAttachmentResponse).url)
        }

        val attachments =
            courseWorkApiOfTeacher.getAttachments(course.id, courseWork.id).unwrap().apply {
                Assertions.assertEquals(2, size)
                assertTrue(any { it is FileAttachmentHeader && it.item.name == "data.txt" })
                assertTrue(any { it is LinkAttachmentHeader && it.item.url == linkUrl })
            }

        deleteAttachment(attachments[0].id)

        courseWorkApiOfTeacher.getAttachments(course.id, courseWork.id).unwrap().apply {
            Assertions.assertEquals(1, size)
        }
    }
}