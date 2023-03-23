package com.denchic45.kts.data.storage

import com.denchic45.kts.di.FirebaseHttpClient
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.toResource
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.schedule.ScheduleApi
import com.denchic45.stuiversity.api.schedule.model.Schedule
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import me.tatarka.inject.annotations.Inject

@Inject
class MetaStorage(
    private val scheduleApi: ScheduleApi,
) {

//    suspend fun getMeta(): Meta = client.get(metaRemoteStorage.getMetaUrl()).body()

    suspend fun getBellSchedule(): Resource<Schedule> {
        return scheduleApi.get().toResource()
    }
}