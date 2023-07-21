package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.CourseTopicLocalDataSource
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.mapper.toEntity
import com.denchic45.studiversity.data.mapper.toResponse
import com.denchic45.studiversity.data.mapper.toTopicEntities
import com.denchic45.studiversity.data.mapper.toTopicResponses
import com.denchic45.studiversity.data.observeResource
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.stuiversity.api.course.topic.CourseTopicApi
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import com.denchic45.stuiversity.api.course.topic.model.CourseTopicResponse
import com.denchic45.stuiversity.api.course.topic.model.CreateCourseTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.UpdateCourseTopicRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class CourseTopicRepository(
    override val networkService: NetworkService,
    private val courseTopicApi: CourseTopicApi,
    private val courseTopicLocalDataSource: CourseTopicLocalDataSource,
) : NetworkServiceOwner {

    suspend fun findById(courseId: UUID, topicId: UUID) = observeResource(
        query = courseTopicLocalDataSource.observe(topicId.toString()).map { it.toResponse() },
        fetch = { courseTopicApi.getById(topicId) },
        saveFetch = { courseTopicLocalDataSource.upsert(it.toEntity()) }
    )

    fun observeByCourseId(courseId: UUID): Flow<Resource<List<CourseTopicResponse>>> {
        return observeResource(
            query = courseTopicLocalDataSource.getByCourseId(courseId.toString())
                .map { it.toTopicResponses() },
            fetch = { courseTopicApi.getList(courseId) },
            saveFetch = {
                courseTopicLocalDataSource.upsertByCourseId(
                    it.toTopicEntities(),
                    courseId.toString()
                )
            }
        )
    }

    suspend fun add(request: CreateCourseTopicRequest) = fetchResource {
        courseTopicApi.create(request)
    }.onSuccess {
        courseTopicLocalDataSource.upsert(it.toEntity())
    }

    suspend fun update(
        topicId: UUID,
        request: UpdateCourseTopicRequest,
    ) = fetchResource {
        courseTopicApi.update(topicId, request)
    }

    suspend fun remove(
        courseId: UUID,
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements,
    ) = fetchResource {
        courseTopicApi.delete(topicId, relatedTopicElements)
    }.onSuccess {
        courseTopicLocalDataSource.deleteById(topicId.toString())
    }
}