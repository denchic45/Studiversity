package com.denchic45.studiversity.ui.timetableeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.domain.timetable.model.PeriodDetails
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.studiversity.domain.timetable.model.PeriodSlot
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.denchic45.stuiversity.api.timetable.model.EventRequest
import com.denchic45.stuiversity.api.timetable.model.LessonRequest
import com.denchic45.stuiversity.api.timetable.model.PeriodRequest
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.DayOfWeek
import java.util.UUID

@Inject
class TimetableEditorComponent(
    @Assisted
    private val source: TimetableState,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    val editingTimetableState = MutableStateFlow(source)

    fun onAddPeriod(dayOfWeek: DayOfWeek, period: PeriodItem) {
        editingTimetableState.update { timetable ->
            timetable.addPeriod(dayOfWeek, period)
        }
    }

    fun onUpdatePeriod(dayOfWeek: DayOfWeek, position: Int, period: PeriodItem) {
        editingTimetableState.update { timetable ->
            timetable.updatePeriod(dayOfWeek, period, position)
        }
    }

    fun onRemovePeriod(dayOfWeek: DayOfWeek, position: Int) {
        editingTimetableState.update { timetable ->
            timetable.removePeriod(dayOfWeek, position)
        }
    }

    fun onDestroy() {}

    fun getRequestModel() = editingTimetableState.value.toPutTimetableRequest()

    private fun TimetableState.toPutTimetableRequest(): PutTimetableRequest {
        fun toRequests(slots: List<PeriodSlot>): List<PeriodRequest> {
            return slots.withIndex()
                .mapNotNull { (index, item) -> (item as? PeriodItem)?.let { index to it } }
                .map { (index, item) ->
                    when (item.details) {
                        is PeriodDetails.Lesson -> LessonRequest(
                            order = index + 1,
                            roomId = item.room?.id,
                            memberIds = item.members.map { it.id },
                            courseId = item.details.course.id
                        )


                        is PeriodDetails.Event -> EventRequest(
                            order = index + 1,
                            roomId = item.room?.id,
                            memberIds = item.members.map { it.id },
                            name = item.details.name,
                            color = item.details.color,
                            iconUrl = item.details.iconUrl
                        )
                    }
                }
        }

        return PutTimetableRequest(
            studyGroupId = studyGroupId,
            monday = toRequests(dayTimetables[0]),
            tuesday = toRequests(dayTimetables[1]),
            wednesday = toRequests(dayTimetables[2]),
            thursday = toRequests(dayTimetables[3]),
            friday = toRequests(dayTimetables[4]),
            saturday = toRequests(dayTimetables[5])
        )
    }

    fun onReset() {
        editingTimetableState.value = source
    }
}