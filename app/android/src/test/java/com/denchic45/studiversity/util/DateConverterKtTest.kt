package com.denchic45.studiversity.util

import org.junit.jupiter.api.Test
import java.time.LocalDate

internal class DateConverterKtTest {

    @Test
    fun toDateTest() {
        print("date is: ${LocalDate.now().toDate()}")
    }
}