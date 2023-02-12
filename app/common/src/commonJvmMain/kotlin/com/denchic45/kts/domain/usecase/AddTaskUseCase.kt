package com.denchic45.kts.domain.usecase

import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.repository.CourseRepository
import com.denchic45.kts.domain.model.Task
import javax.inject.Inject

class AddTaskUseCase @Inject constructor(
    private val courseRepository: CourseRepository,
) {
    suspend operator fun invoke(task: Task, attachments: List<Attachment>) {
        courseRepository.addTask(task, attachments)
    }

}