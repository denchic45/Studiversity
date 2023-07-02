package com.denchic45.studiversity.ui.timetable

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.data.repository.MetaRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.FindTimetableOfWeekUseCase
import com.denchic45.studiversity.domain.usecase.TimetableOwner
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.denchic45.studiversity.ui.timetable.state.toTimetableState
import com.denchic45.studiversity.util.capitalized
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.toDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatterBuilder
import java.time.temporal.ChronoField
import java.util.*


@Inject
class TimetableComponent(
    metaRepository: MetaRepository,
    private val findTimetableOfWeekUseCase: FindTimetableOfWeekUseCase,
    @Assisted val selectedWeekOfYear: StateFlow<String>,
    @Assisted
    private val owner: Flow<TimetableOwner>,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val updateData = MutableStateFlow(true)
    val refreshing = MutableStateFlow(false)

    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily, replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _weekTimetable = owner.flatMapLatest { owner ->
        getTimetableResponse(owner)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private fun getTimetableResponse(owner: TimetableOwner): Flow<Resource<TimetableResponse>> {
        return selectedWeekOfYear.flatMapLatest { weekOfYear ->
            flow {
                println("Loading timetable")
                emit(resourceOf())
                println("Find timetable weekOfYear: $weekOfYear owner $owner")
                emit(findTimetableOfWeekUseCase(weekOfYear, owner))
            }
        }
    }

    val weekTimetable = _weekTimetable.stateInResource(componentScope)

    val timetableStateResource = getTimetableState().stateInResource(componentScope)

//    fun getTimetableState(
//        bellSchedule: Flow<BellSchedule>,
//        timetableResource: Flow<Resource<TimetableResponse>>,
//    ): StateFlow<Resource<TimetableState>> {
//        return getTimetableStateOfLists(
//            timetableResource.mapResource { it.days }
//        )
//    }

    init {
        componentScope.launch {
            updateData.emitAll(refreshing.drop(1))
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    fun getTimetableState(): StateFlow<Resource<TimetableState>> {
        return updateData.filter { it }
            .flatMapLatest {
                bellSchedule.flatMapLatest { schedule ->
                    selectedWeekOfYear.flatMapLatest { selectedWeek ->
                        owner.flatMapLatest { owner ->
                            getTimetableResponse(owner).mapResource {
                                updateData.value = false
                                refreshing.value = false

                                it.days.toTimetableState(
                                    yearWeek = selectedWeek,
                                    bellSchedule = schedule,
                                    showStudyGroups = owner !is TimetableOwner.StudyGroup
                                )
                            }
                        }
                    }
                }
            }.stateInResource(componentScope)
    }
}

private fun LocalDate.getMonthName(): String {
    return SimpleDateFormat("LLLL").format(toDate()).capitalized()
}

private fun LocalDate.getMonthWithYear(): String {
    return getMonthName() + year
}

fun getMonthTitle(yearWeek: String): String {
    val monday = LocalDate.parse(
        yearWeek, DateTimeFormatterBuilder()
            .appendPattern("YYYY_ww")
            .parseDefaulting(ChronoField.DAY_OF_WEEK, DayOfWeek.MONDAY.value.toLong())
            .toFormatter()
    )
    val saturday: LocalDate = monday.plusDays(5)
    return if (monday.monthValue != saturday.monthValue) {
        if (monday.year != saturday.year) {
            "${monday.getMonthWithYear()} - ${saturday.getMonthWithYear()}"
        } else {
            "${monday.getMonthName()} - ${saturday.getMonthName()}"
        }
    } else {
        monday.getMonthName()
    }
}