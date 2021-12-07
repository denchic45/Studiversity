package com.denchic45.kts.data.prefs

import android.content.Context
import javax.inject.Inject

class TimestampPreference @Inject constructor(context: Context) :
    BaseSharedPreference(context, "Timestamp") {
    fun setTimestampGroups(timestamp: Long) {
        setValue(TIMESTAMP_LAST_UPDATE_GROUPS, timestamp)
    }

    fun setTimestampGroupCourses(timestamp: Long) {
        setValue(TIMESTAMP_LAST_UPDATE_GROUP_COURSES, timestamp)
    }

    val lastUpdateGroupsTimestamp: Long
        get() = getValue(TIMESTAMP_LAST_UPDATE_GROUPS, 0L)
    val lastUpdateEventsTimestamp: Long
        get() = getValue(TIMESTAMP_LAST_UPDATE_EVENTS, 0L)
    val lastUpdateGroupCoursesTimestamp: Long
        get() = getValue(TIMESTAMP_LAST_UPDATE_GROUP_COURSES, 0L)
    var lastUpdateTeacherCoursesTimestamp: Long
        get() = getValue(TIMESTAMP_LAST_UPDATE_TEACHER_COURSES, 0L)
        set(value) = setValue(TIMESTAMP_LAST_UPDATE_TEACHER_COURSES, value)

    companion object {
        const val TIMESTAMP_LAST_UPDATE_EVENTS = "TIMESTAMP_LAST_UPDATE_EVENTS"
        const val TIMESTAMP_LAST_UPDATE_GROUPS = "TIMESTAMP_LAST_UPDATE_GROUPS"
        const val TIMESTAMP_LAST_UPDATE_GROUP_COURSES = "TIMESTAMP_LAST_UPDATE_COURSES_GROUP"
        const val TIMESTAMP_LAST_UPDATE_TEACHER_COURSES = "TIMESTAMP_LAST_UPDATE_TEACHER_COURSES"
        const val GROUP_HOURS_TIMEOUT = 3
    }
}