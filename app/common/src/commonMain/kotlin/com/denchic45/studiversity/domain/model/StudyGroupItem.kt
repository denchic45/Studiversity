package com.denchic45.studiversity.domain.model

import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.StudyGroupNameResponse
import java.util.UUID

@Parcelize
data class StudyGroupItem(val id: UUID, val name: String) : Parcelable

fun StudyGroupNameResponse.toItem() = StudyGroupItem(
    id = id,
    name = name
)

fun StudyGroupResponse.toItem() = StudyGroupItem(
    id = id,
    name = name
)