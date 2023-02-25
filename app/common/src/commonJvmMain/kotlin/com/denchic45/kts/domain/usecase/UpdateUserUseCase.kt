//package com.denchic45.kts.domain.usecase
//
//import com.denchic45.kts.data.domain.model.UserRole
//import com.denchic45.kts.data.repository.StudentRepository
//import com.denchic45.kts.data.repository.TeacherRepository
//import com.denchic45.kts.data.repository.UserRepository
//import com.denchic45.kts.data.service.AuthService
//import com.denchic45.kts.data.service.AvatarService
//import com.denchic45.kts.data.service.NetworkService
//import com.denchic45.kts.domain.error.NetworkError
//import com.denchic45.kts.domain.model.User
//import com.github.michaelbull.result.Err
//import com.github.michaelbull.result.Ok
//import com.github.michaelbull.result.Result
//import me.tatarka.inject.annotations.Inject
//
//@Inject
//class UpdateUserUseCase(
//    private val userRepository: UserRepository,
//    private val studentRepository: StudentRepository,
//    private val teacherRepository: TeacherRepository,
//) {
//
//    suspend operator fun invoke(updateuser: User): Result<User, NetworkError> {
//        return if (!networkService.isNetworkAvailable) {
//            Err(NetworkError)
//        } else {
//            val photoUrl = createAvatar(user)
//            val updatedUser = user.copy(photoUrl = photoUrl)
//            when (updatedUser.role) {
//                UserRole.STUDENT -> studentRepository.update(updatedUser)
//                UserRole.TEACHER, UserRole.HEAD_TEACHER -> teacherRepository.update(updatedUser)
//            }
//            Ok(updatedUser)
//        }
//    }
//}