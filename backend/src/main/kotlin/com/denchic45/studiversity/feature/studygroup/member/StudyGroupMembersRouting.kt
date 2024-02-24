package com.denchic45.studiversity.feature.studygroup.member

import com.denchic45.studiversity.feature.role.usecase.RequireAvailableRolesInScopeUseCase
import com.denchic45.studiversity.feature.role.usecase.RequireCapabilityUseCase
import com.denchic45.studiversity.feature.role.usecase.RequirePermissionToAssignRolesUseCase
import com.denchic45.studiversity.feature.studygroup.member.usecase.AddStudyGroupMemberUseCase
import com.denchic45.studiversity.feature.studygroup.member.usecase.CheckExistStudyGroupMemberUseCase
import com.denchic45.studiversity.feature.studygroup.member.usecase.FindStudyGroupMembersUseCase
import com.denchic45.studiversity.feature.studygroup.member.usecase.RemoveStudyGroupMemberUseCase
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getSortingBy
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.stuiversity.api.member.CreateMemberRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.studygroup.member.StudyGroupMemberSorting
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Route.studyGroupMembers() {
    route("/members") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findStudyGroupMembers: FindStudyGroupMembersUseCase by inject()

        val requireAvailableRolesInScope: RequireAvailableRolesInScopeUseCase by inject()
        val requirePermissionToAssignRoles: RequirePermissionToAssignRolesUseCase by inject()
        val checkExistStudyGroupMember: CheckExistStudyGroupMemberUseCase by inject()
        val addStudyGroupMembers: AddStudyGroupMemberUseCase by inject()

        get {
            // todo Добавить проверку разрешения на просмотр участников группы
            val studyGroupId = call.parameters.getUuidOrFail("studyGroupId")
            val sorting = call.request.queryParameters.getSortingBy(StudyGroupMemberSorting)
            call.respond(findStudyGroupMembers(studyGroupId, sorting))
        }

        post {
            val currentUserId = call.currentUserId()
            val studyGroupId = call.parameters.getUuidOrFail("studyGroupId")
            requireCapability(currentUserId, Capability.WriteStudyGroup, studyGroupId)

            val request = call.receive<CreateMemberRequest>()
            val assignableRoles = request.roleIds
            requirePermissionToAssignRoles(currentUserId, assignableRoles, studyGroupId)
            requireAvailableRolesInScope(assignableRoles, studyGroupId)
            if (checkExistStudyGroupMember(studyGroupId, request.memberId)) {
                call.respond(HttpStatusCode.BadRequest, StudyGroupMemberErrors.STUDY_GROUP_MEMBER_ALREADY_EXIST)
                return@post
            }

            addStudyGroupMembers(studyGroupId, request)
            call.respond(HttpStatusCode.OK)
        }

        route("/{memberId}") {
            val removeStudyGroupMember: RemoveStudyGroupMemberUseCase by inject()
            delete {
                val currentUserId = call.currentUserId()
                val studyGroupId = call.parameters.getUuidOrFail("studyGroupId")

                requireCapability(currentUserId, Capability.WriteStudyGroup, studyGroupId)

                val memberId = call.parameters.getUuidOrFail("memberId")

                removeStudyGroupMember(studyGroupId, memberId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
}