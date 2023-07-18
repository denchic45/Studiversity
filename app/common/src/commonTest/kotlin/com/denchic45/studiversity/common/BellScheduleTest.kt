package com.denchic45.studiversity.common

import com.denchic45.studiversity.data.service.model.BellSchedule
import com.denchic45.studiversity.data.service.model.PeriodTime
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
                        PeriodTime("8:30", "9:10"),
                        PeriodTime("9:20", "10:00"),
                        PeriodTime("10:05", "10:45"),
                        PeriodTime("10:50", "11:30"),
                        PeriodTime("11:35", "12:15"),
                        PeriodTime("12:20", "13:00"),
                        PeriodTime("13:05", "13:45"),
                        PeriodTime("13:50", "14:30"),
                        PeriodTime("14:35", "15:15"),
                        PeriodTime("15:20", "16:00"),
                        PeriodTime("16:05", "16:45"),
                    ),
                    null
                )
            )
        )
    }
}