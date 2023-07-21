package com.denchic45.studiversity.client.course

import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.util.assertResultIsOk
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
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.api.membership.MembershipApi
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.submission.SubmissionsApi
import com.denchic45.stuiversity.util.toUUID
import com.github.michaelbull.result.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject
import java.io.File
import java.util.*


class SubmissionsTest : KtorClientTest() {

    private val student1Id = "7a98cdcf-d404-4556-96bd-4ce9137c8cbe".toUUID()
    private val student2Id = "77129e28-bf01-4dca-b19f-9fbcf576345e".toUUID()
    private val teacher1Id = "02f00b3e-3a78-4431-87d4-34128ebbb04c".toUUID()

    private val linkUrl =
        "https://developers.google.com/classroom/reference/rest/v1/courses.courseWork.studentSubmissions#StudentSubmission"

    private val fileRequest = CreateFileRequest(File("data.txt").apply {
        writeText("Hello, Reader!")
    })

    private lateinit var course: CourseResponse
    private lateinit var courseWork: CourseWorkResponse

    private val studentClient by lazy { createAuthenticatedClient("slavik@gmail.com", "GHBO043g54gh") }
    private val teacherClient by lazy { createAuthenticatedClient("stefan@gmail.com", "FSg54g45dg") }

    private val submissionsApiOfStudent: SubmissionsApi by inject { parametersOf(studentClient) }
    private val submissionsApiOfTeacher: SubmissionsApi by inject { parametersOf(teacherClient) }
    private val coursesApi: CoursesApi by inject { parametersOf(client) }
    private val attachmentApi: AttachmentApi by inject { parametersOf(client) }
    private val courseElementsApi: CourseElementsApi by inject { parametersOf(client) }
    private val courseWorkApi: CourseWorkApi by inject { parametersOf(client) }
    private val membershipApi: MembershipApi by inject { parametersOf(client) }


    override fun setup(): Unit = runBlocking {
        course = coursesApi.create(CreateCourseRequest("Test course for submissions")).apply {
            assertNotNull(get()) { unwrapError().error.toString() }
        }.unwrap()
        enrolTeacher(teacher1Id)
    }

    override fun cleanup(): Unit = runBlocking {
        coursesApi.setArchive(course.id)
        coursesApi.delete(course.id)
    }

    @BeforeEach
    fun init(): Unit = runBlocking {
        courseWork = courseWorkApi.create(
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
        ).unwrap()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        // delete course element
        courseElementsApi.delete(course.id, courseWork.id).apply { assertNotNull(get()) { unwrapError().toString() } }
        // unroll users
        unrollUser(student1Id)
        unrollUser(student2Id)
    }

    private suspend fun enrolStudentsToCourse() {
        enrolStudent(student1Id)
        enrolStudent(student2Id)
    }

    private suspend fun enrolStudent(userId: UUID) {
        enrolUser(userId, Role.Student.id)
    }

    private suspend fun enrolTeacher(userId: UUID) {
        enrolUser(userId, Role.Teacher.id)
    }

    private suspend fun enrolUser(userId: UUID, roleId: Long) {
        membershipApi.joinToScopeManually(userId, course.id, listOf(roleId)).also(::assertResultIsOk)
    }

    private suspend fun unrollUser(userId: UUID) {
        membershipApi.leaveFromScope(userId, course.id, "manual")
            .onSuccess { println("Success unroll user: $userId") }
            .onFailure { println("Failed unroll user: $userId. Status: ${it.code}. Body: ${it.error}") }
    }

    @Test
    fun testAddSubmissions(): Unit = runBlocking {
        enrolStudentsToCourse()
        submissionsApiOfStudent.getAllByCourseWorkId(course.id, courseWork.id).onSuccess { response ->
            assertEquals(2, response.size)
            assertAllStatesIsNew(response)
        }
    }

    @Test
    fun testUpdateStatusToCreatedAfterGettingSubmissionByStudent(): Unit = runBlocking {
        enrolStudentsToCourse()
        val submissions = submissionsApiOfTeacher.getAllByCourseWorkId(course.id, courseWork.id)
            .also(::assertResultIsOk)
            .unwrap()
            .also { response ->
                assertEquals(2, response.size)
                assertAllStatesIsNew(response)
            }
        val ownSubmission = submissions.first { it.author.id == student1Id }

        // get submission by another user (maybe teacher)
        submissionsApiOfTeacher.getById(course.id, courseWork.id, ownSubmission.id).apply {
            assertNotNull(get()) { unwrapError().toString() }
            assertEquals(SubmissionState.NEW, unwrap().state)
        }
        // twice get submission by another user (maybe teacher)
        submissionsApiOfTeacher.getById(course.id, courseWork.id, ownSubmission.id).apply {
            assertNotNull(get()) { unwrapError().toString() }
            assertEquals(SubmissionState.NEW, unwrap().state)
        }

        // get submission by owner student
        submissionsApiOfStudent.getById(course.id, courseWork.id, ownSubmission.id).apply {
            assertNotNull(get()) { unwrapError().toString() }
            assertAllStatesInCreated(unwrap())
        }

        // get submission by another user again
        submissionsApiOfTeacher.getById(course.id, courseWork.id, ownSubmission.id).apply {
            assertNotNull(get()) { unwrapError().toString() }
            assertAllStatesInCreated(unwrap())
        }
    }

    @Test
    fun testGetSubmissionsAfterAddNewStudentToCourse(): Unit = runBlocking {
        enrolStudent(student2Id)
        submissionsApiOfTeacher.getAllByCourseWorkId(course.id, courseWork.id)
            .also(::assertResultIsOk)
            .unwrap().also { response ->
                assertEquals(1, response.size)
            }
        enrolStudent(student1Id)
        val submissions = submissionsApiOfTeacher.getAllByCourseWorkId(course.id, courseWork.id)
            .unwrap().also { response ->
                assertEquals(2, response.size)
                assertAllStatesIsNew(response)
            }
        val ownSubmission = submissions.first { it.author.id == student1Id }
        // get submission by owner student
        submissionsApiOfStudent.getById(course.id, courseWork.id, ownSubmission.id)
            .apply {
                assertNotNull(get()) { unwrapError().toString() }
                assertAllStatesInCreated(unwrap())
            }
    }

    @Test
    fun testOnStudentFirstGetSubmissionByStudentId(): Unit = runBlocking {
        enrolStudent(student1Id)
        // get submission by student
        submissionsApiOfStudent.getByStudent(course.id, courseWork.id, student1Id).unwrap().also { response ->
            assertEquals(SubmissionState.CREATED, response.state)
        }
    }

    @Test
    fun testOnTeacherFirstGetSubmissionByStudentId(): Unit = runBlocking {
        enrolStudent(student1Id)
        // get submission by another user (maybe teacher)
        val submission = submissionsApiOfTeacher.getByStudent(course.id, courseWork.id, student1Id)
            .also(::assertResultIsOk)
            .unwrap().also { response ->
                assertEquals(SubmissionState.NEW, response.state)
            }
        submissionsApiOfStudent.getByStudent(course.id, courseWork.id, student1Id).unwrap().also { response ->
            assertEquals(SubmissionState.CREATED, response.state)
            assertEquals(submission.id, response.id)
        }
    }

    @Test
    fun testSubmitSubmission(): Unit = runBlocking {
        enrolStudent(student1Id)
        val submission = submissionsApiOfStudent.getByStudent(course.id, courseWork.id, student1Id).unwrap()

        submissionsApiOfTeacher.uploadFile(
            course.id,
            courseWork.id,
            submission.id,
            fileRequest
        ).apply { assertNotNull(getError()) { unwrap().toString() } }

        submissionsApiOfStudent.uploadFile(
            course.id,
            courseWork.id,
            submission.id,
            fileRequest
        )

        submissionsApiOfTeacher.submitSubmission(course.id, courseWork.id, submission.id).apply {
            assertEquals(HttpStatusCode.Forbidden.value, unwrapError().code)
        }
        submissionsApiOfStudent.submitSubmission(course.id, courseWork.id, submission.id).apply {
            assertNotNull(get()) { unwrapError().toString() }
            assertEquals(SubmissionState.SUBMITTED, unwrap().state)
        }
    }

    @Test
    fun testGradeSubmission(): Unit = runBlocking {
        enrolStudent(student1Id)
        val submission = submissionsApiOfStudent.getByStudent(course.id, courseWork.id, student1Id).unwrap()

        submissionsApiOfStudent.addLink(
            course.id,
            courseWork.id,
            submission.id,
            CreateLinkRequest("https://developers.google.com/classroom/reference/rest/v1/courses.courseWork.studentSubmissions#StudentSubmission")
        ).apply { assertNotNull(get()) { unwrapError().error.toString() } }

        submissionsApiOfStudent.gradeSubmission(course.id, courseWork.id, submission.id, 5).apply {
            assertEquals(HttpStatusCode.Forbidden.value, unwrapError().code)
        }

        submissionsApiOfTeacher.gradeSubmission(course.id, courseWork.id, submission.id, 6).apply {
            assertEquals(HttpStatusCode.BadRequest.value, unwrapError().code)
        }

        submissionsApiOfTeacher.gradeSubmission(course.id, courseWork.id, submission.id, 5).apply {
            val gradedSubmission = get()
            assertNotNull(gradedSubmission) { getError().toString() }
            gradedSubmission?.apply {
                grade!!.apply {
                    assertEquals(5, value)
                    assertEquals(teacher1Id, gradedBy)
                }
            }
        }
    }

    @Test
    fun testAddRemoveAttachment(): Unit = runBlocking {
        enrolStudent(student1Id)
        val submission = submissionsApiOfStudent.getByStudent(course.id, courseWork.id, student1Id).unwrap()
        submissionsApiOfStudent.uploadFile(course.id, courseWork.id, submission.id, fileRequest).apply {
            assertNotNull(get(), getError().toString())
        }

        submissionsApiOfStudent.getAttachments(
            courseId = course.id,
            courseWorkId = courseWork.id,
            submissionId = submission.id
        ).unwrap().apply {
            assertEquals(1, size)
        }

        submissionsApiOfStudent.addLink(
            course.id,
            courseWork.id,
            submission.id,
            CreateLinkRequest(linkUrl)
        ).apply {
            assertNotNull(get()) { unwrapError().error.toString() }
            assertEquals(
                linkUrl,
                unwrap().item.url
            )
        }

        val attachments = submissionsApiOfStudent.getAttachments(
            courseId = course.id,
            courseWorkId = courseWork.id,
            submissionId = submission.id
        )
            .unwrap().apply {
                assertEquals(2, size)
                kotlin.test.assertTrue(any { it is FileAttachmentHeader && it.item.name == "data.txt" })
                kotlin.test.assertTrue(any { it is LinkAttachmentHeader && it.item.url == linkUrl })
            }

        submissionsApiOfStudent.deleteAttachment(course.id, courseWork.id, submission.id, attachments[0].id)
            .apply { assertNotNull(get()) { unwrapError().toString() } }

        submissionsApiOfStudent.getAttachments(
            courseId = course.id,
            courseWorkId = courseWork.id,
            submissionId = submission.id
        ).unwrap().apply {
            assertEquals(1, size)
        }
        submissionsApiOfStudent.deleteAttachment(course.id, courseWork.id, submission.id, attachments[1].id)
            .apply { assertNotNull(get()) { unwrapError().toString() } }

        submissionsApiOfStudent.getAttachments(
            courseId = course.id,
            courseWorkId = courseWork.id,
            submissionId = submission.id
        ).unwrap().apply {
            assertTrue { isEmpty() }
        }
    }

    @Test
    fun testDownloadAttachments(): Unit = runBlocking {
        enrolStudent(student1Id)
        val submission = submissionsApiOfStudent.getByStudent(course.id, courseWork.id, student1Id).unwrap()
        val submissionId = submission.id

        val fileAttachment =
            submissionsApiOfStudent.uploadFile(course.id, courseWork.id, submissionId, fileRequest).apply {
                assertNotNull(get(), getError().toString())
                assertEquals("data.txt", unwrap().item.name)
            }.unwrap()

        attachmentApi.getById(
            attachmentId = fileAttachment.id
        ).apply {
            assertNotNull(get()) { unwrapError().error.toString() }
            val downloadedFile = unwrap() as FileAttachmentResponse
            assertEquals("data.txt", downloadedFile.name)
            assertEquals(fileRequest.bytes.decodeToString(), downloadedFile.bytes.decodeToString())
        }

        val linkAttachment = submissionsApiOfStudent.addLink(
            course.id,
            courseWork.id,
            submissionId,
            CreateLinkRequest(linkUrl)
        ).unwrap()

        attachmentApi.getById(
            attachmentId = linkAttachment.id
        ).apply {
            assertEquals(linkUrl, (unwrap() as LinkAttachmentResponse).url)
        }
    }

    private fun assertAllStatesInCreated(response: SubmissionResponse) {
        assertEquals(SubmissionState.CREATED, response.state)
    }

    private fun assertAllStatesIsNew(response: List<SubmissionResponse>) {
        assertTrue(response.all { it.state == SubmissionState.NEW })
    }
}