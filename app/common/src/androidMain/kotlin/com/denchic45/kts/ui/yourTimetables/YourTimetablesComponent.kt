package com.denchic45.kts.ui.yourTimetables

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.util.componentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate


@Inject
class YourTimetablesComponent(
    private val findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    private val _dayTimetableComponent: (LocalDate, Flow<TimetableOwner>, ComponentContext) -> DayTimetableComponent,
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
        LocalDate.now(),
        selectedOwner,
        componentContext.childContext("DayTimetable")
    )

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