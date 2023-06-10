package com.studiversity.client.studygroup

import com.github.michaelbull.result.unwrap
import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.util.assertResultIsOk
import com.denchic45.studiversity.util.assertedResultIsOk
import com.denchic45.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.specialty.SpecialtyApi
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.studygroup.model.AcademicYear
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.test.assertEquals

class SearchStudyGroupsTest : KtorClientTest() {
    private val specialtyApi: SpecialtyApi by inject { parametersOf(client) }
    private val studyGroupApi: StudyGroupApi by inject { parametersOf(client) }

    private lateinit var specialty: SpecialtyResponse
    private lateinit var studyGroup: StudyGroupResponse

    private val expectedName = "ПКС-4.2"

    @BeforeEach
    fun init(): Unit = runBlocking {
        specialty = specialtyApi.create(CreateSpecialtyRequest("Программирование в компьютерных системах", "ПКС"))
            .unwrapAsserted()
        studyGroup =
            studyGroupApi.create(CreateStudyGroupRequest(expectedName, AcademicYear(2022, 2026), specialty.id, null))
                .unwrapAsserted()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        studyGroupApi.delete(studyGroup.id).assertedResultIsOk()
    }

    @Test
    fun test(): Unit = runBlocking {
        studyGroupApi.search("ПК").also(::assertResultIsOk).unwrap().single().assertStudyGroupsEquals()

        studyGroupApi.search("КС-4.").also(::assertResultIsOk).unwrap().single().assertStudyGroupsEquals()

        studyGroupApi.search("ограммирование").also(::assertResultIsOk).unwrap().single().assertStudyGroupsEquals()
    }

    private fun StudyGroupResponse.assertStudyGroupsEquals() {
        assertEquals(expectedName, name)
    }
}