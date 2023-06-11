package com.denchic45.studiversity.client.room

import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.util.assertResultIsOk
import com.denchic45.stuiversity.api.room.RoomApi
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import com.denchic45.stuiversity.util.OptionalProperty
import com.github.michaelbull.result.unwrap
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject
import kotlin.test.assertEquals

class RoomTest : KtorClientTest() {

    private val roomApi: RoomApi by inject { parametersOf(client) }

    @Test
    fun test(): Unit = runBlocking {
        val createdRoom = roomApi.create(CreateRoomRequest("Room №20")).also(::assertResultIsOk).unwrap().apply {
            assertEquals("Room №20", name)
        }

        roomApi.getById(createdRoom.id).also(::assertResultIsOk).unwrap().apply {
            assertEquals(createdRoom.id, id)
            assertEquals("Room №20", name)
        }

        roomApi.update(createdRoom.id, UpdateRoomRequest(OptionalProperty.Present("Workshop"))).also(::assertResultIsOk)
            .unwrap().apply {
                assertEquals("Workshop", name)
            }

        roomApi.delete(createdRoom.id).also(::assertResultIsOk)
    }
}