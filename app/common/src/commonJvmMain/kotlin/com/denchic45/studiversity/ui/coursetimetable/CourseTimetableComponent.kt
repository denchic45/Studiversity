package com.denchic45.studiversity.ui.coursetimetable

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.data.repository.MetaRepository
import com.denchic45.studiversity.domain.usecase.TimetableOwner
import com.denchic45.studiversity.ui.timetable.TimetableComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerDelegate
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
    _timetableComponent: (
        StateFlow<String>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> TimetableComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext,
    TimetableOwnerComponent by TimetableOwnerDelegate(componentContext) {
    private val timetableComponent = _timetableComponent(
        selectedWeekOfYear,
        flowOf(TimetableOwner.Course(courseId)),
        componentContext.childContext("timetable")
    )

    val timetable = getTimetableState(
        metaRepository.observeBellSchedule.shareIn(
            componentScope,
            SharingStarted.Lazily
        ),
        timetableComponent.weekTimetable
    )
}