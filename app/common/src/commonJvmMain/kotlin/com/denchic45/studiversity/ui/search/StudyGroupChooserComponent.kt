package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.domain.model.toItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.usecase.FindStudyGroupByContainsNameUseCase
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class StudyGroupChooserComponent(
    private val findStudyGroupByContainsNameUseCase: FindStudyGroupByContainsNameUseCase,
    @Assisted
    override val onSelect: (StudyGroupItem) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<StudyGroupItem>(componentContext) {

    override fun search(query: String): Flow<Resource<List<StudyGroupItem>>> {
        return findStudyGroupByContainsNameUseCase(query).mapResource { it.map(StudyGroupResponse::toItem) }
    }
}