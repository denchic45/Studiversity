package com.denchic45.studiversity.feature.course.work.usecase

import com.denchic45.studiversity.feature.course.work.CourseWorkRepository
import com.denchic45.studiversity.transaction.SuspendTransactionWorker
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import java.util.*

class AddCourseWorkUseCase(
    private val suspendTransactionWorker: SuspendTransactionWorker,
    private val courseWorkRepository: CourseWorkRepository,
) {
    suspend operator fun invoke(
        courseId: UUID,
        request: CreateCourseWorkRequest
    ): CourseWorkResponse = suspendTransactionWorker {
        val workId = courseWorkRepository.add(courseId, request)
        courseWorkRepository.findWorkById(workId)
    }
}