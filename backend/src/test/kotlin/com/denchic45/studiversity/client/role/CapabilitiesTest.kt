package com.denchic45.studiversity.client.role

import com.denchic45.studiversity.KtorClientTest
import com.denchic45.studiversity.util.assertedResultIsOk
import com.denchic45.studiversity.util.unwrapAsserted
import com.denchic45.stuiversity.api.membership.MembershipApi
import com.denchic45.stuiversity.api.role.CapabilityApi
import com.denchic45.stuiversity.api.role.RoleApi
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.studygroup.model.AcademicYear
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.Gender
import com.denchic45.stuiversity.api.user.model.UserResponse
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
    private val membershipApi: MembershipApi by inject { parametersOf(client) }

    private lateinit var user: UserResponse
    private lateinit var studyGroup: StudyGroupResponse

    @BeforeEach
    fun init(): Unit = runBlocking {
        user = userApi.create(
            CreateUserRequest("Anton", "Ermolin", null, "anton@mail.ru", Gender.MALE, emptyList())
        ).unwrapAsserted()
        studyGroup = studyGroupApi.create(
            CreateStudyGroupRequest("Test group", AcademicYear(2023, 2027), null, null)
        ).unwrapAsserted()
        membershipApi.joinToScopeManually(user.id, studyGroup.id, listOf(Role.Student.id))
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        userApi.delete(user.id).assertedResultIsOk()
        studyGroupApi.delete(studyGroup.id)
    }


    // TODO: rewrite with other roles and capabilities
    @Test
    fun test(): Unit = runBlocking {
        val response1 = capabilityApi.check(user.id, studyGroup.id, listOf(Capability.BeStudent)).unwrapAsserted()
        assertTrue(response1.hasCapability(Capability.BeStudent))
        roleApi.deleteRoleFromUserInScope(user.id, studyGroup.id, Role.Student.id).unwrapAsserted()
        roleApi.assignRoleToUserInScope(user.id, studyGroup.id, Role.Headman.id).unwrapAsserted()
        val response2 = capabilityApi.check(user.id, studyGroup.id, listOf(Capability.BeStudent)).unwrapAsserted()
        assertTrue(response2.hasCapability(Capability.BeStudent))
    }
}