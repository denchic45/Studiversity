package com.denchic45.kts.ui.ownTimetables

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject

@Inject
class OwnTimetablesComponent(
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val studyGroups = flow { emit(findYourStudyGroupsUseCase()) }.stateInResource(componentScope)


}