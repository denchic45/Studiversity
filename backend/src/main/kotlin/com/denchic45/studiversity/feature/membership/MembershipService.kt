package com.denchic45.studiversity.feature.membership

//import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
//import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
//import com.denchic45.studiversity.feature.role.repository.RoleRepository
//import com.denchic45.studiversity.transaction.TransactionWorker
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//import java.util.*
//import kotlin.reflect.KClass
//
//class MembershipService(
//    private val coroutineScope: CoroutineScope,
//    val transactionWorker: TransactionWorker,
//    val membershipRepository: MembershipRepository,
//    private val userMembershipRepository: UserMembershipRepository,
//    private val roleRepository: RoleRepository,
//) {
//
//    val factory = mapOf<KClass<out Membership>, (membershipId: UUID) -> Membership>(
//        ManualMembership::class to { id ->
//            ManualMembership(
//                transactionWorker,
//                membershipRepository,
//                userMembershipRepository,
//                roleRepository,
//                id
//            )
//        },
//        SelfMembership::class to { id ->
//            SelfMembership(
//                transactionWorker,
//                membershipRepository,
//                userMembershipRepository,
//                roleRepository,
//                id
//            )
//        },
//        StudyGroupExternalMembership::class to { id ->
//            StudyGroupExternalMembership(
//                coroutineScope,
//                transactionWorker,
//                membershipRepository,
//                userMembershipRepository,
//                roleRepository,
//                id
//            )
//        }
//    )
//
//    inline fun <reified T : Membership> getMembership(membershipId: UUID): T {
//        return (factory[T::class]?.invoke(membershipId)
//            ?: throw IllegalArgumentException("No membership dependency with type: ${T::class}")) as T
//    }
//
//    inline fun <reified T : Membership> getMembershipByTypeAndScopeId(type: String, scopeId: UUID): T =
//        transactionWorker {
//            getMembership(membershipRepository.findMembershipIdByTypeAndScopeId(type, scopeId))
//        }
//
//    fun getExternalMemberships(): List<ExternalMembership> {
//        return membershipRepository.findIdsByType("by_group")
//            .map { membershipId -> getMembership<StudyGroupExternalMembership>(membershipId) }
//    }
//
//    fun observeAddMemberships(): Flow<StudyGroupExternalMembership> {
//        return membershipRepository.observeOnFirstAddExternalStudyGroupMembershipInMembership()
//            .map { getMembership(it) }
//    }
//
//    fun observeRemoveMemberships(): Flow<UUID> {
//        return membershipRepository.observeRemoveExternalStudyGroupMemberships()
//    }
//}