package com.denchic45.studiversity.feature.course

import com.denchic45.studiversity.feature.course.element.courseElementRoutes
import com.denchic45.studiversity.feature.course.material.courseMaterialRoutes
import com.denchic45.studiversity.feature.course.subject.subjectRoutes
import com.denchic45.studiversity.feature.course.topic.courseTopicRoutes
import com.denchic45.studiversity.feature.course.work.courseWorkRoutes
import io.ktor.server.application.*

@Suppress("unused")
fun Application.configureCourses() {
    courseRoutes()
    courseTopicRoutes()

    courseElementRoutes()
    courseWorkRoutes()
    courseMaterialRoutes()

    subjectRoutes()
}

object CourseErrors {
    const val INVALID_COURSE_NAME = "INVALID_COURSE_NAME"
    const val STUDY_GROUP_ALREADY_EXIST = "STUDY_GROUP_ALREADY_EXIST"
    const val COURSE_IS_NOT_ARCHIVED = "COURSE_IS_NOT_ARCHIVED"
}