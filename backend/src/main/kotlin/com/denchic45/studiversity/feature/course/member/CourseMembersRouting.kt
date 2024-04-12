package com.denchic45.studiversity.feature.course.member

import com.denchic45.studiversity.api.course.member.CourseMemberSorting
import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.feature.course.member.usecase.CheckExistCourseMemberUseCase
import com.denchic45.studiversity.feature.course.member.usecase.EnrollCourseMemberUseCase
import com.denchic45.studiversity.feature.course.member.usecase.FindCourseMembersUseCase
import com.denchic45.studiversity.feature.course.member.usecase.RemoveCourseMemberUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireAvailableRolesInScopeUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.feature.role.usecase.RequirePermissionToAssignRolesUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getSortingBy
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.studiversity.util.ErrorInfo
import com.denchic45.studiversity.util.ErrorResponse
import com.denchic45.studiversity.util.respondWithError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.courseMembersRoutes() {
    route("/members") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findCourseMembersUseCase: FindCourseMembersUseCase by inject()

        val requireAvailableRolesInScope: RequireAvailableRolesInScopeUseCase by inject()
        val requirePermissionToAssignRoles: RequirePermissionToAssignRolesUseCase by inject()
        val checkExistCourseMember: CheckExistCourseMemberUseCase by inject()
        val enrollCourseMember: EnrollCourseMemberUseCase by inject()

        get {
            // todo Добавить проверку разрешения на просмотр участников курса
            val courseId = call.parameters.getUuidOrFail("courseId")
            val sorting = call.request.queryParameters.getSortingBy(CourseMemberSorting)
            call.respond(findCourseMembersUseCase(courseId, sorting))
        }

        post {
            val currentUserId = call.currentUserId()
            val courseId = call.parameters.getUuidOrFail("courseId")
            requireCapability(currentUserId, Capability.WriteCourse, courseId)

            val request = call.receive<CreateMemberRequest>()
            val assignableRoles = request.roleIds

            requirePermissionToAssignRoles(currentUserId, assignableRoles, courseId)
            requireAvailableRolesInScope(assignableRoles, courseId)
            if (checkExistCourseMember(courseId, request.memberId)) {
                call.respond(
                    HttpStatusCode.BadRequest,
                    ErrorResponse(
                        HttpStatusCode.BadRequest.value,
                        ErrorInfo(CourseMemberErrors.COURSE_MEMBER_ALREADY_EXIST)
                    )
                )
                return@post
            }

            enrollCourseMember(courseId, request)
            call.respond(HttpStatusCode.OK)
        }

        route("/{memberId}") {
            val removeCourseMember: RemoveCourseMemberUseCase by inject()
            delete {
                val currentUserId = call.currentUserId()
                val courseId = call.parameters.getUuidOrFail("courseId")

                requireCapability(currentUserId, Capability.WriteCourse, courseId)

                val memberId = call.parameters.getUuidOrFail("memberId")

                val deleted = removeCourseMember(courseId, memberId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respondWithError(
                        HttpStatusCode.Conflict,
                        ErrorInfo(CourseMemberErrors.COURSE_MEMBER_CAN_NOT_BE_DELETED)
                    )
                }
            }
        }
    }
}