package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.repository.TimetableRepository
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.api.timetable.model.TimetableResponse
import com.denchic45.studiversity.util.userIdOfMe
import me.tatarka.inject.annotations.Inject

@Inject
class FindYourTimetableOfWeekUseCase(private val timetableRepository: TimetableRepository) {
    suspend operator fun invoke(
        weekOfYear: String
    ): Resource<TimetableResponse> {
        return timetableRepository.findTimetable(weekOfYear, memberIds = listOf(userIdOfMe()))
    }
}