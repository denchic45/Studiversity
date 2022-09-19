package com.denchic45.common

import com.denchic45.kts.data.network.model.BellSchedule
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class BellScheduleTest {

    private val json = Json { prettyPrint = true }

    @Test
    fun test() {
        println(
            json.encodeToString(BellSchedule(
                listOf(
                    "8:30" to "9:10",
                    "9:20" to "10:00",
                    "10:05" to "10:45",
                    "10:50" to "11:30",
                    "11:35" to "12:15",
                    "12:20" to "13:00",
                    "13:05" to "13:45",
                    "13:50" to "14:30",
                    "14:35" to "15:15",
                    "15:20" to "16:00",
                    "16:05" to "16:45",
                )
            ))
        )
    }
}