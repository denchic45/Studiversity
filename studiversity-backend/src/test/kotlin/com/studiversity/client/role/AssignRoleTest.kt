package com.studiversity.client.role

import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.config
import com.denchic45.studiversity.util.assertedResultIsError
import com.denchic45.studiversity.util.assertedResultIsOk
import com.denchic45.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.role.RoleApi
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.uuidOf
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject
import java.util.*

class AssignRoleTest : KtorClientTest() {

    private val userApi: UserApi by inject { parametersOf(client) }
    private val roleApi: RoleApi by inject { parametersOf(client) }

    private val organizationId: UUID = config.organization.id

    private lateinit var user: UserResponse

    @BeforeEach
    fun init(): Unit = runBlocking {
        user = userApi.create(
            CreateUserRequest("Anton", "Ermolin", null, "anton@mail.ru")
        ).unwrapAsserted()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        userApi.delete(user.id).assertedResultIsOk()
    }

    @Test
    fun testAssignRole(): Unit = runBlocking {
        // Student role unavailable in organization scope
        roleApi.assignRoleToUserInScope(user.id, organizationId, Role.Student.id).assertedResultIsError()

        roleApi.assignRoleToUserInScope(user.id, organizationId, Role.Moderator.id).assertedResultIsOk()

        roleApi.getUserRolesInScope(uuidOf(user.id), organizationId).unwrapAsserted().apply {
            assertEquals(listOf(Role.Moderator), this.roles)
        }

        roleApi.deleteRoleFromUserInScope(user.id, organizationId, Role.Moderator.id).assertedResultIsOk()

        roleApi.getUserRolesInScope(uuidOf(user.id), organizationId).unwrapAsserted().apply {
            assertEquals(emptyList<Role>(), this.roles)
        }
    }
}