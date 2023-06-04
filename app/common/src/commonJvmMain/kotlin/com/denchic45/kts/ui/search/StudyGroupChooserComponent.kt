package com.denchic45.kts.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.usecase.FindStudyGroupByContainsNameUseCase
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class StudyGroupChooserComponent(
    private val findStudyGroupByContainsNameUseCase: FindStudyGroupByContainsNameUseCase,
    @Assisted
    override val onSelect: (StudyGroupResponse) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<StudyGroupResponse>(componentContext) {

    override fun search(query: String): Flow<Resource<List<StudyGroupResponse>>> {
        return findStudyGroupByContainsNameUseCase(query)
    }
}