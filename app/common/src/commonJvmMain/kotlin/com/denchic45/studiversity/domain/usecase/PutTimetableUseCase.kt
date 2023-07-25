package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.TimetableRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import me.tatarka.inject.annotations.Inject

@Inject
class PutTimetableUseCase(private val timetableRepository: TimetableRepository) {
    suspend operator fun invoke(
        weekOfYear: String,
        putTimetableRequest: PutTimetableRequest
    ): Resource<TimetableResponse> {
        return timetableRepository.updateTimetableOfWeek(weekOfYear, putTimetableRequest)
    }
}