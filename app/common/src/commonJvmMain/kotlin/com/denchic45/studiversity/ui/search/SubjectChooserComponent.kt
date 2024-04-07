package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.usecase.FindSubjectByContainsNameUseCase
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SubjectChooserComponent(
    private val findSubjectByContainsNameUseCase: FindSubjectByContainsNameUseCase,
    @Assisted
    override val onFinish: (SubjectResponse?) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : SearchableComponent<SubjectResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<SubjectResponse>>> {
        return findSubjectByContainsNameUseCase(query)
    }
}