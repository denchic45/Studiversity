//package com.denchic45.studiversity.data.repository
//
//import com.denchic45.studiversity.data.db.remote.source.UserRemoteDataSource
//import com.denchic45.studiversity.data.mapper.mapsToUsers
//import com.denchic45.studiversity.data.mapper.toMap
//import com.denchic45.studiversity.data.service.AppVersionService
//import com.denchic45.studiversity.data.service.NetworkService
//import com.denchic45.studiversity.data.storage.remote.UserRemoteStorage
//import com.denchic45.studiversity.domain.error.SearchError
//import com.denchic45.studiversity.domain.model.User
//import com.github.michaelbull.result.Result
//import kotlinx.coroutines.flow.Flow
//import kotlinx.coroutines.flow.map
//import javax.inject.Inject
//
//@me.tatarka.inject.annotations.Inject
//class TeacherRepository @Inject constructor(
//    override val appVersionService: AppVersionService,
//    override val networkService: NetworkService,
//    private val userRemoteDataSource: UserRemoteDataSource,
//    private val userRemoteStorage: UserRemoteStorage
//) : Repository(), FindByContainsNameRepository<User>, FindByContainsName3Repository<User> {
//
//    override fun findByContainsName(text: String): Flow<List<User>> {
//        requireNetworkAvailable()
//        return userRemoteDataSource.findTeachersByContainsName(text)
//            .map { it.mapsToUsers() }
//    }
//
//    override suspend fun findByContainsName3(text: String): Flow<Result<List<User>, SearchError>> {
//        return observeByContainsName {
//            userRemoteDataSource.findTeachersByContainsName(text)
//                .map { it.mapsToUsers() }
//        }
//    }
//
//    suspend fun add(teacher: User) {
//        userRemoteDataSource.addTeacher(teacher.toMap())
//    }
//
//    suspend fun update(teacher: User) {
//        userRemoteDataSource.updateTeacher(teacher.toMap())
//    }
//
//    suspend fun remove(teacher: User) {
//        requireAllowWriteData()
//        userRemoteDataSource.removeTeacher(teacher.toMap())
//        userRemoteStorage.deleteAvatar(teacher.id)
//    }
//}