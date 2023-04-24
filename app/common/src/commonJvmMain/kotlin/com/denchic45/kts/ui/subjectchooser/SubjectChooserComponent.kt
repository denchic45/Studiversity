package com.denchic45.kts.ui.subjectchooser

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.usecase.FindSubjectByContainsNameUseCase
import com.denchic45.kts.ui.chooser.ChooserComponent
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SubjectChooserComponent(
    private val findSubjectByContainsNameUseCase: FindSubjectByContainsNameUseCase,
    @Assisted
    override val onFinish: (SubjectResponse?) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<SubjectResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<SubjectResponse>>> {
        return flow { emit(findSubjectByContainsNameUseCase(query)) }
    }
}