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
import com.denchic45.stuiversity.api.course.topic.CourseTopicApi
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
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
        fetch = { courseTopicApi.getById(courseId, topicId) },
        saveFetch = { courseTopicLocalDataSource.upsert(it.toEntity(courseId)) }
    )

    fun observeByCourseId(courseId: UUID): Flow<Resource<List<TopicResponse>>> {
        return observeResource(
            query = courseTopicLocalDataSource.getByCourseId(courseId.toString())
                .map { it.toTopicResponses() },
            fetch = { courseTopicApi.getByCourseId(courseId) },
            saveFetch = { courseTopicLocalDataSource.upsert(it.toTopicEntities(courseId)) }
        )
    }

    suspend fun add(courseId: UUID, request: CreateTopicRequest) = fetchResource {
        courseTopicApi.create(courseId, request)
    }

    suspend fun update(
        courseId: UUID,
        topicId: UUID,
        request: UpdateTopicRequest,
    ) = fetchResource {
        courseTopicApi.update(courseId, topicId, request)
    }

    suspend fun remove(
        courseId: UUID,
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements,
    ) = fetchResource {
        courseTopicApi.delete(courseId, topicId, relatedTopicElements)
    }
}