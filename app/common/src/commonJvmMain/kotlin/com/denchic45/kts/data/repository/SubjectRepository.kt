package com.denchic45.kts.data.repository

import com.denchic45.kts.data.db.local.source.*
import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.remote.SubjectRemoteStorage
import com.denchic45.stuiversity.api.course.subject.SubjectApi
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SubjectRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    override val networkService: NetworkService,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val sectionLocalDataSource: SectionLocalDataSource,
    override val groupCourseLocalDataSource: GroupCourseLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
    private val subjectApi: SubjectApi,
) : Repository(), NetworkServiceOwner, SaveCourseRepository,
    FindByContainsNameRepository<SubjectResponse> {

    override suspend fun findByContainsName(text: String) = fetchResource {
        subjectApi.search(text)
    }

    suspend fun add(createSubjectRequest: CreateSubjectRequest) = fetchResource {
        subjectApi.create(createSubjectRequest)
    }

    suspend fun update(
        subjectId: UUID,
        updateSubjectRequest: UpdateSubjectRequest,
    ) = fetchResource {
        subjectApi.update(subjectId, updateSubjectRequest)
    }

    suspend fun remove(subjectId: UUID) = fetchResource {
        subjectApi.delete(subjectId)
    }

    suspend fun findById(subjectId: UUID) = fetchResource {
        subjectApi.getById(subjectId)
    }
}