package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.usecase.FindSpecialtyByContainsNameUseCase
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SpecialtyChooserComponent(
    private val findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
    @Assisted
    override val onSelect: (SpecialtyResponse) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<SpecialtyResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<SpecialtyResponse>>> {
        return findSpecialtyByContainsNameUseCase(query)
    }
}