package com.denchic45.stuiversity.util

import java.text.SimpleDateFormat
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.*

fun String.toDate(format: String): Date =
    SimpleDateFormat(format, Locale.getDefault()).parse(this)!!

fun Date.toString(format: String): String =
    SimpleDateFormat(format, Locale.getDefault()).format(this)

fun LocalDateTime.toString(pattern: String): String =
    DateTimeFormatter.ofPattern(pattern, Locale.getDefault()).format(this)

fun LocalDate.toString(pattern: String): String =
    DateTimeFormatter.ofPattern(pattern).format(this)

fun LocalTime.toString(pattern: String): String =
    DateTimeFormatter.ofPattern(pattern).format(this)

fun String.toLocalDateTime(pattern: String): LocalDateTime =
    LocalDateTime.parse(this, DateTimeFormatter.ofPattern(pattern))

fun Date.toLocalDateTime(): LocalDateTime =
    LocalDateTime.ofInstant(toInstant(), ZoneId.systemDefault())

fun String.toLocalDate(pattern: String): LocalDate =
    LocalDate.parse(this, DateTimeFormatter.ofPattern(pattern))

fun Date.toLocalDate(): LocalDate =
    toInstant()
        .atZone(ZoneId.systemDefault())
        .toLocalDate()

fun LocalDateTime.toDate(): Date = Date.from(atZone(ZoneId.systemDefault()).toInstant())

fun LocalDate.toDate(): Date = Date.from(
    atStartOfDay()
        .atZone(ZoneId.systemDefault())
        .toInstant()
)

fun LocalDate.toDateUTC(): Date = Date.from(
    atStartOfDay()
        .atOffset(ZoneOffset.UTC)
        .toInstant()
)

fun Long.toToLocalDateTime() = LocalDateTime.ofInstant(
    Instant.ofEpochMilli(this),
    TimeZone.getDefault().toZoneId()
)


object Dates {
    fun toStringMonthHidingCurrentYear(date: LocalDate): String {
        return if (Year.now().value == date.year) {
            val sdf = SimpleDateFormat(DateTimePatterns.LLLL, Locale.getDefault())
            sdf.format(date.toDate())
        } else {
            val sdf = SimpleDateFormat(DateTimePatterns.LLLL_yyyy, Locale.getDefault())
            return sdf.format(date.toDate())
        }
    }

    fun toStringDayMonthHidingCurrentYear(date: LocalDate): String {
        return if (Year.now().value == date.year) {
            date.toString("dd MMMM")
        } else {
            date.toString("dd MMMM YYYY")
        }
    }

    fun isValidDate(date: String, pattern: String): Boolean {
        return try {
            date.toLocalDate(pattern)
            true
        } catch (e: DateTimeParseException) {
            false
        }
    }

    fun parseRfc3339(date: String): Date {
        return try {
            date.toDate("yyyy-MM-dd'T'HH:mm:ssX")
        } catch (e: Exception) {
            date.toDate("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        }
    }
}

object DateTimePatterns {
    const val yyy_MM_dd = "yyyy-MM-dd"
    const val LLL = "LLL"
    const val LLLL = "LLLL"
    const val LLLL_yyyy = "LLLL yyyy"
    const val DD_MM_yy = "dd.MM.yy"
    const val dd_MMM = "dd MMM"
    const val d_MMMM = "d MMMM"
    const val E = "E"
    const val YYYY_ww = "YYYY_ww"
}