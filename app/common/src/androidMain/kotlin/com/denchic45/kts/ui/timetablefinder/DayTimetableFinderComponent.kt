package com.denchic45.kts.ui.timetablefinder

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
import com.denchic45.kts.domain.usecase.PutTimetableUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.timetable.state.toDayTimetableViewState
import com.denchic45.kts.ui.timetableeditor.DayTimetableEditorComponent
import com.denchic45.kts.util.asFlow
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.map
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.toString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID

@Inject
class DayTimetableFinderComponent(
    metaRepository: MetaRepository,
    private val putTimetableUseCase: PutTimetableUseCase,
    _dayTimetableComponent: (
        StateFlow<LocalDate>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> DayTimetableComponent,
    private val _dayTimetableEditorComponent: (
        timetable: TimetableResponse,
        studyGroupId: UUID,
        _selectedDate: StateFlow<LocalDate>,
        ComponentContext,
    ) -> DayTimetableEditorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val overlayNavigation = OverlayNavigation<Config>()
    private val childOverlay = childOverlay(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, componentContext ->
            childFactory(config, componentContext)
        }
    )

//    private val weekTimetable: StateFlow<Resource<TimetableResponse>> = MutableStateFlow(resourceOf())

//    init {
//        componentScope.launch {
//            childOverlay.asFlow().map { it.overlay }.filterNotNull().collect {
//                it.instance.component.editingWeekTimetable
//            }
//        }
//    }

    private fun childFactory(
        config: Config,
        componentContext: ComponentContext,
    ) = when (config) {
//        Config.Timetable -> Child.Timetable(
//            dayTimetableComponent(
//                selectedDate.value,
//                owner,
//                componentContext
//            )
//        )

        Config.Editor -> {
            Child.Editor(
                _dayTimetableEditorComponent(
                    dayTimetableComponent.weekTimetable.value.success().value,
                    owner.value!!.ownerId,
                    selectedDate,
                    componentContext
                )
            )
        }
    }

    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily)

//    private val isEdit = MutableStateFlow(false)

    val selectedDate = MutableStateFlow(LocalDate.now())
    private val selectedWeekOfYear = selectedDate.map(componentScope) {
        it.toString(DateTimePatterns.YYYY_ww)
    }
    private val owner = MutableStateFlow<TimetableOwner.StudyGroup?>(null)

    private val dayTimetableComponent = _dayTimetableComponent(
        selectedDate,
        owner.filterNotNull(),
        componentContext.childContext("DayTimetable")
    )

    private val actualTimetable = dayTimetableComponent.weekTimetable
        .shareIn(componentScope, SharingStarted.Lazily)

    @OptIn(ExperimentalCoroutinesApi::class)
    val dayViewState = bellSchedule.flatMapLatest { schedule ->
        childOverlay.asFlow().flatMapLatest { child ->
            selectedWeekOfYear.flatMapLatest { selectedWeek ->
                selectedDate.filter { it.toString(DateTimePatterns.YYYY_ww) == selectedWeek }
                    .flatMapLatest { selectedDate ->
                        dayViewStateFlow(selectedDate, child, schedule)
                    }
            }
        }
    }.stateInResource(componentScope)

    private fun dayViewStateFlow(
        selectedDate: LocalDate,
        child: ChildOverlay<Config, Child.Editor>,
        schedule: BellSchedule,
    ): Flow<Resource<DayTimetableViewState>> {
        val dayOfWeek = selectedDate.dayOfWeek.ordinal
        return if (dayOfWeek == 6) {
            flowOf(
                resourceOf(
                    emptyList<PeriodResponse>().toDayTimetableViewState(
                        selectedDate,
                        schedule,
                        child.overlay != null
                    )
                )
            )
        } else {
            child.overlay?.let {
                it.instance.component.editingWeekTimetable[dayOfWeek].map { periods ->
                    resourceOf(
                        periods.toDayTimetableViewState(
                            selectedDate,
                            schedule,
                            true
                        )
                    )
                }
            } ?: actualTimetable.mapResource {
                it.days[dayOfWeek].toDayTimetableViewState(selectedDate, schedule)
            }
        }
    }

//    private val dayTimetableEditorComponent = _dayTimetableEditorComponent(
//        dayTimetableComponent.weekTimetable.filterSuccess().map { it.value },
//    )


    fun onEditClick() {
//        editor.update {
//            DayTimetableEditor(
//                _weekTimetable = dayTimetableComponent.weekTimetable.value.success().value.days
//            )
//        }
        overlayNavigation.activate(Config.Editor)
    }

    private val dayTimetableEditorComponent
        get() = childOverlay.value.overlay?.instance?.component

    fun onSaveClick() {
        dayTimetableEditorComponent?.request?.let {
            componentScope().launch {
                putTimetableUseCase(
                    weekOfYear = dayTimetableComponent.selectedWeekOfYear.first(),
                    putTimetableRequest = it
                )
            }
        }
        onCloseEditorClick()
    }

    private fun onCloseEditorClick() {
        overlayNavigation.dismiss()
    }

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }

    @Parcelize
    sealed class Config : Parcelable {
        //        object Timetable : Config()
        object Editor : Config()
    }

    sealed class Child {
        //        class Timetable(val component: DayTimetableComponent) : Child()
        class Editor(val component: DayTimetableEditorComponent) : Child()
    }
}