//package com.denchic45.kts.ui.login.groupChooser
//
//import com.denchic45.kts.domain.model.GroupHeader
//import com.denchic45.kts.domain.model.Specialty
//import com.denchic45.kts.data.repository.GroupRepository
//import com.denchic45.kts.data.repository.SpecialtyRepository
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.Flow
//import javax.inject.Inject
//import javax.inject.Singleton
//
//@Singleton
//class GroupChooserInteractor @Inject constructor(
//    private val groupRepository: GroupRepository,
//    specialtyRepository: SpecialtyRepository
//) {
//    private val selectedGroup = Channel<GroupHeader>()
//
//    suspend fun findGroupsBySpecialtyId(id: String): List<GroupHeader> {
//        return groupRepository.findBySpecialtyId(id)
//    }
//
//    val allSpecialties: Flow<List<Specialty>> = specialtyRepository.findAllSpecialties()
//
//    suspend fun findGroupInfoById(groupId: String) {
//        groupRepository.findById(groupId)
//    }
//
//    suspend fun receiveSelectedGroup(): GroupHeader {
//        return selectedGroup.receive()
//    }
//
//    suspend fun postSelectGroup(groupHeader: GroupHeader) {
//        selectedGroup.send(groupHeader)
//    }
//}