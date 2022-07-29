package com.denchic45.kts.data.mapper

import com.denchic45.kts.CourseContentEntity
import com.denchic45.kts.data.remote.model.ContentCommentMap
import com.denchic45.kts.data.remote.model.CourseContentDoc
import com.denchic45.kts.data.remote.model.CourseContentMap
import com.denchic45.kts.data.remote.model.SubmissionDoc
import com.denchic45.kts.domain.model.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*


fun CourseContentMap.domainToEntity() = CourseContentEntity(
    content_id = id,
    course_id = courseId,
    section_id = sectionId,
    name = name,
    description = description,
    order = order,
    attachments = attachments,
    content_details = Json.encodeToString(contentDetails),
    comments_enabled = commentsEnabled,
    content_type = contentType,
    created_date = createdDate.time,
    timestamp = timestamp!!.time,
    completion_date = completionDate!!.time,
    week_date = weekDate!!,
    deleted = deleted
)


fun CourseContentEntity.entityToUserDomain(): CourseContent {
    return when (ContentType.valueOf(content_type)) {
        ContentType.TASK -> toTaskDomain()
        else -> throw IllegalStateException()
    }
}


fun CourseContentEntity.toTaskDomain(): Task {
    return courseContentEntityWithDetailsToTask(
        this,
        Json.decodeFromString(content_details)
//        GsonBuilder().create().fromJson(entity.contentDetails, ContentDetails.Task::class.java)
    )
}


fun CourseContentEntity.entityToTaskDoc(): CourseContentDoc {
    val contentDetails: ContentDetails.Task = Json.decodeFromString(content_details)

    val attachments: List<String>
    val id: String = content_id
    val list: List<String> = this.attachments
    attachments = ArrayList(list)
    val order: Long = order
    val courseId: String = course_id
    val sectionId: String = section_id
    val name: String = name
    val description: String = description
    val commentsEnabled: Boolean = comments_enabled
    val createdDate = Date(created_date)
    val timestamp = Date(timestamp)
    val deleted: Boolean = deleted
    val contentType: ContentType = ContentType.valueOf(content_type)
    val completionDate = Date(completion_date)
    val weekDate: String = week_date
    val comments: List<ContentCommentMap>? = null
    val submissions: Map<String, SubmissionDoc>? = null
    return CourseContentDoc(
        id,
        courseId,
        sectionId,
        name,
        description, commentsEnabled,
        attachments, order,
        createdDate, timestamp, comments, submissions, deleted,
        contentType, completionDate, weekDate, contentDetails
    )
}

fun taskWithDetailsToTaskDoc(
    task: Task,
    contentDetails: ContentDetails.Task,
    contentType: ContentType
): CourseContentDoc {
    val completionDate: Date? = Date.from(task.completionDate?.toInstant(ZoneOffset.UTC))
    val id: String = task.id
    val attachments: List<String> = mapAttachmentsToFilePaths(task.attachments)
    val order: Long = task.order
    val courseId: String = task.courseId
    val sectionId: String = task.sectionId
    val name: String = task.name
    val description: String = task.description
    val commentsEnabled: Boolean = task.commentsEnabled
    val createdDate: Date = task.createdDate
    val timestamp: Date = task.timestamp

    val weekDate: String? = task.weekDate

    val submissions: Map<String, SubmissionDoc>? = null
    val comments: List<ContentCommentMap>? = null
    val deleted = false
    return CourseContentDoc(
        id, courseId,
        sectionId, name, description, commentsEnabled, attachments, order,
        createdDate, timestamp, comments, submissions, deleted,
        contentType, completionDate, weekDate,
        contentDetails
    )
}

fun mapAttachmentsToFilePaths(attachments: List<Attachment>): List<String> {
    return attachments.map { (file) -> file.path }
}

fun courseContentEntityWithDetailsToTask(
    courseEntity: CourseContentEntity,
    taskDetails: ContentDetails.Task
): Task {
    val order = courseEntity.order
    val commentsEnabled = courseEntity.comments_enabled
    val id: String = courseEntity.course_id
    val attachments: List<Attachment> = mapFilePathsToAttachments(courseEntity.attachments)
    val courseId: String = courseEntity.course_id
    val sectionId: String = courseEntity.section_id
    val name: String = courseEntity.name
    val description: String = courseEntity.description

    val completionDate: LocalDateTime = LocalDateTime.ofInstant(
        Date(courseEntity.completion_date).toInstant(),
        ZoneId.of("UTC")
    )

    val createdDate = Date(courseEntity.created_date)
    val timestamp = Date(courseEntity.timestamp)

    val disabledSendAfterDate: Boolean = taskDetails.disabledSendAfterDate
    val submissionSettings: SubmissionSettings = taskDetails.submissionSettings
    return Task(
        id,
        courseId,
        sectionId,
        name, description, order, completionDate, disabledSendAfterDate, attachments,
        submissionSettings, commentsEnabled,
        createdDate,
        timestamp
    )
}

fun mapFilePathsToAttachments(filePaths: List<String>?): List<Attachment> {
    return filePaths?.map { path: String -> Attachment(File(path)) } ?: emptyList()
}

fun List<CourseContentEntity>.entitiesToDomains(): List<CourseContent> = map { it.entityToUserDomain() }

fun List<CourseContentEntity>.entitiesToTaskDomains(): List<Task> = map { it.toTaskDomain() }