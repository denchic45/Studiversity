package com.studiversity.client.course

import com.studiversity.KtorClientTest
import com.studiversity.util.assertedResultIsOk
import com.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.subject.SubjectApi
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.test.assertEquals

class SearchCoursesTest : KtorClientTest() {
    private val subjectApi: SubjectApi by inject { parametersOf(client) }
    private val courseApi: CoursesApi by inject { parametersOf(client) }

    private lateinit var subject: SubjectResponse
    private lateinit var course: CourseResponse

    private val expectedName = "Талг ПКС 4"

    @BeforeEach
    fun init(): Unit = runBlocking {
        subject = subjectApi.create(CreateSubjectRequest("Теория алгоритмов", "ТАЛГ", "algorithm")).unwrapAsserted()
        course = courseApi.create(CreateCourseRequest(expectedName, subject.id)).unwrapAsserted()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        courseApi.setArchive(course.id).assertedResultIsOk()
        courseApi.delete(course.id).assertedResultIsOk()
        subjectApi.delete(subject.id).assertedResultIsOk()
    }

    @Test
    fun test(): Unit = runBlocking {
        courseApi.search("тал").unwrapAsserted().single().assertCourseEquals()

        courseApi.search("пкС 4").unwrapAsserted().single().assertCourseEquals()

        courseApi.search("теор").unwrapAsserted().single().assertCourseEquals()

        courseApi.search("теория алг").unwrapAsserted().single().assertCourseEquals()
    }

    private fun CourseResponse.assertCourseEquals() {
        assertEquals(expectedName, name)
    }
}