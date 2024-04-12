package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.api.course.member.CourseMemberApi
import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.service.NetworkService
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseMemberRepository(
    override val networkService: NetworkService,
    private val courseMemberApi: CourseMemberApi,
) : NetworkServiceOwner {

    fun findMembersByCourseId(courseId: UUID) = fetchResourceFlow {
        courseMemberApi.getByCourse(courseId)
    }

    suspend fun enrol(courseId: UUID, request: CreateMemberRequest) = fetchResource {
        courseMemberApi.enroll(courseId, request)
    }

    suspend fun remove(courseId: UUID, memberId: UUID) = fetchResource {
        courseMemberApi.delete(courseId, memberId)
    }
}