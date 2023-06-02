package com.denchic45.kts.data.repository

import com.denchic45.kts.data.db.local.source.CourseLocalDataSource
import com.denchic45.kts.data.db.local.source.GroupCourseLocalDataSource
import com.denchic45.kts.data.db.local.source.GroupLocalDataSource
import com.denchic45.kts.data.db.local.source.SectionLocalDataSource
import com.denchic45.kts.data.db.local.source.SubjectLocalDataSource
import com.denchic45.kts.data.db.local.source.UserLocalDataSource
import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.fetchResourceFlow
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.stuiversity.api.course.subject.SubjectApi
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import java.util.UUID
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class SubjectRepository @Inject constructor(
    override val networkService: NetworkService,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val sectionLocalDataSource: SectionLocalDataSource,
    override val groupCourseLocalDataSource: GroupCourseLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
    private val subjectApi: SubjectApi,
) : NetworkServiceOwner, SaveCourseRepository,
    FindByContainsNameRepository<SubjectResponse> {

    override fun findByContainsName(text: String) = fetchResourceFlow {
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

    fun findById(subjectId: UUID) = fetchResourceFlow {
        subjectApi.getById(subjectId)
    }

    fun findIcons() = fetchResourceFlow {
        subjectApi.getIconsUrls()
    }
}