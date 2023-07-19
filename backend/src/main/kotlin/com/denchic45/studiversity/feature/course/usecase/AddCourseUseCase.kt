package com.denchic45.studiversity.feature.course.usecase

import com.denchic45.studiversity.feature.course.CourseEvent
import com.denchic45.studiversity.feature.course.CoursesChannel
import com.denchic45.studiversity.feature.course.repository.CourseRepository
import com.denchic45.studiversity.feature.membership.repository.MembershipRepository
import com.denchic45.studiversity.feature.role.ScopeType
import com.denchic45.studiversity.feature.role.repository.ScopeRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.membership.model.CreateMembershipRequest
import java.util.*

class AddCourseUseCase(
    private val organizationId: UUID,
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseRepository: CourseRepository,
    private val scopeRepository: ScopeRepository,
    private val membershipRepository: MembershipRepository
) {
    suspend operator fun invoke(request: CreateCourseRequest): CourseResponse = suspendTransactionWorker {
        courseRepository.add(request).also { response ->
            scopeRepository.add(response.id, ScopeType.Course, organizationId)
            membershipRepository.addManualMembership(CreateMembershipRequest("manual", response.id))
        }.apply {
            CoursesChannel.emit(CourseEvent.CourseAdded(id))
        }
    }
}