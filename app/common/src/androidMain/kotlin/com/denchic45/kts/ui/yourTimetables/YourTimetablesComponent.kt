package com.denchic45.kts.ui.yourTimetables

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.timetable.state.toTimetableViewState
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.map
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate


@Inject
class YourTimetablesComponent(
    private val metaRepository: MetaRepository,
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    private val _dayTimetableComponent: (
        Flow<LocalDate>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> DayTimetableComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    init {
        println("COMPONENT $this")
    }

//    private val navigation = OverlayNavigation<TimetableConfig>()

//    val childOverlay = childOverlay(
//        source = navigation,
//        initialConfiguration = { TimetableConfig(LocalDate.now(),TimetableOwner.Member, null) },
//        childFactory = { config, _ ->
//            TimetableChild(
//                _dayTimetableComponent(config.date, config.owner, config.ownerId)
//            )
//        }
//    )

    fun onTimetableSelect(position: Int) {
        selectedTimetable.value = position
        if (position == -1) {
            selectedOwner.value = TimetableOwner.Member(null)
        } else {
            studyGroups.value.onSuccess {
                selectedOwner.value = TimetableOwner.StudyGroup(it[position].id)
            }
        }
    }

    private val componentScope = componentScope()

    val studyGroups = flow { emit(findYourStudyGroupsUseCase()) }.stateInResource(componentScope)
    val selectedTimetable = MutableStateFlow(-1)

    private val selectedOwner = MutableStateFlow<TimetableOwner>(TimetableOwner.Member(null))
     val selectedDate = MutableStateFlow(LocalDate.now())
    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily)

    private val selectedWeekOfYear = selectedDate.map(componentScope) {
        it.toString(DateTimePatterns.YYYY_ww)
    }

    val dayViewState = bellSchedule.flatMapLatest { schedule ->
        selectedWeekOfYear.flatMapLatest { selectedWeek ->
            selectedDate.filter { it.toString(DateTimePatterns.YYYY_ww) == selectedWeek }
                .flatMapLatest { selectedDate ->
                    dayViewStateFlow(selectedDate, schedule)
                }
        }
    }.stateInResource(componentScope)

    private fun dayViewStateFlow(
        selectedDate: LocalDate,
        schedule: BellSchedule,
    ): Flow<Resource<DayTimetableViewState>> {
        val dayOfWeek = selectedDate.dayOfWeek.ordinal
        return timetableComponent.weekTimetable.mapResource {
            it.days[dayOfWeek].toTimetableViewState(selectedDate, schedule, false)
        }
    }

//    val timetableComponent = studyGroups.flatMapResourceFlow { studyGroupResponses ->
//        selectedTimetable.map { selectedTimetable ->
//            Resource.Success(
//                if (selectedTimetable == -1) {
//                    _dayTimetableComponent(
//                        LocalDate.now(),
//                        TimetableOwner.Member,
//                        UUID.randomUUID()
//                    )
//                } else {
//                    _dayTimetableComponent(
//                        LocalDate.now(),
//                        TimetableOwner.StudyGroup,
//                        studyGroupResponses[selectedTimetable].id
//                    )
//                }
//            )
//        }
//    }.stateInResource(componentScope)

    val timetableComponent = _dayTimetableComponent(
        selectedDate,
        selectedOwner,
        componentContext.childContext("DayTimetable")
    )

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }

//    @Parcelize
//    data class TimetableConfig(
//        val date: LocalDate,
//        val owner: TimetableOwner,
//        val ownerId: UUID?,
//    ) : Parcelable

//    data class TimetableChild(
//        val component: DayTimetableComponent,
//    )

}