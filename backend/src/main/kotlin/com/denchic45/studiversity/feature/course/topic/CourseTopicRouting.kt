package com.denchic45.studiversity.feature.course.topic

import com.denchic45.studiversity.feature.course.topic.usecase.*
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import com.denchic45.stuiversity.api.role.model.Capability
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject

fun Route.courseTopicsRoutes() {
    route("/topics") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val addCourseTopic: AddCourseTopicUseCase by inject()
        val findCourseTopicsByCourse: FindCourseTopicsByCourseUseCase by inject()

        post {
            val currentUserId = call.currentUserId()
            val courseId = call.parameters.getUuidOrFail("courseId")

            requireCapability(
                userId = currentUserId,
                capability = Capability.WriteCourseTopics,
                scopeId = courseId
            )

            val topic = addCourseTopic(courseId, call.receive())
            call.respond(HttpStatusCode.Created, topic)
        }

        get {
            val currentUserId = call.currentUserId()
            val courseId = call.parameters.getUuidOrFail("courseId")

            requireCapability(
                userId = currentUserId,
                capability = Capability.ReadCourse,
                scopeId = courseId
            )

            val topics = findCourseTopicsByCourse(courseId)
            call.respond(HttpStatusCode.OK, topics)
        }
        courseTopicById()
    }
}

private fun Route.courseTopicById() {
    route("/{topicId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val updateCourseTopic: UpdateCourseTopicUseCase by inject()
        val findCourseTopic: FindCourseTopicUseCase by inject()
        val removeCourseTopic: RemoveCourseTopicUseCase by inject()

        get {
            val currentUserId = call.currentUserId()
            val courseId = call.parameters.getUuidOrFail("courseId")
            val topicId = call.parameters.getUuidOrFail("topicId")

            requireCapability(
                userId = currentUserId,
                capability = Capability.ReadCourse,
                scopeId = courseId
            )

            val topic = findCourseTopic(courseId, topicId)
            call.respond(HttpStatusCode.OK, topic)
        }
        patch {
            val currentUserId = call.currentUserId()
            val courseId = call.parameters.getUuidOrFail("courseId")
            val topicId = call.parameters.getUuidOrFail("topicId")

            requireCapability(
                userId = currentUserId,
                capability = Capability.WriteCourseTopics,
                scopeId = courseId
            )

            val topic = updateCourseTopic(courseId, topicId, call.receive())
            call.respond(HttpStatusCode.OK, topic)
        }
        delete {
            val currentUserId = call.currentUserId()
            val courseId = call.parameters.getUuidOrFail("courseId")
            val topicId = call.parameters.getUuidOrFail("topicId")

            val relatedTopicElements =
                Json.decodeFromString<RelatedTopicElements>(call.parameters.getOrFail("elements"))

            requireCapability(
                userId = currentUserId,
                capability = Capability.WriteCourseTopics,
                scopeId = courseId
            )

            removeCourseTopic(courseId, topicId, relatedTopicElements)

            call.respond(HttpStatusCode.NoContent)
        }
    }
}