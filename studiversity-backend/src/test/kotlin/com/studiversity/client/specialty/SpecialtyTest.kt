package com.studiversity.client.specialty

import com.studiversity.KtorClientTest
import com.studiversity.util.assertResultIsOk
import com.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.specialty.SpecialtyApi
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import com.denchic45.stuiversity.util.optPropertyOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject
import kotlin.test.assertEquals

class SpecialtyTest : KtorClientTest() {

    private val specialtyApi: SpecialtyApi by inject { parametersOf(client) }

    @Test
    fun test(): Unit = runBlocking {
        val createdSpecialty = specialtyApi.create(CreateSpecialtyRequest("Программирование в компьютерных системах", "ПКС"))
            .unwrapAsserted().apply {
                assertEquals("Программирование в компьютерных системах", name)
                assertEquals("ПКС", shortname)
            }

        specialtyApi.getById(createdSpecialty.id).unwrapAsserted().apply {
            assertEquals(createdSpecialty.id, id)
            assertEquals("Программирование в компьютерных системах", name)
            assertEquals("ПКС", shortname)
        }

        specialtyApi.update(createdSpecialty.id, UpdateSpecialtyRequest(optPropertyOf("Реклама"), optPropertyOf("Р")))
            .unwrapAsserted().apply {
                assertEquals("Реклама", name)
                assertEquals("Р", shortname)
            }

        specialtyApi.delete(createdSpecialty.id).also(::assertResultIsOk)
    }
}