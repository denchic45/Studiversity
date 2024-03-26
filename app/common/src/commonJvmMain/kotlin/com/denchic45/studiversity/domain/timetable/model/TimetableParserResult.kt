package com.denchic45.studiversity.domain.timetable.model

import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse

data class TimetableParserResult(
    val weekOfYear: String,
    val timetables: List<Pair<StudyGroupItem, TimetableResponse>>,
)