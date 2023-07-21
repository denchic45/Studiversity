package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.course.material.CourseMaterialApi
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseMaterialRepository(
    override val networkService: NetworkService,
    private val courseMaterialApi: CourseMaterialApi
) : NetworkServiceOwner {
    suspend fun add(
        courseId: UUID,
        request: CreateCourseMaterialRequest,
    ) = fetchResource {
        courseMaterialApi.create(courseId, request)
    }

    suspend fun update(
        courseId: UUID,
        materialId: UUID,
        request: UpdateCourseMaterialRequest,
    ) = fetchResource {
        courseMaterialApi.update(courseId, materialId, request)
    }

    suspend fun findById(courseId: UUID, materialId: UUID) = fetchResource {
        courseMaterialApi.getById(courseId, materialId)
    }
}