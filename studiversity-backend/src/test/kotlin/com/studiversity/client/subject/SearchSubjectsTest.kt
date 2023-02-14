package com.studiversity.client.subject

import com.github.michaelbull.result.unwrap
import com.studiversity.KtorClientTest
import com.studiversity.util.assertResultIsOk
import com.studiversity.util.assertedResultIsOk
import com.studiversity.util.unwrapAsserted
import com.stuiversity.api.course.subject.SubjectApi
import com.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.test.assertEquals

class SearchSubjectsTest : KtorClientTest() {
    private val subjectApi: SubjectApi by inject { parametersOf(client) }

    private lateinit var subject: SubjectResponse

    private val expectedName = "Теория алгоритмов"
    private val expectedShortname = "ТАЛГ"

    @BeforeEach
    fun init(): Unit = runBlocking {
        subject = subjectApi.create(CreateSubjectRequest(expectedName, expectedShortname, "algorithm")).unwrapAsserted()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        subjectApi.delete(subject.id).assertedResultIsOk()
    }

    @Test
    fun test(): Unit = runBlocking {
        subjectApi.search("теория").also(::assertResultIsOk).unwrap().single().assertSpecialtyEquals()

        subjectApi.search("ТАЛ").also(::assertResultIsOk).unwrap().single().assertSpecialtyEquals()

        subjectApi.search("алг").also(::assertResultIsOk).unwrap().single().assertSpecialtyEquals()
    }

    private fun SubjectResponse.assertSpecialtyEquals() {
        assertEquals(expectedName, name)
        assertEquals(expectedShortname, shortname)
    }
}