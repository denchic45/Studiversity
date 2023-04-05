package com.denchic45.kts.ui.courseEditor

import com.denchic45.kts.data.repository.*
import com.denchic45.kts.domain.EmptyResource
import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.domain.Resource
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import java.util.*
import javax.inject.Inject

class CourseEditorInteractor @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val studyGroupRepository: StudyGroupRepository,
    private val courseRepository: CourseRepository,
) : Interactor {

    suspend fun findSubjectByTypedName(name: String): Resource<List<SubjectResponse>> {
        return subjectRepository.findByContainsName(name)
    }

    suspend fun findById(courseId: UUID): Resource<CourseResponse> {
        return courseRepository.findById(courseId)
    }

    suspend fun addCourse(createCourseRequest: CreateCourseRequest): Resource<CourseResponse> {
        return courseRepository.add(createCourseRequest)
    }

    suspend fun updateCourse(
        courseId: UUID,
        updateCourseRequest: UpdateCourseRequest,
    ): Resource<CourseResponse> {
        return courseRepository.updateCourse(courseId, updateCourseRequest)
    }

    suspend fun removeCourse(courseId: UUID): EmptyResource {
        return courseRepository.removeCourse(courseId)
    }

    override fun removeListeners() {
        courseRepository.removeListeners()
        subjectRepository.removeListeners()
    }
}