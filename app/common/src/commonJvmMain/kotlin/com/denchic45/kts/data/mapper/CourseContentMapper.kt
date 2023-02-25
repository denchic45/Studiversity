//package com.denchic45.kts.data.mapper
//
//import com.denchic45.kts.CourseContentEntity
//import com.denchic45.kts.data.db.remote.model.ContentCommentMap
//import com.denchic45.kts.data.db.remote.model.CourseContentMap
//import com.denchic45.kts.data.domain.model.Attachment
//import com.denchic45.kts.data.remote.model.CourseContentDoc
//import com.denchic45.kts.data.remote.model.SubmissionDoc
//import com.denchic45.kts.domain.model.ContentDetails
//import com.denchic45.kts.domain.model.ContentType
//import com.denchic45.kts.domain.model.CourseContent
//import com.denchic45.kts.domain.model.Task
//import com.denchic45.stuiversity.util.toDate
//import com.denchic45.kts.util.toJsonString
//import com.google.gson.GsonBuilder
//import kotlinx.serialization.decodeFromString
//import kotlinx.serialization.json.Json
//import java.io.File
//import java.time.LocalDateTime
//import java.time.ZoneId
//import java.util.*
//
//
//fun CourseContentMap.domainToEntity() = CourseContentEntity(
//    content_id = id,
//    course_id = courseId,
//    section_id = sectionId,
//    name = name,
//    description = description,
//    order = order,
//    attachments = attachments,
//    content_details = contentDetails.toJsonString(),
//    comments_enabled = commentsEnabled,
//    content_type = contentType,
//    created_date = createdDate.time,
//    timestamp = timestamp.time,
//    completion_date = completionDate?.time,
//    week_date = weekDate,
//    deleted = deleted
//)
//
//
//fun CourseContentEntity.entityToDomain(): CourseContent {
//    return when (ContentType.valueOf(content_type)) {
//        ContentType.TASK -> toTaskDomain()
//        else -> throw IllegalStateException()
//    }
//}
//
//
//fun CourseContentEntity.toTaskDomain(): Task {
//    return courseContentEntityWithDetailsToTask(
////        Json.decodeFromString(content_details)
//        GsonBuilder().create().fromJson(content_details, ContentDetails.Task::class.java)
//    )
//}
//
//
//fun CourseContentEntity.entityToTaskDoc(): CourseContentDoc {
//    val contentDetails: ContentDetails.Task = Json.decodeFromString(content_details)
//
//    val attachments: List<String>
//    val list: List<String> = this.attachments
//    attachments = ArrayList(list)
//    val order: Long = order
//    val createdDate = Date(created_date)
//    val timestamp = Date(timestamp)
//    val deleted: Boolean = deleted
//    val contentType: ContentType = ContentType.valueOf(content_type)
//    val completionDate = Date(completion_date!!)
//    val weekDate: String = week_date!!
//    val comments: List<ContentCommentMap>? = null
//    val submissions: Map<String, SubmissionDoc>? = null
//    return CourseContentDoc(
//        id = content_id,
//        courseId = course_id,
//        sectionId = section_id,
//        name = name,
//        description = description,
//        commentsEnabled = comments_enabled,
//        attachments = attachments,
//        order = order,
//        createdDate = createdDate,
//        timestamp = timestamp,
//        comments = comments,
//        submissions = submissions,
//        deleted = deleted,
//        contentType = contentType,
//        completionDate = completionDate,
//        weekDate = weekDate,
//        contentDetails = contentDetails
//    )
//}
//
//fun Task.taskToTaskDetails(): ContentDetails.Task = ContentDetails.Task(
//    disabledSendAfterDate = disabledSendAfterDate,
//    submissionSettings = submissionSettings
//)
//
//fun mapAttachmentsToFilePaths(attachments: List<Attachment>): List<String> {
//    return attachments.map { (file) -> file.path }
//}
//
//fun CourseContentEntity.courseContentEntityWithDetailsToTask(
//    taskDetails: ContentDetails.Task
//) = Task(
//    id = content_id,
//    courseId = course_id,
//    sectionId = section_id,
//    name = name,
//    description = description,
//    order = order,
//    completionDate = completion_date?.let {
//        LocalDateTime.ofInstant(
//            Date(it).toInstant(),
//            ZoneId.of("UTC")
//        )
//    },
//    disabledSendAfterDate = taskDetails.disabledSendAfterDate,
//    submissionSettings = taskDetails.submissionSettings,
//    commentsEnabled = comments_enabled,
//    createdDate = Date(created_date),
//    timestamp = Date(timestamp)
//)
//
//fun Task.toMap(attachments: List<String>, order: Long) = mutableMapOf(
//    "id" to id,
//    "courseId" to courseId,
//    "sectionId" to sectionId,
//    "name" to name,
//    "description" to description,
//    "commentsEnabled" to commentsEnabled,
//    "attachments" to attachments,
//    "order" to order,
//    "createdDate" to createdDate,
//    "timestamp" to timestamp,
//    "contentType" to ContentType.TASK.name,
//    "completionDate" to completionDate?.toDate(),
//    "weekDate" to weekDate,
//    "contentDetails" to taskToTaskDetails(),
//    "deleted" to false
//)
//
//fun mapFilePathsToAttachments(filePaths: List<String>?): List<Attachment> {
//    return filePaths?.map { path: String -> Attachment(File(path)) } ?: emptyList()
//}
//
//fun List<CourseContentEntity>.entitiesToDomains(): List<CourseContent> =
//    map { it.entityToDomain() }
//
//fun List<CourseContentEntity>.entitiesToTaskDomains(): List<Task> = map { it.toTaskDomain() }