package com.studiversity.client.role

import com.studiversity.KtorClientTest
import com.studiversity.di.OrganizationEnv
import com.studiversity.util.assertedResultIsError
import com.studiversity.util.assertedResultIsOk
import com.studiversity.util.unwrapAsserted
import com.stuiversity.api.auth.model.CreateUserRequest
import com.stuiversity.api.role.RoleApi
import com.stuiversity.api.role.model.Role
import com.stuiversity.api.user.UserApi
import com.stuiversity.api.user.model.User
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.core.qualifier.named
import org.koin.test.inject
import java.util.*

class AssignRoleTest : KtorClientTest() {

    private val userApi: UserApi by inject { parametersOf(client) }
    private val roleApi: RoleApi by inject { parametersOf(client) }

    private val organizationId: UUID by inject(named(OrganizationEnv.ORG_ID))

    private lateinit var user: User

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

        roleApi.getUserRolesInScope(user.id, organizationId).unwrapAsserted().apply {
            assertEquals(listOf(Role.Moderator), this.roles)
        }

        roleApi.deleteRoleFromUserInScope(user.id, organizationId, Role.Moderator.id).assertedResultIsOk()

        roleApi.getUserRolesInScope(user.id, organizationId).unwrapAsserted().apply {
            assertEquals(emptyList<Role>(), this.roles)
        }
    }
}