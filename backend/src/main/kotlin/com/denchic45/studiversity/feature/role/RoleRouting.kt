package com.denchic45.studiversity.feature.role

import com.denchic45.studiversity.feature.role.usecase.*
import com.denchic45.studiversity.ktor.currentUserId
import com.denchic45.studiversity.ktor.getUserUuidByParameterOrMe
import com.denchic45.studiversity.ktor.getUuidOrFail
import com.denchic45.studiversity.util.hasNotDuplicates
import com.denchic45.studiversity.validation.buildValidationResult
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.UpdateUserRolesRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import org.koin.ktor.ext.inject

fun Application.rolesRoutes() {
    routing {
        authenticate("auth-jwt") {
            userAssignedRolesRoute()
            capabilitiesRoutes()
        }
    }
}

private fun Route.userAssignedRolesRoute() {
    route("/users/{id}/scopes/{scopeId}") {
        val requireCapability: RequireCapabilityUseCase by inject()
        val findAssignableRolesByUserAndScope: FindAssignableRolesByUserAndScopeUseCase by inject()

        get("/assignable-roles") {
            val userId = call.getUserUuidByParameterOrMe("id")
            val scopeId = call.parameters.getUuidOrFail("scopeId")

            requireCapability(call.currentUserId(), Capability.WriteAssignRoles, scopeId)
            call.respond(findAssignableRolesByUserAndScope(userId, scopeId))
        }
        route("/roles") {
            install(RequestValidation) {
                validate<UpdateUserRolesRequest> {
                    buildValidationResult {
                        condition(it.roleIds.isNotEmpty(), RoleErrors.NO_ROLE_ASSIGNMENT)
                        condition(it.roleIds.hasNotDuplicates(), RoleErrors.ROLES_DUPLICATION)
                    }
                }
            }
            val requireAvailableRolesInScope: RequireAvailableRolesInScopeUseCase by inject()
            val requirePermissionToAssignRoles: RequirePermissionToAssignRolesUseCase by inject()
            val findAssignedUserRolesInScope: FindAssignedUserRolesInScopeUseCase by inject()
            val putRoleToUserInScope: SetRoleToUserInScopeUseCase by inject()
            val putRolesToUserInScope: PutRolesToUserInScopeUseCase by inject()
            val removeRoleFromUserInScope: RemoveRoleFromUserInScopeUseCase by inject()

            get {
                val userId = call.getUserUuidByParameterOrMe("id")
                val scopeId = call.parameters.getUuidOrFail("scopeId")
                call.respond(HttpStatusCode.OK, findAssignedUserRolesInScope(userId, scopeId))
            }

            put {
                val userId = call.getUserUuidByParameterOrMe("id")
                val scopeId = call.parameters.getUuidOrFail("scopeId")
                val currentUserId = call.currentUserId()
                val roleIds = call.receive<List<Long>>()

                requireCapability(currentUserId, Capability.WriteAssignRoles, scopeId) // TODO: Вернуть потом
                requireAvailableRolesInScope(roleIds, scopeId)
                requirePermissionToAssignRoles(currentUserId, roleIds, scopeId)

                putRolesToUserInScope(userId, roleIds, scopeId)
                call.respond(HttpStatusCode.OK)
            }

            route("/{roleId}") {
                put {
                    val userId = call.parameters.getUuidOrFail("id")
                    val roleId = call.parameters.getOrFail<Long>("roleId")
                    val scopeId = call.parameters.getUuidOrFail("scopeId")
                    val currentUserId = call.currentUserId()

                    requireCapability(currentUserId, Capability.WriteAssignRoles, scopeId)

                    requireAvailableRolesInScope(listOf(roleId), scopeId)
                    requirePermissionToAssignRoles(currentUserId, listOf(roleId), scopeId)

                    putRoleToUserInScope(userId, roleId, scopeId)
                    call.respond(HttpStatusCode.OK)
                }
                delete {
                    val userId = call.parameters.getUuidOrFail("id")
                    val roleId = call.parameters.getOrFail("roleId").toLong()
                    val scopeId = call.parameters.getUuidOrFail("scopeId")
                    val currentUserId = call.currentUserId()

                    requireCapability(currentUserId, Capability.WriteAssignRoles, scopeId)
                    requirePermissionToAssignRoles(currentUserId, listOf(roleId), scopeId)

                    removeRoleFromUserInScope(userId, roleId, scopeId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}