package com.denchic45.kts.ui.ownTimetables

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.flatMapResourceFlow
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.*

@Inject
class OwnTimetablesComponent(
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    private val _dayTimetableComponent: (LocalDate, TimetableOwner, UUID?) -> DayTimetableComponent,
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    fun onTimetableSelect(position: Int) {
        selectedTimetable.value = position
    }

    private val componentScope = componentScope()

    val studyGroups = flow { emit(findYourStudyGroupsUseCase()) }.stateInResource(componentScope)
    private val selectedTimetable = MutableStateFlow(-1)
    val timetableComponent = studyGroups.flatMapResourceFlow { studyGroupResponses ->
        selectedTimetable.map { selectedTimetable ->
            Resource.Success(
                if (selectedTimetable == -1) {
                    _dayTimetableComponent(LocalDate.now(), TimetableOwner.Member, UUID.randomUUID())
                } else {
                    _dayTimetableComponent(
                        LocalDate.now(),
                        TimetableOwner.StudyGroup,
                        studyGroupResponses[selectedTimetable].id
                    )
                }
            )
        }
    }.stateInResource(componentScope)

}