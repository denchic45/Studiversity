package com.denchic45.studiversity.ui.profile


//class ProfileInteractor @Inject constructor(
//    private val userRepository: UserRepository,
//    private val studentRepository: StudentRepository,
//    private val teacherRepository: TeacherRepository,
//    private val studyGroupRepository: StudyGroupRepository
//) : Interactor {
//
//    fun observe(id: String): Flow<User?> {
//        return userRepository.observeById(id)
//    }
//
//    fun findThisUser(): User {
//        return userRepository.findSelf()
//    }
//
//    fun findGroupByStudent(user: User): Flow<Group> {
//        return studyGroupRepository.findGroupByStudent(user)
//    }
//
//    fun findGroupByCurator(user: User): Flow<Group?> {
//        return studyGroupRepository.findGroupByCuratorId(user.id)
//    }
//
//    override fun removeListeners() {
//        userRepository.removeListeners()
//        studyGroupRepository.removeListeners()
//    }
//
//    suspend fun updateAvatar(user: User, imageBytes: ByteArray) {
//        val photoUrl = userRepository.updateUserAvatar(imageBytes, user.id)
//        val updatedUser = user.copy(photoUrl = photoUrl, generatedAvatar = false)
//        when {
//            updatedUser.isStudent -> {
//                studentRepository.update(updatedUser)
//            }
//            updatedUser.isTeacher -> {
//                teacherRepository.update(updatedUser)
//            }
//        }
//    }
//}