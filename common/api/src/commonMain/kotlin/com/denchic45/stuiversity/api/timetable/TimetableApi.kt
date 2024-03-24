package com.denchic45.stuiversity.api.timetable

import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.timetable.model.PeriodsSorting
import com.denchic45.stuiversity.api.timetable.model.PutTimetableOfDayRequest
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import com.denchic45.stuiversity.api.timetable.model.TimetableOfDayResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.UserId
import com.denchic45.stuiversity.util.parametersOf
import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

interface TimetableApi {
    suspend fun putTimetable(
        weekOfYear: String,
        putTimetableRequest: PutTimetableRequest,
    ): ResponseResult<TimetableResponse>

    suspend fun getTimetable(
        weekOfYear: String,
        studyGroupIds: List<UUID>? = null,
        courseIds: List<UUID>? = null,
        memberIds: List<UserId>? = null,
        roomIds: List<UUID>? = null,
        sorting: List<PeriodsSorting> = listOf(),
    ): ResponseResult<TimetableResponse>

    suspend fun getTimetableByStudyGroupId(
        date:LocalDate,
        studyGroupId: UUID,
        sorting: List<PeriodsSorting> = listOf(),
    ): ResponseResult<TimetableResponse> = getTimetable(
        weekOfYear = date.format(DateTimeFormatter.ofPattern("YYYY_ww")),
        studyGroupIds = listOf(studyGroupId),
        sorting = sorting
    )

    suspend fun getTimetableByStudyGroupId(
        weekOfYear: String,
        studyGroupId: UUID,
        sorting: List<PeriodsSorting> = listOf(),
    ): ResponseResult<TimetableResponse> = getTimetable(
        weekOfYear = weekOfYear,
        studyGroupIds = listOf(studyGroupId),
        sorting = sorting
    )

    suspend fun putTimetableOfDay(
        weekOfYear: String,
        dayOfWeek: Int,
        putTimetableOfDayRequest: PutTimetableOfDayRequest,
    ): ResponseResult<TimetableOfDayResponse>

    suspend fun getTimetableOfDay(
        date: LocalDate,
        studyGroupIds: List<UUID>? = null,
        courseIds: List<UUID>? = null,
        memberIds: List<UUID>? = null,
        roomIds: List<UUID>? = null,
        sorting: List<PeriodsSorting> = listOf(),
    ): ResponseResult<TimetableOfDayResponse> = getTimetableOfDay(
        weekOfYear = date.format(DateTimeFormatter.ofPattern("YYYY_ww")),
        dayOfWeek = date.dayOfWeek.value,
        studyGroupIds = studyGroupIds,
        courseIds = courseIds,
        memberIds = memberIds,
        roomIds = roomIds,
        sorting = sorting
    )

    suspend fun getTimetableOfDay(
        weekOfYear: String,
        dayOfWeek: Int,
        studyGroupIds: List<UUID>? = null,
        courseIds: List<UUID>? = null,
        memberIds: List<UUID>? = null,
        roomIds: List<UUID>? = null,
        sorting: List<PeriodsSorting> = listOf(),
    ): ResponseResult<TimetableOfDayResponse>

    suspend fun getTimetableOfDayByStudyGroupId(
        weekOfYear: String,
        dayOfWeek: Int,
        studyGroupId: UUID,
        sorting: List<PeriodsSorting> = listOf(),
    ): ResponseResult<TimetableOfDayResponse> = getTimetableOfDay(
        weekOfYear = weekOfYear,
        dayOfWeek = dayOfWeek,
        studyGroupIds = listOf(studyGroupId),
        sorting = sorting
    )

    suspend fun deleteTimetable(
        weekOfYear: String,
        studyGroupId: UUID,
    ): EmptyResponseResult

    suspend fun deleteTimetable(
        weekOfYear: String,
        dayOfWeek: Int,
        studyGroupId: UUID,
    ): EmptyResponseResult
}

class TimetableApiImpl(private val client: HttpClient) : TimetableApi {
    override suspend fun putTimetable(
        weekOfYear: String,
        putTimetableRequest: PutTimetableRequest,
    ): ResponseResult<TimetableResponse> {
        return client.put("/timetables/$weekOfYear") {
            contentType(ContentType.Application.Json)
            setBody(putTimetableRequest)
        }.toResult()
    }

    override suspend fun getTimetable(
        weekOfYear: String,
        studyGroupIds: List<UUID>?,
        courseIds: List<UUID>?,
        memberIds: List<UserId>?,
        roomIds: List<UUID>?,
        sorting: List<PeriodsSorting>,
    ): ResponseResult<TimetableResponse> = client.get("/timetables/$weekOfYear") {
        studyGroupIds?.forEach { parameter("study_group_id", it) }
        courseIds?.forEach { parameter("course_id", it) }
        memberIds?.forEach { parameter("member_id", it.value) }
        roomIds?.forEach { parameter("room_id", it) }
        parametersOf(values = sorting)
    }.toResult()

    override suspend fun putTimetableOfDay(
        weekOfYear: String,
        dayOfWeek: Int,
        putTimetableOfDayRequest: PutTimetableOfDayRequest,
    ): ResponseResult<TimetableOfDayResponse> = client.put("/timetables/$weekOfYear/$dayOfWeek") {
        contentType(ContentType.Application.Json)
        setBody(putTimetableOfDayRequest)
    }.toResult()

    override suspend fun getTimetableOfDay(
        weekOfYear: String,
        dayOfWeek: Int,
        studyGroupIds: List<UUID>?,
        courseIds: List<UUID>?,
        memberIds: List<UUID>?,
        roomIds: List<UUID>?,
        sorting: List<PeriodsSorting>,
    ): ResponseResult<TimetableOfDayResponse> = client.get("/timetables/$weekOfYear/$dayOfWeek") {
        studyGroupIds?.forEach { parameter("studyGroupId", it) }
        courseIds?.forEach { parameter("courseId", it) }
        memberIds?.forEach { parameter("memberId", it) }
        roomIds?.forEach { parameter("roomId", it) }
        parametersOf(values = sorting)
    }.toResult()

    override suspend fun deleteTimetable(
        weekOfYear: String,
        studyGroupId: UUID,
    ): EmptyResponseResult {
        return client.delete("/timetables/$weekOfYear") {
            parameter("studyGroupId", studyGroupId)
        }.toResult()
    }

    override suspend fun deleteTimetable(
        weekOfYear: String,
        dayOfWeek: Int,
        studyGroupId: UUID,
    ): EmptyResponseResult {
        return client.delete("/timetables/$weekOfYear/$dayOfWeek") {
            parameter("studyGroupId", studyGroupId)
        }.toResult()
    }
}