package com.denchic45.kts.data.model.domain

import com.denchic45.kts.data.model.DomainModel
import java.util.*

abstract class CourseContent : DomainModel() {
    abstract val courseId: String
    abstract val sectionId: String
    abstract val name: String
    abstract val description: String
    abstract val commentsEnabled: Boolean
    abstract val createdDate: Date
    abstract val timestamp: Date
}

sealed class ContentDetails {
    class Task(
        val isDisabledSendAfterDate: Boolean,
        val attachments: List<String>,
        val answerType: AnswerType,
        val markType: MarkType
    ) : ContentDetails()

    object Empty : ContentDetails()
}

enum class ContentType {
    TASK, POST, TEST
}