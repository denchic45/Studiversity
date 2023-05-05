package com.denchic45.kts.domain.timetable.model

import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse

data class TimetableParserResult(
    val weekOfYear: String,
    val timetables: List<Pair<StudyGroupResponse, TimetableResponse>>,
)