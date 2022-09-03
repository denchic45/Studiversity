package com.denchic45.kts.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.usecase.FindEventsOfWeekByThisUserUseCase
import com.denchic45.kts.util.componentScope
import me.tatarka.inject.annotations.Inject

@Inject
class TimetableComponent(
    findEventsOfWeekByThisUserUseCase: FindEventsOfWeekByThisUserUseCase,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    val coroutineScope = componentScope()

    val timetable = findEventsOfWeekByThisUserUseCase()
}