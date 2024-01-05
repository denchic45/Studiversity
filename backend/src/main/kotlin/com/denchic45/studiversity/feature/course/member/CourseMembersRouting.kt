package com.denchic45.studiversity.feature.course.member

import com.denchic45.studiversity.feature.course.member.usecase.CheckExistCourseMemberUseCase
import com.denchic45.studiversity.feature.course.member.usecase.EnrollCourseMemberUseCase
import com.denchic45.studiversity.feature.course.member.usecase.FindCourseMembersUseCase
import com.denchic45.studiversity.feature.course.member.usecase.RemoveCourseMemberUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireAvailableRolesInScopeUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.feature.role.usecase.RequirePermissionToAssignRolesUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.studiversity.util.respondWithError
import com.denchic45.stuiversity.api.member.CreateMemberRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.util.ErrorInfo
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.courseMembers() {
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
            call.respond(findCourseMembersUseCase(courseId))
        }

        post {
            val currentUserId = call.currentUserId()
            val courseId = call.parameters.getUuidOrFail("courseId")
            // todo test Проверять наличие права зачислять участников на курс
            requireCapability(currentUserId, Capability.WriteMembers, courseId)

            val request = call.receive<CreateMemberRequest>()
            val assignableRoles = request.roleIds
            // todo test Проверять допустимость назначенных ролей
            requirePermissionToAssignRoles(currentUserId, assignableRoles, courseId)
            requireAvailableRolesInScope(assignableRoles, courseId)
            // todo test Проверять не добавлен ли пользователь уже
            if (checkExistCourseMember(courseId, request.memberId)) {
                call.respond(HttpStatusCode.BadRequest, CourseMemberErrors.COURSE_MEMBER_ALREADY_EXIST)
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

                requireCapability(currentUserId, Capability.WriteMembers, courseId)

                val memberId = call.parameters.getUuidOrFail("memberId")

                val deleted = removeCourseMember(courseId, memberId)
                if (deleted) {
                    call.respond(HttpStatusCode.NoContent)
                } else {
                    call.respondWithError(HttpStatusCode.Conflict, ErrorInfo(CourseMemberErrors.COURSE_MEMBER_CAN_NOT_BE_DELETED))
                }
            }
        }
    }
}