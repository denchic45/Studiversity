package com.denchic45.kts.ui.studygroup.users


//class GroupUsersInteractor @Inject constructor(
//    private val userRepository: UserRepository,
//    private val studyGroupRepository: StudyGroupRepository,
//    private val studentRepository: StudentRepository
//) : Interactor {
//
//    suspend fun updateGroupCurator(groupId: String, teacherId: User) {
//        studyGroupRepository.updateGroupCurator(groupId, teacherId)
//    }
//
//    override fun removeListeners() {
//        studentRepository.removeListeners()
//        userRepository.removeListeners()
//        studyGroupRepository.removeListeners()
//    }
//
//    fun findThisUser(): User {
//        return userRepository.findSelf()
//    }
//}