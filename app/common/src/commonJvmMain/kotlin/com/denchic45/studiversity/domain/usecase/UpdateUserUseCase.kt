//package com.denchic45.studiversity.domain.usecase
//
//import com.denchic45.studiversity.data.domain.model.UserRole
//import com.denchic45.studiversity.data.repository.StudentRepository
//import com.denchic45.studiversity.data.repository.TeacherRepository
//import com.denchic45.studiversity.data.repository.UserRepository
//import com.denchic45.studiversity.data.service.AuthService
//import com.denchic45.studiversity.data.service.AvatarService
//import com.denchic45.studiversity.data.service.NetworkService
//import com.denchic45.studiversity.domain.error.NetworkError
//import com.denchic45.studiversity.domain.model.User
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