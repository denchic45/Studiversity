package com.denchic45.studiversity.feature.course.topic

import com.denchic45.studiversity.feature.course.topic.usecase.*
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.role.model.Capability
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject


fun Application.courseTopicRoutes() {
    routing {
        authenticate("auth-jwt") {
            route("/course/{courseId}/topics") {
                val requireCapability: RequireCapabilityUseCase by inject()
                val addCourseTopic: AddCourseTopicUseCase by inject()
                val findCourseTopicsByCourse: FindCourseTopicsByCourseUseCase by inject()

                post {
                    val courseId = call.parameters.getUuidOrFail("courseId")
                    requireCapability(
                        userId = call.currentUserId(),
                        capability = Capability.WriteCourseTopics,
                        scopeId = courseId
                    )

                    val topic = addCourseTopic(courseId, call.receive())
                    call.respond(HttpStatusCode.Created, topic)
                }
                get {
                    val courseId = call.parameters.getUuidOrFail("courseId")

//                    requireCapability(
//                        userId = call.currentUserId(),
//                        capability = Capability.ReadOtherCourse,
//                        scopeId = courseId
//                    )

                    val topics = findCourseTopicsByCourse(courseId)
                    call.respond(topics)
                }
            }
            courseTopicById()
        }
    }
}

private fun Route.courseTopicById() {
    route("/course-topics/{topicId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val updateCourseTopic: UpdateCourseTopicUseCase by inject()
        val reorderCourseTopic: ReorderCourseTopicUseCase by inject()
        val findCourseTopic: FindCourseTopicUseCase by inject()
        val findCourseIdByTopicId: FindCourseIdByTopicIdUseCase by inject()
        val removeCourseTopic: RemoveCourseTopicUseCase by inject()

        get {
            val topicId = call.parameters.getUuidOrFail("topicId")
            val courseId = findCourseIdByTopicId(topicId)

//            requireCapability(
//                userId = call.currentUserId(),
//                capability = Capability.ReadOtherCourse,
//                scopeId = courseId
//            )

            val topic = findCourseTopic(courseId, topicId)
            call.respond(topic)
        }
        patch {
            val topicId = call.parameters.getUuidOrFail("topicId")
            val courseId = findCourseIdByTopicId(topicId)

            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.WriteCourseTopics,
                scopeId = courseId
            )

            val topic = updateCourseTopic(courseId, topicId, call.receive())
            call.respond(topic)
        }
        delete {
            val topicId = call.parameters.getUuidOrFail("topicId")
            val courseId = findCourseIdByTopicId(topicId)

            val withElements = call.parameters.getOrFail<Boolean>("with_elements")

            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.WriteCourseTopics,
                scopeId = courseId
            )

            removeCourseTopic(courseId, topicId, withElements)

            call.respond(HttpStatusCode.NoContent)
        }
        put("/order") {
            val topicId = call.parameters.getUuidOrFail("topicId")
            val courseId = findCourseIdByTopicId(topicId)

            requireCapability(
                userId = call.currentUserId(),
                capability = Capability.WriteCourseTopics,
                scopeId = courseId
            )

            val topic = reorderCourseTopic(topicId, call.receive())
            call.respond(topic)
        }
    }
}