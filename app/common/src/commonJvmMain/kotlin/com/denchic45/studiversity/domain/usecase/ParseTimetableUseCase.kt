package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.domain.timetable.TimetableParser
import com.denchic45.studiversity.domain.timetable.model.TimetableParserResult
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import me.tatarka.inject.annotations.Inject
import okio.Path

@Inject
class ParseTimetableUseCase(
    private val timetableParser:()-> TimetableParser
) {

    suspend operator fun invoke(document:Path): TimetableParserResult {
       return timetableParser().parseDoc(document.toFile().inputStream())
    }
}