package com.denchic45.studiversity.ui.yourtimetables

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.data.pref.AppPreferences
import com.denchic45.studiversity.data.repository.MetaRepository
import com.denchic45.studiversity.domain.map
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.studiversity.domain.usecase.TimetableOwner
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.ui.timetable.TimetableComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerDelegate
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject


@Inject
class YourTimetablesComponent(
    metaRepository: MetaRepository,
    private val appPreferences: AppPreferences,
    findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    _TimetableComponent: (
        StateFlow<String>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> TimetableComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    EmptyChildrenContainer,
    TimetableOwnerComponent by TimetableOwnerDelegate(componentContext) {

    fun onTimetableSelect(position: Int) {
//        selectedTimetable.value = position
        studyGroups.value.onSuccess { groups ->
            appPreferences.selectedStudyGroupTimetableId = if (position == -1) null
            else groups[position].id.toString()
        }
    }


    val studyGroups = findYourStudyGroupsUseCase().stateInResource(componentScope)
    val selectedTimetablePosition = combine(
        studyGroups,
        appPreferences.selectedStudyGroupTimetableIdFlow
    ) { groups, selectedStudyGroupId ->
        selectedStudyGroupId?.toUUID()?.let { studyGroupId ->
            groups.map { groups ->
                groups.indexOfFirst { it.id == studyGroupId }
                    .also { index ->
                        if (index == -1)
                            appPreferences.selectedStudyGroupTimetableId = null
                    }
            }
        } ?: resourceOf(-1)
    }.stateInResource(componentScope)

    private val selectedOwner = appPreferences.selectedStudyGroupTimetableIdFlow.map { id ->
        id?.let { TimetableOwner.StudyGroup(id.toUUID()) }
            ?: TimetableOwner.Member(null)
    }

//        MutableStateFlow<TimetableOwner>(TimetableOwner.Member(null))

    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily)


    private val timetableComponent = _TimetableComponent(
        selectedWeekOfYear,
        selectedOwner,
        componentContext.childContext("DayTimetable")
    )

    val timetableState = getTimetableState(
        bellSchedule,
        timetableComponent.weekTimetable
    )
}