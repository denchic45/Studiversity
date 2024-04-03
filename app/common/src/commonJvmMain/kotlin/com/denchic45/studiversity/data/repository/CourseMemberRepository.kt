package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.api.course.member.CourseMemberApi
import com.denchic45.studiversity.api.member.CreateMemberRequest
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.service.NetworkService
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseMemberRepository(
    override val networkService: NetworkService,
    private val courseMemberApi: CourseMemberApi,
) : NetworkServiceOwner {

    suspend fun findMembersByCourseId(courseId: UUID) = fetchResource {
        courseMemberApi.getByCourse(courseId)
    }

    suspend fun enroll(courseId: UUID, request: CreateMemberRequest) = fetchResource {
        courseMemberApi.enroll(courseId, request)
    }

    suspend fun removeMember(courseId: UUID, memberId: UUID) = fetchResource {
        courseMemberApi.delete(courseId, memberId)
    }
}