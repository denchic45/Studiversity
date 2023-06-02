package com.denchic45.kts.ui.subjecticons

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindSubjectIconsUseCase
import com.denchic45.kts.util.componentScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SubjectIconsComponent(
    findSubjectIconsUseCase: FindSubjectIconsUseCase,
    @Assisted
    private val onSelect: (iconUrl: String) -> Unit,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    val componentScope = componentScope()
    val iconUrls = findSubjectIconsUseCase().stateInResource(componentScope)

    fun onSelectImage(url: String) {
        onSelect(url)
    }
}