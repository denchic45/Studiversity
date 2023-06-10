package com.denchic45.studiversity.ui.subjectEditor

import com.denchic45.studiversity.data.repository.SubjectRepository
import com.denchic45.studiversity.domain.EmptyResource
import com.denchic45.studiversity.domain.Interactor
import com.denchic45.studiversity.domain.Resource
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

class SubjectEditorInteractor @Inject constructor(
    private val subjectRepository: SubjectRepository
) : Interactor {

    suspend fun add(request: CreateSubjectRequest): Resource<SubjectResponse> {
        return subjectRepository.add(request)
    }

    suspend fun update(subjectId: UUID, request: UpdateSubjectRequest): Resource<SubjectResponse> {
        return subjectRepository.update(subjectId, request)
    }

    suspend fun remove(subjectId: UUID): EmptyResource {
        return subjectRepository.remove(subjectId)
    }


    fun find(id: UUID): Flow<Resource<SubjectResponse>> {
        return subjectRepository.findById(id)
    }

    override fun removeListeners() {}
}