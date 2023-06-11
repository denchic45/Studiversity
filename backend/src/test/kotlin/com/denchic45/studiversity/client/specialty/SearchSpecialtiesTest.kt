package com.denchic45.studiversity.client.specialty

import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.util.assertResultIsOk
import com.denchic45.studiversity.util.assertedResultIsOk
import com.denchic45.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.specialty.SpecialtyApi
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.github.michaelbull.result.unwrap
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.test.assertEquals

class SearchSpecialtiesTest : KtorClientTest() {
    private val specialtyApi: SpecialtyApi by inject { parametersOf(client) }

    private lateinit var specialty: SpecialtyResponse

    private val expectedName = "Программирование в компьютерных системах"
    private val expectedShortname = "ПКС"

    @BeforeEach
    fun init(): Unit = runBlocking {
        specialty = specialtyApi.create(CreateSpecialtyRequest(expectedName, expectedShortname))
            .unwrapAsserted()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        specialtyApi.delete(specialty.id).assertedResultIsOk()
    }

    @Test
    fun test(): Unit = runBlocking {
        specialtyApi.search("Программир").also(::assertResultIsOk).unwrap().single().assertSpecialtyEquals()

        specialtyApi.search("пк").also(::assertResultIsOk).unwrap().single().assertSpecialtyEquals()

        specialtyApi.search("Комп").also(::assertResultIsOk).unwrap().single().assertSpecialtyEquals()

        specialtyApi.search("Комп").also(::assertResultIsOk).unwrap().single().assertSpecialtyEquals()
    }

    private fun SpecialtyResponse.assertSpecialtyEquals() {
        assertEquals(expectedName, name)
        assertEquals(expectedShortname, shortname)
    }
}