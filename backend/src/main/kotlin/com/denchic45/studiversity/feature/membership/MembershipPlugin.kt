package com.denchic45.studiversity.feature.membership

//import com.denchic45.studiversity.logger.logger
//import io.ktor.server.application.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import org.koin.ktor.ext.inject
//
//fun Application.configureMembership() {
//    val membershipService: MembershipService by inject()
//    val coroutineScope: CoroutineScope by inject()
//
//    val memberships = membershipService.getExternalMemberships()
//        .associateBy(ExternalMembership::membershipId).toMutableMap()
//    logger.info { "started ${memberships.size} memberships" }
//
//    membershipRoutes(memberships)
//
//    memberships.values.forEach(ExternalMembership::init)
//
//    coroutineScope.launch {
//        membershipService.observeAddMemberships().collect {
//            logger.info { "Added membership: ${it.membershipId}" }
//            it.init()
//            memberships[it.membershipId] = it
//            logger.info { "After adding membership total count: ${memberships.size}" }
//        }
//    }
//
//    coroutineScope.launch {
//        membershipService.observeRemoveMemberships().collect { oldMembershipId ->
//            logger.info { "Removed membership: $oldMembershipId" }
//            memberships.remove(oldMembershipId)
//            logger.info { "After deleting membership total count: ${memberships.size}" }
//        }
//    }
//}