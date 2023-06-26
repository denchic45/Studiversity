package com.denchic45.studiversity.ui.schedule

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.data.repository.MetaRepository
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class ScheduleComponent(
    private val metaRepository: MetaRepository,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val schedule = metaRepository.observeBellSchedule.map(::resourceOf)
        .stateInResource(componentScope)
}