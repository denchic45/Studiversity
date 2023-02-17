package com.denchic45.stuiversity.api.schedule

import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.common.toResult
import com.denchic45.stuiversity.api.schedule.model.Schedule
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*

interface ScheduleApi {
    suspend fun put(schedule: Schedule): ResponseResult<Schedule>

    suspend fun get(): ResponseResult<Schedule>
}

class ScheduleApiImpl(private val client: HttpClient) : ScheduleApi {
    override suspend fun put(schedule: Schedule): ResponseResult<Schedule> {
        return client.put("/schedule") {
            contentType(ContentType.Application.Json)
            setBody(schedule)
        }.toResult()
    }

    override suspend fun get(): ResponseResult<Schedule> {
        return client.get("/schedule").toResult()
    }
}