package com.denchic45.kts.ui.studygroup.timetable

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.TimetableComponent
import com.denchic45.kts.ui.timetable.TimetableOwnerComponent
import com.denchic45.kts.ui.timetable.TimetableOwnerDelegate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupTimetableComponent(
    metaRepository: MetaRepository,
    _timetableComponent: (
        StateFlow<String>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> TimetableComponent,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    TimetableOwnerComponent by TimetableOwnerDelegate(componentContext) {

    private val timetableComponent = _timetableComponent(
        selectedWeekOfYear,
        flowOf(TimetableOwner.StudyGroup(studyGroupId)),
        componentContext.childContext("Timetable")
    )

    val timetableState = getTimetableState(
        bellSchedule = metaRepository.observeBellSchedule,
        timetableResource = timetableComponent.weekTimetable
    )
}