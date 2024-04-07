package com.denchic45.studiversity.ui.search

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.usecase.FindSpecialtyByContainsNameUseCase
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SpecialtyChooserComponent(
    private val findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
    @Assisted
    override val onFinish: (SpecialtyResponse?) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : SearchableComponent<SpecialtyResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<SpecialtyResponse>>> {
        return findSpecialtyByContainsNameUseCase(query)
    }
}