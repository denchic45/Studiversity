package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.stuiversity.api.course.material.CourseMaterialApi
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import me.tatarka.inject.annotations.Inject
import java.util.*

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
        materialId: UUID,
        request: UpdateCourseMaterialRequest,
    ) = fetchResource {
        courseMaterialApi.update(materialId, request)
    }

    suspend fun findById(materialId: UUID) = fetchResource {
        courseMaterialApi.getById(materialId)
    }
}