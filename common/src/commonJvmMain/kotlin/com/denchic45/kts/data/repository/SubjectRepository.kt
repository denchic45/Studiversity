package com.denchic45.kts.data.repository

import com.denchic45.kts.SubjectEntity
import com.denchic45.kts.data.db.local.source.*
import com.denchic45.kts.data.db.remote.model.SubjectMap
import com.denchic45.kts.data.db.remote.source.SubjectRemoteDataSource
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.remote.SubjectRemoteStorage
import com.denchic45.kts.domain.model.Subject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class SubjectRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    override val networkService: NetworkService,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val sectionLocalDataSource: SectionLocalDataSource,
    override val groupCourseLocalDataSource: GroupCourseLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
    private val subjectRemoteDataSource: SubjectRemoteDataSource,
    private val subjectRemoteStorage: SubjectRemoteStorage,
) : Repository(), SaveCourseRepository, FindByContainsNameRepository<Subject> {

    suspend fun add(subject: Subject) {
        requireAllowWriteData()
        subjectRemoteDataSource.add(SubjectMap(subject.domainToMap()))
    }

    suspend fun update(subject: Subject) {
        requireAllowWriteData()
        subjectRemoteDataSource.update(SubjectMap(subject.domainToMap()))
    }

    suspend fun remove(subject: Subject) {
        requireAllowWriteData()
        subjectRemoteDataSource.remove(subject.domainToMap())
    }

    fun observe(id: String): Flow<Subject?> = callbackFlow {
        launch {
            subjectRemoteDataSource.observeById(id).collect {
                it?.let { saveSubject(it.mapToSubjectEntity()) }
                    ?: subjectLocalDataSource.delete(id)
            }
        }
        subjectLocalDataSource.observe(id)
            .distinctUntilChanged()
            .map { it?.entityToSubjectDomain() }
            .collect { send(it) }
    }

    suspend fun findAllRefsOfSubjectIcons(): List<String> {
        return subjectRemoteStorage.findAllRefsOfSubjectIcons()
    }

    private suspend fun saveSubject(subjectEntity: SubjectEntity) {
        subjectLocalDataSource.upsert(subjectEntity)
    }

    override fun findByContainsName(text: String): Flow<List<Subject>> {
        return subjectRemoteDataSource.findByContainsName(text)
            .map { subjectMaps ->
                subjectLocalDataSource.upsert(subjectMaps.mapsToSubjectEntities())
                subjectMaps.mapsToSubjectDomains()
            }
    }

    fun findByGroup(groupId: String): Flow<List<Subject>> = callbackFlow {
        launch {
            requireAllowWriteData()
            saveCourses(subjectRemoteDataSource.findByGroupId(groupId))
        }
        subjectLocalDataSource.observeByGroupId(groupId)
            .map { it.entitiesToSubjectDomains() }
            .collect { send(it) }
    }
}