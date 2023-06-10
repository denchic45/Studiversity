package com.denchic45.studiversity.data.pref

import com.russhwolf.settings.Settings

class CoursePreferences(settings: Settings) : Settings by settings {

//    fun getTimestampContentsOfCourse(courseId: String): Long {
//        return getLong(COURSE_CONTENT_TIMESTAMP_ + courseId)
//    }

//    fun setTimestampContentsOfCourse(courseId: String, timestamp: Long) {
//        putLong(COURSE_CONTENT_TIMESTAMP_ + courseId, timestamp)
//    }

    companion object {
        const val COURSE_CONTENT_TIMESTAMP_ = "courseContentTimestamp_"
    }
}