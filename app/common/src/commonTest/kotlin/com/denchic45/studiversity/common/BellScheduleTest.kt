package com.denchic45.studiversity.common

import com.denchic45.studiversity.data.service.model.BellPeriod
import com.denchic45.studiversity.data.service.model.BellSchedule
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.Test

class BellScheduleTest {

    private val json = Json { prettyPrint = true }

    @Test
    fun test() {
        println(
            json.encodeToString(
                BellSchedule(
                    listOf(
                        BellPeriod("8:30", "9:10"),
                        BellPeriod("9:20", "10:00"),
                        BellPeriod("10:05", "10:45"),
                        BellPeriod("10:50", "11:30"),
                        BellPeriod("11:35", "12:15"),
                        BellPeriod("12:20", "13:00"),
                        BellPeriod("13:05", "13:45"),
                        BellPeriod("13:50", "14:30"),
                        BellPeriod("14:35", "15:15"),
                        BellPeriod("15:20", "16:00"),
                        BellPeriod("16:05", "16:45"),
                    ),
                    null
                )
            )
        )
    }
}