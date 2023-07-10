package com.denchic45.stuiversity.util

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

fun LocalDate.withDayOfWeek(dayOfWeek: DayOfWeek): LocalDate {
    return with(TemporalAdjusters.previousOrSame(dayOfWeek))
}