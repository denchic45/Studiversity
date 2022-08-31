package com.denchic45.kts

import com.denchic45.kts.util.toString
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DateTest {

    @Test
    fun mondayOfThisWeekTest() {
        println(
            "Monday is: " + LocalDateTime.now().plusWeeks(1).with(DayOfWeek.MONDAY).atOffset(
                ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"))
        )
    }

    @Test
    fun testConvertDate() {
        println(
            "Date is: " + LocalDateTime.now().atOffset(
                ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX"))
        )
    }
}