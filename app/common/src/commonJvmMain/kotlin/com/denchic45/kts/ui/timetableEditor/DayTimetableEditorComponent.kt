package com.denchic45.kts.ui.timetableEditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.ui.timetable.toLocalDateOfWeekOfYear
import com.denchic45.kts.ui.timetable.toTimetableViewState
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.copy
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class DayTimetableEditorComponent constructor(
    metaRepository: MetaRepository,
    @Assisted
    weekOfYear: String,
    @Assisted
    private val weekTimetableFlows: List<MutableStateFlow<List<PeriodResponse>>>,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val mondayDate = weekOfYear.toLocalDateOfWeekOfYear()

    private val selectedDay = MutableStateFlow(0)

    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily)

    @OptIn(FlowPreview::class)
    val viewState = combine(selectedDay, bellSchedule) { selected, schedule ->
        weekTimetableFlows[selected].map {
            it.toTimetableViewState(
                date = mondayDate.plusDays(selected.toLong()),
                bellSchedule = schedule
            )
        }
    }.flattenConcat().stateIn(componentScope, SharingStarted.Lazily, null)

    fun onAddPeriod(period: PeriodResponse) {
        weekTimetableFlows[selectedDay.value].update { it + period }
    }

    fun onUpdatePeriod(position: Int, period: PeriodResponse) {
        weekTimetableFlows[selectedDay.value].update {
            it.copy {
                this[position] = period
            }
        }
    }

    fun onRemovePeriod(position: Int) {
        weekTimetableFlows[selectedDay.value].update {
            it - it[position]
        }
    }
}