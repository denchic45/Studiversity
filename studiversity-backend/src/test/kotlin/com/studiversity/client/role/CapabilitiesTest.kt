package com.studiversity.client.role

import com.studiversity.KtorClientTest
import com.studiversity.util.assertedResultIsOk
import com.studiversity.util.unwrapAsserted
import com.stuiversity.api.auth.model.CreateUserRequest
import com.stuiversity.api.membership.MembershipsApi
import com.stuiversity.api.role.CapabilityApi
import com.stuiversity.api.role.RoleApi
import com.stuiversity.api.role.model.Capability
import com.stuiversity.api.role.model.Role
import com.stuiversity.api.studygroup.StudyGroupApi
import com.stuiversity.api.studygroup.model.AcademicYear
import com.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.stuiversity.api.studygroup.model.StudyGroupResponse
import com.stuiversity.api.user.UserApi
import com.stuiversity.api.user.model.User
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject

class CapabilitiesTest : KtorClientTest() {

    private val capabilityApi: CapabilityApi by inject { parametersOf(client) }
    private val roleApi: RoleApi by inject { parametersOf(client) }
    private val userApi: UserApi by inject { parametersOf(client) }
    private val studyGroupApi: StudyGroupApi by inject { parametersOf(client) }
    private val membershipsApi: MembershipsApi by inject { parametersOf(client) }

    private lateinit var user: User
    private lateinit var studyGroup: StudyGroupResponse

    @BeforeEach
    fun init(): Unit = runBlocking {
        user = userApi.create(
            CreateUserRequest("Anton", "Ermolin", null, "anton@mail.ru")
        ).unwrapAsserted()
        studyGroup = studyGroupApi.create(
            CreateStudyGroupRequest("Test group", AcademicYear(2023, 2027), null)
        ).unwrapAsserted()
        membershipsApi.joinToScopeManually(user.id, studyGroup.id, listOf(Role.Student.id))
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        userApi.delete(user.id).assertedResultIsOk()
        studyGroupApi.delete(studyGroup.id)
    }


    // TODO: rewrite with other roles and capabilities
    @Test
    fun test(): Unit = runBlocking {
        val response1 = capabilityApi.check(user.id, studyGroup.id, Capability.BeStudent).unwrapAsserted()
        assertTrue(response1.hasCapability(Capability.BeStudent))
        roleApi.deleteRoleFromUserInScope(user.id, studyGroup.id, Role.Student.id).unwrapAsserted()
        roleApi.assignRoleToUserInScope(user.id, studyGroup.id, Role.Headman.id).unwrapAsserted()
        val response2 = capabilityApi.check(user.id, studyGroup.id, Capability.BeStudent).unwrapAsserted()
        assertTrue(response2.hasCapability(Capability.BeStudent))
    }
}