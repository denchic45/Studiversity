package com.studiversity.client.room

import com.github.michaelbull.result.unwrap
import com.studiversity.KtorClientTest
import com.studiversity.util.assertResultIsOk
import com.stuiversity.api.room.RoomApi
import com.stuiversity.api.room.model.CreateRoomRequest
import com.stuiversity.api.room.model.UpdateRoomRequest
import com.stuiversity.util.OptionalProperty
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

        roomApi.update(createdRoom.id, UpdateRoomRequest(OptionalProperty.Present("Workshop"))).also(::assertResultIsOk).unwrap().apply {
            assertEquals("Workshop", name)
        }

        roomApi.delete(createdRoom.id).also(::assertResultIsOk)
    }
}