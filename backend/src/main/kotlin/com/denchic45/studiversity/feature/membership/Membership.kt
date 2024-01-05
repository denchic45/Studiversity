package com.denchic45.studiversity.feature.membership

//import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
//import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
//import com.denchic45.studiversity.feature.role.repository.RoleRepository
//import com.denchic45.studiversity.logger.logger
//import com.denchic45.studiversity.transaction.TransactionWorker
//import com.denchic45.stuiversity.api.membership.MembershipErrors
//import com.denchic45.stuiversity.api.membership.model.ManualJoinMemberRequest
//import com.denchic45.stuiversity.api.membership.model.Member
//import com.denchic45.stuiversity.api.membership.model.ScopeMember
//import com.denchic45.stuiversity.api.role.model.Role
//import io.ktor.server.plugins.*
//import kotlinx.coroutines.*
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.collect
//import java.util.*

//abstract class Membership {
//    abstract val transactionWorker: TransactionWorker
//    abstract val membershipRepository: MembershipRepository
//    abstract val userMembershipRepository: UserMembershipRepository
//    abstract val roleRepository: RoleRepository
//    abstract val membershipId: UUID
//
//    val scopeId by lazy { membershipRepository.findScopeIdByMembershipId(membershipId) }
//}
//
//class SelfMembership(
//    override val transactionWorker: TransactionWorker,
//    override val membershipRepository: MembershipRepository,
//    override val userMembershipRepository: UserMembershipRepository,
//    override val roleRepository: RoleRepository,
//    override val membershipId: UUID,
//) : Membership() {
//
//    fun selfJoin(userId: UUID) {
//        userMembershipRepository.addMember(Member(userId, membershipId))
//    }
//}
//
//class ManualMembership(
//    override val transactionWorker: TransactionWorker,
//    override val membershipRepository: MembershipRepository,
//    override val userMembershipRepository: UserMembershipRepository,
//    override val roleRepository: RoleRepository,
//    override val membershipId: UUID,
//) : Membership() {
//
//    fun joinMember(manualJoinMemberRequest: ManualJoinMemberRequest): ScopeMember = transactionWorker {
//        val userId = manualJoinMemberRequest.userId
//        if (userMembershipRepository.existMember(userId, membershipId))
//            throw BadRequestException(MembershipErrors.MEMBER_ALREADY_EXIST_IN_MEMBERSHIP)
//        userMembershipRepository.addMember(
//            Member(userId = userId, membershipId = membershipId)
//        )
//        roleRepository.addUserRolesInScope(userId, manualJoinMemberRequest.roleIds, scopeId)
//        userMembershipRepository.findMemberByScope(userId, scopeId)
//    }
//}
//
//abstract class ExternalMembership(private val coroutineScope: CoroutineScope) : Membership() {
//
//    abstract val assignedRole: Role
//
//    companion object {
//        const val SYNC_PERIOD = 60000L
//    }
//
//    private var syncJob: Job? = null
//
//    fun init() {
//        syncMembers()
//        syncJob = syncMembersPeriodically()
//    }
//
//    private fun syncMembersPeriodically(): Job {
//        return CoroutineScope(Dispatchers.IO).launch {
//            while (isActive) {
//                delay(SYNC_PERIOD)
//                syncMembers()
//            }
//        }
//    }
//
//    protected fun syncMembers() {
//        coroutineScope.launch {
//            launch {
//                onSyncGetAddedMembers().let {
//                    logger.info { "sync add members: $it in membership: $membershipId" }
//                    if (it.isNotEmpty())
//                        userMembershipRepository.addMembersToMembership(it, membershipId, assignedRole.id)
//                }
//            }
//            launch {
//                onSyncGetRemovedMembers().let {
//                    logger.info { "sync remove members: $it in membership: $membershipId" }
//                    if (it.isNotEmpty())
//                        userMembershipRepository.removeMembersFromMembership(it, membershipId)
//                }
//            }
//        }
//    }
//
//    fun forceSync() {
//        syncJob?.cancel()
//        syncMembers()
//        syncJob = syncMembersPeriodically()
//    }
//
//    abstract suspend fun onSyncGetAddedMembers(): List<UUID>
//
//    abstract suspend fun onSyncGetRemovedMembers(): List<UUID>
//}
//
//class StudyGroupExternalMembership(
//    coroutineScope: CoroutineScope,
//    override val transactionWorker: TransactionWorker,
//    override val membershipRepository: MembershipRepository,
//    override val userMembershipRepository: UserMembershipRepository,
//    override val roleRepository: RoleRepository,
//    override val membershipId: UUID,
//) : ExternalMembership(coroutineScope) {
//
//    override val assignedRole: Role = Role.Student
//
//    private val groupIds: StateFlow<List<UUID>> =
//        membershipRepository.observeStudyGroupIdsOfExternalMembershipsByMembershipId(membershipId)
//
//    init {
//        coroutineScope.launch {
//            groupIds.collect()
//        }
//    }
//
//    override suspend fun onSyncGetAddedMembers(): List<UUID> {
//        return userMembershipRepository.findMissingStudentsFromGroupsToCourse(membershipId, groupIds.value)
//    }
//
//    override suspend fun onSyncGetRemovedMembers(): List<UUID> {
//        return userMembershipRepository.findRemainingStudentsOfCourseFromGroups(groupIds.value, membershipId)
//    }
//}