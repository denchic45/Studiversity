package com.denchic45.kts.ui.chooser

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.usecase.FindSubjectByContainsNameUseCase
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SubjectChooserComponent(
    private val findSubjectByContainsNameUseCase: FindSubjectByContainsNameUseCase,
    @Assisted
    override val onSelect: (SubjectResponse) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<SubjectResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<SubjectResponse>>> {
        return findSubjectByContainsNameUseCase(query)
    }
}