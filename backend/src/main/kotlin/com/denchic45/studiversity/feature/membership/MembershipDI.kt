package com.denchic45.studiversity.feature.membership

//import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
//import com.denchic45.studiversity.feature.membership.repository.UserMembershipRepository
//import com.denchic45.studiversity.feature.membership.usecase.*
//import com.denchic45.studiversity.feature.role.usecase.FindMembersInScopeUseCase
//import org.koin.dsl.module
//
//private val useCaseModule = module {
//    single { AddUserToMembershipUseCase(get(), get()) }
//    single { RemoveUserFromMembershipUseCase(get(), get()) }
//    single { RemoveMemberFromScopeUseCase(get(), get(), get()) }
//    single { RemoveSelfMemberFromScopeUseCase(get(), get(), get()) }
//    single { FindMembersInScopeUseCase(get(), get()) }
//    single { FindMembershipByScopeUseCase(get(), get()) }
//}
//
//private val serviceModule = module {
//    single { MembershipService(get(), get(), get(), get(), get()) }
//}
//
//private val repositoryModule = module {
//    single { MembershipRepository(get(), get()) }
//    single { UserMembershipRepository(get(), get()) }
//}
//
//val membershipModule = module {
//    includes(serviceModule, useCaseModule, repositoryModule)
//}