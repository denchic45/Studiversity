package com.denchic45.kts.ui.yourTimetables

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.ui.timetable.TimetableOwnerComponent
import com.denchic45.kts.ui.timetable.TimetableOwnerDelegate
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class YourTimetablesComponent(
    metaRepository: MetaRepository,
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    _dayTimetableComponent: (
        StateFlow<String>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> DayTimetableComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, TimetableOwnerComponent by TimetableOwnerDelegate(componentContext) {

    init {
        println("COMPONENT $this")
    }


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

    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily)


    private val timetableComponent = _dayTimetableComponent(
        selectedWeekOfYear,
        selectedOwner,
        componentContext.childContext("DayTimetable")
    )

    val timetableState = getTimetableOfResponseState(
        bellSchedule,
        selectedWeekOfYear,
        timetableComponent.weekTimetable
    ).stateInResource(componentScope)


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