package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.CourseLocalDataSource
import com.denchic45.studiversity.data.db.local.source.CourseTopicLocalDataSource
import com.denchic45.studiversity.data.db.local.source.StudyGroupCourseLocalDataSource
import com.denchic45.studiversity.data.db.local.source.StudyGroupLocalDataSource
import com.denchic45.studiversity.data.db.local.source.SubjectLocalDataSource
import com.denchic45.studiversity.data.db.local.source.UserLocalDataSource
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.course.subject.SubjectApi
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SubjectRepository(
    override val networkService: NetworkService,
    override val userLocalDataSource: UserLocalDataSource,
    override val studyGroupLocalDataSource: StudyGroupLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val courseTopicLocalDataSource: CourseTopicLocalDataSource,
    override val studyGroupCourseLocalDataSource: StudyGroupCourseLocalDataSource,
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