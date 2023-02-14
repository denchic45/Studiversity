package com.studiversity.client.specialty

import com.github.michaelbull.result.unwrap
import com.studiversity.KtorClientTest
import com.studiversity.util.assertResultIsOk
import com.studiversity.util.assertedResultIsOk
import com.studiversity.util.unwrapAsserted
import com.stuiversity.api.specialty.SpecialtyApi
import com.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.stuiversity.api.specialty.model.SpecialtyResponse
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import kotlin.test.assertEquals

class SearchSpecialties : KtorClientTest() {
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
        assertEquals(name, expectedName)
        assertEquals(shortname, expectedShortname)
    }
}