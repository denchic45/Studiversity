package com.denchic45.studiversity.ui.studygroup.timetable

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.data.repository.MetaRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.usecase.TimetableOwner
import com.denchic45.studiversity.ui.timetable.TimetableComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerDelegate
import com.denchic45.studiversity.ui.timetable.state.TimetableState
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

    val timetableState: StateFlow<Resource<TimetableState>> = getTimetableState(
        bellSchedule = metaRepository.observeBellSchedule,
        timetableResource = timetableComponent.weekTimetable
    )
}