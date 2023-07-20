package com.denchic45.studiversity.domain.model

import java.util.Date

abstract class CourseContent : DomainModel {
    abstract val courseId: String
    abstract val sectionId: String
    abstract val name: String
    abstract val description: String
    abstract val order: Long
    abstract val commentsEnabled: Boolean
    abstract val createdDate: Date
    abstract val timestamp: Date
}

sealed class ContentDetails {
    class Task(
        val disabledSendAfterDate: Boolean,
        val submissionSettings: SubmissionSettings,
    ) : ContentDetails()

    object Empty : ContentDetails()
}

enum class ContentType {
    TASK, POST, TEST
}
