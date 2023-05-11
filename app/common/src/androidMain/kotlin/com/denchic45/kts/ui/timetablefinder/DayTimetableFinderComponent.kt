package com.denchic45.kts.ui.timetablefinder

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.data.service.model.BellSchedule
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.success
import com.denchic45.kts.domain.usecase.FindStudyGroupByContainsNameUseCase
import com.denchic45.kts.domain.usecase.PutTimetableUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.periodeditor.EditingPeriod
import com.denchic45.kts.ui.periodeditor.EditingPeriodDetails
import com.denchic45.kts.ui.periodeditor.PeriodEditorComponent
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.ui.timetable.TimetableOwnerComponent
import com.denchic45.kts.ui.timetable.TimetableOwnerDelegate
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.timetable.state.TimetableState
import com.denchic45.kts.ui.timetable.state.toTimetableState
import com.denchic45.kts.ui.timetableeditor.DayTimetableEditorComponent
import com.denchic45.kts.util.asFlow
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.EventDetails
import com.denchic45.stuiversity.api.timetable.model.LessonDetails
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.api.timetable.model.toStudyGroupName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID

@Inject
class DayTimetableFinderComponent(
    metaRepository: MetaRepository,
    private val putTimetableUseCase: PutTimetableUseCase,
    private val findStudyGroupByContainsNameUseCase: FindStudyGroupByContainsNameUseCase,
    _dayTimetableComponent: (
        StateFlow<String>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> DayTimetableComponent,
    private val _dayTimetableEditorComponent: (
        timetable: TimetableResponse,
        studyGroupId: UUID,
        _selectedDate: StateFlow<LocalDate>,
        ComponentContext,
    ) -> DayTimetableEditorComponent,
    private val periodEditorComponent: (
        EditingPeriod,
        (PeriodResponse?) -> Unit,
        ComponentContext,
    ) -> PeriodEditorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    TimetableOwnerComponent by TimetableOwnerDelegate(componentContext) {

    private val componentScope = componentScope()

    private val timetableEditorNavigation = OverlayNavigation<TimetableEditorConfig>()
    private val timetableEditorOverlay = childOverlay(
        source = timetableEditorNavigation,
        handleBackButton = true,
        childFactory = { _, componentContext ->
            TimetableEditorChild(
                _dayTimetableEditorComponent(
                    dayTimetableComponent.weekTimetable.value.success().value,
                    owner.value!!.ownerId,
                    selectedDate,
                    componentContext
                )
            )
        }
    )

    private val overlayNavigation = OverlayNavigation<OverlayConfig>()
    val childOverlay = childOverlay<OverlayConfig, OverlayChild>(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, componentContext ->
            when (config) {
                is OverlayConfig.PeriodEditor -> OverlayChild.PeriodEditor(
                    periodEditorComponent(
                        config.periodConfig,
                        {
                            overlayNavigation.dismiss()
                            config.onFinish(it)
                        },
                        componentContext
                    )
                )
            }
        }
    )


    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily)

    private val owner = MutableStateFlow<TimetableOwner.StudyGroup?>(null)

    private val dayTimetableComponent = _dayTimetableComponent(
        selectedWeekOfYear,
        owner.filterNotNull(),
        componentContext.childContext("DayTimetable")
    )

    private val actualTimetable = dayTimetableComponent.weekTimetable
        .shareIn(componentScope, SharingStarted.Lazily)

    @OptIn(ExperimentalCoroutinesApi::class)
    val timetable = bellSchedule.flatMapLatest { schedule ->
        timetableEditorOverlay.asFlow().flatMapLatest { child ->
            selectedWeekOfYear.flatMapLatest { selectedWeek ->
                timetableFlow(selectedWeek, child, schedule)
            }
        }
    }.stateInResource(componentScope)

    private fun timetableFlow(
        yearWeek: String,
        child: ChildOverlay<TimetableEditorConfig, TimetableEditorChild>,
        schedule: BellSchedule,
    ): Flow<Resource<TimetableState>> = child.overlay?.let {
        it.instance.component.editingWeekTimetable.map { periods ->
            resourceOf(
                periods.toTimetableState(
                    yearWeek,
                    schedule,
                    true
                )
            )
        }
    } ?: actualTimetable.mapResource {
        it.days.toTimetableState(yearWeek, schedule)
    }

    val state = TimetableFinderState()

    private val query = MutableStateFlow("")

    private val foundGroups = query.map {
        findStudyGroupByContainsNameUseCase(it)
    }

    init {
        componentScope.launch {
            foundGroups.collect {
                state.foundGroups = it
            }
        }
    }

    fun onEditClick() {
        timetableEditorNavigation.activate(TimetableEditorConfig)
    }

    private val dayTimetableEditorComponent
        get() = timetableEditorOverlay.value.overlay?.instance?.component

    fun onSaveChangesClick() {
        dayTimetableEditorComponent?.request?.let {
            componentScope().launch {
                putTimetableUseCase(
                    weekOfYear = dayTimetableComponent.selectedWeekOfYear.first(),
                    putTimetableRequest = it
                )
            }
        }
        timetableEditorNavigation.dismiss()
    }

    fun onQueryType(queryText: String) {
        state.query = queryText
        query.update { queryText }
    }

    fun onGroupSelect(response: StudyGroupResponse) {
        state.selectedStudyGroup = response
        owner.update { TimetableOwner.StudyGroup(response.id) }
    }

    fun onAddPeriodClick() {
        val group = state.selectedStudyGroup!!.toStudyGroupName()
        overlayNavigation.activate(
            OverlayConfig.PeriodEditor(
                EditingPeriod(
                    selectedDate.value,
                    group.id,
                    group.name
                ).apply {
                    order =
                        dayTimetableEditorComponent!!.editingWeekTimetable.value[selectedDate.value.dayOfWeek.ordinal]
                            .lastOrNull()?.order?.let { it + 1 } ?: 1
                }
            ) { it?.let(dayTimetableEditorComponent!!::onAddPeriod) })
    }

    fun onEditPeriodClick(periodPosition: Int) {
        overlayNavigation.activate(
            OverlayConfig.PeriodEditor(
                dayTimetableEditorComponent!!.editingWeekTimetable.value[selectedDate.value.dayOfWeek.ordinal][periodPosition].let { period ->
                    val group = state.selectedStudyGroup!!.toStudyGroupName()
                    EditingPeriod(period.date, group.id, group.name).apply {
                        order = period.order
                        room = period.room
                        members = period.members
                        details = when (val details = period.details) {
                            is EventDetails -> EditingPeriodDetails.Event().apply {
                                name = details.name
                                color = details.color
                                iconUrl = details.iconUrl
                            }

                            is LessonDetails -> EditingPeriodDetails.Lesson().apply {
                                course = details.course
                            }
                        }
                    }
                }
            ) {
                it?.let { dayTimetableEditorComponent!!.onUpdatePeriod(periodPosition, it) }
            })
    }

    fun onRemovePeriodSwipe(periodPosition: Int) {
        dayTimetableEditorComponent!!.onPeriodRemove(periodPosition)
    }

//    fun onDateSelect(date: LocalDate) {
//        selectedDate.value = date
//    }

    @Parcelize
    object TimetableEditorConfig : Parcelable

    class TimetableEditorChild(val component: DayTimetableEditorComponent)

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class PeriodEditor(
            val periodConfig: EditingPeriod,
            val onFinish: (PeriodResponse?) -> Unit,
        ) : OverlayConfig()
    }

    sealed class OverlayChild {
        class PeriodEditor(val component: PeriodEditorComponent) : OverlayChild()
    }

}

class TimetableFinderState {
    var query by mutableStateOf("")
    var foundGroups: Resource<List<StudyGroupResponse>> by mutableStateOf(resourceOf(emptyList()))
    var selectedStudyGroup by mutableStateOf<StudyGroupResponse?>(null)
    var timetable by mutableStateOf<DayTimetableViewState?>(null)
}