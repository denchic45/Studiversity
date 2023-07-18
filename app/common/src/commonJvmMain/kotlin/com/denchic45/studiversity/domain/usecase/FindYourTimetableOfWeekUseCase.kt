package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.TimetableRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.uuidOfMe
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourTimetableOfWeekUseCase(private val timetableRepository: TimetableRepository) {
    suspend operator fun invoke(
        weekOfYear: String
    ): Resource<TimetableResponse> {
        return timetableRepository.findTimetable(weekOfYear, memberIds = listOf(uuidOfMe()))
    }
}