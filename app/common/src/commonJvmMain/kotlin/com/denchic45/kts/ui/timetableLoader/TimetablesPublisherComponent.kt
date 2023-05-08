package com.denchic45.kts.ui.timetableLoader

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.data.repository.MetaRepository
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.usecase.PutTimetableUseCase
import com.denchic45.kts.ui.chooser.StudyGroupChooserComponent
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.confirm.ConfirmState
import com.denchic45.kts.ui.periodeditor.EditingPeriod
import com.denchic45.kts.ui.periodeditor.EditingPeriodDetails
import com.denchic45.kts.ui.periodeditor.PeriodEditorComponent
import com.denchic45.kts.ui.timetable.state.DayTimetableViewState
import com.denchic45.kts.ui.timetable.state.toDayTimetableViewState
import com.denchic45.kts.ui.timetableeditor.DayTimetableEditorComponent
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.EventDetails
import com.denchic45.stuiversity.api.timetable.model.LessonDetails
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.api.timetable.model.toStudyGroupName
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID

@Inject
class TimetablesPublisherComponent(
    metaRepository: MetaRepository,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val studyGroupChooserComponent: (
        onFinish: (StudyGroupResponse?) -> Unit,
        ComponentContext
    ) -> StudyGroupChooserComponent,
    private val periodEditorComponent: (
        EditingPeriod,
        (PeriodResponse?) -> Unit,
        ComponentContext
    ) -> PeriodEditorComponent,
    private val putTimetableUseCase: PutTimetableUseCase,
    private val _dayTimetableEditorComponent: (
        timetable: TimetableResponse,
        studyGroupId: UUID,
        _selectedDate: StateFlow<LocalDate>,
        ComponentContext,
    ) -> DayTimetableEditorComponent,
    @Assisted
    private val weekOfYear: String,
    @Assisted
    _studyGroupTimetables: List<Pair<StudyGroupResponse, TimetableResponse>>,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val overlayNavigation = OverlayNavigation<OverlayConfig>()

    val childOverlay: Value<ChildOverlay<OverlayConfig, OverlayChild>> = childOverlay(
        handleBackButton = true,
        source = overlayNavigation,
        childFactory = { config, componentContext ->
            when (config) {
                OverlayConfig.GroupChooser -> OverlayChild.GroupChooser(
                    studyGroupChooserComponent(
                        {
                            overlayNavigation.dismiss()
                            it?.let(::onAddStudyGroup)
                        },
                        componentContext
                    )
                )

                is OverlayConfig.PeriodEditor -> OverlayChild.PeriodEditor(
                    periodEditorComponent(
                        config.periodConfig,
                        config.onFinish,
                        componentContext
                    )
                )
            }
        }
    )

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        object GroupChooser : OverlayConfig()

        data class PeriodEditor(
            val periodConfig: EditingPeriod,
            val onFinish: (PeriodResponse?) -> Unit
        ) : OverlayConfig()
    }

    sealed class OverlayChild {
        class GroupChooser(val component: StudyGroupChooserComponent) : OverlayChild()

        class PeriodEditor(val component: PeriodEditorComponent) : OverlayChild()
    }


    private val bellSchedule = metaRepository.observeBellSchedule

    val studyGroups = MutableStateFlow(_studyGroupTimetables.map { it.first })

    val selectedDate = MutableStateFlow(LocalDate.now())

    private val editorComponents = MutableStateFlow(
        _studyGroupTimetables.map {
            _dayTimetableEditorComponent(
                it.second,
                it.first.id,
                selectedDate,
                componentContext.childContext("DayTimetable ${it.first.id}")
            )
        }
    )

    val isEdit = MutableStateFlow(false)

    val publishState = MutableStateFlow(PublishState.PREPARATION)

    val timetablesViewStates: MutableStateFlow<List<StateFlow<DayTimetableViewState>>> =
        MutableStateFlow(emptyList())

    init {
        componentScope.launch {
            timetablesViewStates.value = editorComponents.value.map { getViewState(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getViewState(component: DayTimetableEditorComponent) =
        bellSchedule.flatMapLatest { schedule ->
            selectedDate.flatMapLatest { selectedDate ->
                isEdit.flatMapLatest { isEdit ->
                    val dayOfWeek = selectedDate.dayOfWeek.ordinal
                    val periodsFlow = if (dayOfWeek == 6) flowOf(emptyList())
                    else component.editingWeekTimetable[dayOfWeek]

                    periodsFlow.map {
                        it.toDayTimetableViewState(selectedDate, schedule, isEdit)
                    }
                }
            }
        }.stateIn(componentScope)

    val selectedGroup = MutableStateFlow(0)

    private fun onAddStudyGroup(studyGroupResponse: StudyGroupResponse) {
        studyGroups.update { it + studyGroupResponse }

//        val list = List(6) {
//            MutableStateFlow(listOf<PeriodResponse>())
//        }

        val dayTimetableEditorComponent = _dayTimetableEditorComponent(
            TimetableResponse(
                weekOfYear,
                listOf(),
                listOf(),
                listOf(),
                listOf(),
                listOf(),
                listOf()
            ),
            studyGroupResponse.id,
            selectedDate,
            componentContext.childContext("DayTimetable")
        )
        editorComponents.update { components ->
            components + dayTimetableEditorComponent
        }
        componentScope.launch {
            timetablesViewStates.update { it + getViewState(dayTimetableEditorComponent) }
        }

    }

    fun onRemoveStudyGroupClick(position: Int) {
        confirmDialogInteractor.set(
            ConfirmState(
                uiTextOf("Удалить группу"),
                uiTextOf("Расписание данной группы будет удалено")
            )
        )
        componentScope.launch {
            if (confirmDialogInteractor.receiveConfirm()) {
                if (selectedGroup.value == editorComponents.value.size - 1 && selectedGroup.value != 0) {
                    selectedGroup.update { it - 1 }
                }
                studyGroups.update { it - it[position] }
                editorComponents.update { it - it[position] }
                timetablesViewStates.update { it - it[position] }
            }
        }
    }

    fun onStudyGroupClick(position: Int) {
        selectedGroup.value = position
    }

    fun onDateSelect(date: LocalDate) {
        selectedDate.value = date
    }

    fun onEditEnableClick(edit: Boolean) {
        isEdit.value = edit
    }

    fun onAddPeriodClick(timetablePos: Int) {
        overlayNavigation.activate(OverlayConfig.PeriodEditor(EditingPeriod().apply {
            group = studyGroups.value[timetablePos].toStudyGroupName()
            date = selectedDate.value
            order = getCurrentSelectedDayTimetable(timetablePos)
                .lastOrNull()?.order?.let { it + 1 } ?: 1
        }
        ) { it?.let(editorComponent(timetablePos)::onAddPeriod) })
    }

    fun onEditPeriodClick(timetablePos: Int, periodPos: Int) {
        overlayNavigation.activate(OverlayConfig.PeriodEditor(
            EditingPeriod().apply {
                getCurrentSelectedDayTimetable(timetablePos)[periodPos].let { period ->
                    date = period.date
                    order = period.order
                    group = period.studyGroup
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
            it?.let { editorComponent(timetablePos).onUpdatePeriod(periodPos, it) }
        })
    }

    private fun getCurrentSelectedDayTimetable(timetablePos: Int): List<PeriodResponse> {
        return editorComponent(timetablePos).editingWeekTimetable[selectedDate.value.dayOfWeek.ordinal].value
    }

    private fun editorComponent(timetablePos: Int) =
        editorComponents.value[timetablePos]

    fun onPublishClick() {
        publishState.update { PublishState.SENDING }
        componentScope.launch {
            val result = editorComponents.value.map {
                async { putTimetableUseCase(weekOfYear, it.request) }
            }.awaitAll()

            publishState.update {
                if (result.all { it is Resource.Success }) PublishState.DONE
                else PublishState.FAILED
            }
        }
    }

    fun onStudyGroupChoose() {
        overlayNavigation.activate(OverlayConfig.GroupChooser)
    }

    enum class PublishState { PREPARATION, SENDING, DONE, FAILED }

}