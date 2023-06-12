package com.denchic45.studiversity.feature.membership

import com.denchic45.studiversity.config
import com.denchic45.studiversity.feature.membership.usecase.FindMembershipByScopeUseCase
import com.denchic45.studiversity.feature.membership.usecase.RemoveMemberFromScopeUseCase
import com.denchic45.studiversity.feature.membership.usecase.RemoveSelfMemberFromScopeUseCase
import com.denchic45.studiversity.feature.role.RoleErrors
import com.denchic45.studiversity.feature.role.usecase.*
import com.denchic45.studiversity.ktor.ForbiddenException
import com.denchic45.studiversity.ktor.claimId
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.jwtPrincipal
import com.denchic45.studiversity.util.hasNotDuplicates
import com.denchic45.studiversity.validation.buildValidationResult
import com.denchic45.stuiversity.api.membership.model.ManualJoinMemberRequest
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.UpdateUserRolesRequest
import com.denchic45.stuiversity.util.toUUID
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject
import java.util.*

fun Application.membershipRoutes(memberships: Map<UUID, ExternalMembership>) {
    routing {
        authenticate("auth-jwt") {
            route("/scopes/{scopeId}") {
                membersRoute()
                route("/memberships") {
                    val findMembershipByScope: FindMembershipByScopeUseCase by inject()
                    get {
                        findMembershipByScope(
                            call.parameters["scopeId"]!!.toUUID(),
                            call.request.queryParameters["type"]
                        )?.let { membershipId -> call.respond(HttpStatusCode.OK, membershipId.toString()) }
                            ?: throw NotFoundException()
                    }
                    route("/{membershipId}") {
                        val requireCapability: RequireCapabilityUseCase by inject()
                        post("/sync") {
                            requireCapability(
                                call.jwtPrincipal().payload.claimId,
                                Capability.WriteMembership,
                                config.organization.id
                            )
                            memberships[call.parameters.getOrFail("membershipId").toUUID()]
                                ?.forceSync()
                                ?: throw NotFoundException()
                            call.respond(HttpStatusCode.Accepted)
                        }
                    }
                }
            }
        }
    }
}

fun Route.membersRoute() {
    route("/members") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val requireAvailableRolesInScope: RequireAvailableRolesInScopeUseCase by inject()
        val requirePermissionToAssignRoles: RequirePermissionToAssignRolesUseCase by inject()
        val findMembersInScope: FindMembersInScopeUseCase by inject()
        val existMemberInScopeUseCase: ExistMemberInScopeUseCase by inject()
        val membershipService: MembershipService by inject()

        get {
            val scopeId = call.parameters["scopeId"]!!.toUUID()
            val currentUserId = call.currentUserId()


            if (!existMemberInScopeUseCase(currentUserId, scopeId))
                requireCapability(currentUserId, Capability.ReadMembers, scopeId)

            findMembersInScope(scopeId).apply {
                call.respond(HttpStatusCode.OK, this)
            }
        }
        post {
            val currentUserId = call.jwtPrincipal().payload.claimId
            val scopeId = call.parameters["scopeId"]!!.toUUID()

            val result = when (call.request.queryParameters["action"]!!) {
                "manual" -> {
                    val body = call.receive<ManualJoinMemberRequest>()

                    requireCapability(currentUserId, Capability.WriteMembers, scopeId)

                    val assignableRoles = body.roleIds
                    requireAvailableRolesInScope(assignableRoles, scopeId)
                    requirePermissionToAssignRoles(currentUserId, assignableRoles, scopeId)

                    membershipService.getMembershipByTypeAndScopeId<ManualMembership>("manual", scopeId)
                        .joinMember(body)
                }

                else -> throw BadRequestException("UNKNOWN_MEMBERSHIP_ACTION")
            }
            call.respond(HttpStatusCode.Created, result)
        }
        memberByIdRoute()
    }
}

private fun Route.memberByIdRoute() {
    route("/{memberId}") {
        install(RequestValidation) {
            validate<UpdateUserRolesRequest> {
                buildValidationResult {
                    condition(it.roleIds.isNotEmpty(), RoleErrors.NO_ROLE_ASSIGNMENT)
                    condition(it.roleIds.hasNotDuplicates(), RoleErrors.ROLES_DUPLICATION)
                }
            }
        }

        val requireCapability: RequireCapabilityUseCase by inject()
        val removeMemberFromScope: RemoveMemberFromScopeUseCase by inject()
        val removeSelfMemberFromScope: RemoveSelfMemberFromScopeUseCase by inject()

        delete {
            val currentUserId = call.currentUserId()
            val scopeId = call.parameters.getOrFail("scopeId").toUUID()
            val memberId = call.parameters.getOrFail("memberId").toUUID()
            when (call.request.queryParameters.getOrFail("action")) {
                "manual" -> {
                    requireCapability(currentUserId, Capability.WriteMembers, scopeId)
                    removeMemberFromScope(memberId, scopeId)
                }

                "self" -> {
                    if (currentUserId != memberId)
                        removeSelfMemberFromScope(memberId, scopeId)
                    else throw ForbiddenException()
                }

                else -> throw BadRequestException("UNKNOWN_MEMBERSHIP_ACTION")
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}