package com.denchic45.studiversity.data.storage

import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.toResource
import com.denchic45.stuiversity.api.schedule.ScheduleApi
import com.denchic45.stuiversity.api.schedule.model.Schedule
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