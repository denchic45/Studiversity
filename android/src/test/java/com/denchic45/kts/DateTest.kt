package com.denchic45.kts

import com.denchic45.kts.util.Dates
import com.denchic45.kts.util.toDate
import org.junit.Test
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class DateTest {
    val format1 = "yyyy-MM-dd'T'HH:mm:ssX"
    val format2 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    @Test
    fun mondayOfThisWeekTest() {
        println(
            "Monday is: " + LocalDateTime.now().plusWeeks(1).with(DayOfWeek.MONDAY).atOffset(
                ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern(format1))
        )
    }

    @Test
    fun testFormatDate() {


        val first = "2022-09-06T21:00:00Z"
        val second = "2022-09-07T15:42:38.062Z"

        println(first.toDate(format1))
    }

    @Test
    fun test() {
        Dates.parseRfc3339("2022-09-06T21:00:00Z")
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