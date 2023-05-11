package com.denchic45.kts.ui.coursetimetable

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.ui.timetable.TimetableOwnerComponent
import com.denchic45.kts.ui.timetable.TimetableOwnerDelegate
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseTimetableComponent(
    metaRepository: MetaRepository,
    _dayTimetableComponent: (
        StateFlow<String>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> DayTimetableComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    TimetableOwnerComponent by TimetableOwnerDelegate(componentContext) {
    private val componentScope = componentScope()
    private val timetableComponent = _dayTimetableComponent(
        selectedWeekOfYear,
        flowOf(TimetableOwner.Course(courseId)),
        componentContext.childContext("Timetable")
    )

    val timetable = getTimetableOfResponseState(
        bellSchedule = metaRepository.observeBellSchedule.shareIn(
            componentScope,
            SharingStarted.Lazily
        ),
        selectedWeekOfYear,
        timetableComponent.weekTimetable
    ).stateInResource(componentScope)
}