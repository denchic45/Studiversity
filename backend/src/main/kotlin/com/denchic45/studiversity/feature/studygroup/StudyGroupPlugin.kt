package com.denchic45.studiversity.feature.studygroup

import io.ktor.server.application.*

@Suppress("unused")
fun Application.configureStudyGroups() {
    studyGroupRoutes()
}

object StudyGroupErrors {
    const val INVALID_STUDY_GROUP_NAME = "INVALID_STUDY_GROUP_NAME"
    const val INVALID_ACADEMIC_YEAR = "INVALID_ACADEMIC_YEAR"
    const val STUDY_GROUP_DOES_NOT_EXIST = "STUDY_GROUP_DOES_NOT_EXIST"
}