package com.denchic45.studiversity.ui.timetableLoader

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
import com.denchic45.studiversity.data.repository.MetaRepository
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.usecase.PutTimetableUseCase
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.periodeditor.EditingPeriod
import com.denchic45.studiversity.ui.periodeditor.EditingPeriodDetails
import com.denchic45.studiversity.ui.periodeditor.PeriodEditorComponent
import com.denchic45.studiversity.ui.search.StudyGroupChooserComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerDelegate
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.denchic45.studiversity.ui.timetable.state.toLocalDateOfWeekOfYear
import com.denchic45.studiversity.ui.timetable.state.toTimetableState
import com.denchic45.studiversity.ui.timetableeditor.DayTimetableEditorComponent
import com.denchic45.studiversity.ui.uiTextOf
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
            (StudyGroupResponse) -> Unit,
            ComponentContext,
    ) -> StudyGroupChooserComponent,
    private val periodEditorComponent: (
        EditingPeriod,
        (PeriodResponse?) -> Unit,
        ComponentContext,
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
) : ComponentContext by componentContext,
    TimetableOwnerComponent by TimetableOwnerDelegate(
        componentContext,
        weekOfYear.toLocalDateOfWeekOfYear()
    ) {

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
                            it.let(::onAddStudyGroup)
                        },
                        componentContext
                    )
                )

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

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        object GroupChooser : OverlayConfig()

        data class PeriodEditor(
            val periodConfig: EditingPeriod,
            val onFinish: (PeriodResponse?) -> Unit,
        ) : OverlayConfig()
    }

    sealed class OverlayChild {
        class GroupChooser(val component: StudyGroupChooserComponent) : OverlayChild()

        class PeriodEditor(val component: PeriodEditorComponent) : OverlayChild()
    }

    private val bellSchedule = metaRepository.observeBellSchedule

    val studyGroups = MutableStateFlow(_studyGroupTimetables.map { it.first })

    private val editorComponents = MutableStateFlow(
        _studyGroupTimetables.map {
            createDayTimetableEditorComponent(it.second, it.first.id)
        }
    )

    private fun createDayTimetableEditorComponent(
        timetable: TimetableResponse,
        groupId: UUID,
    ): DayTimetableEditorComponent {
        return _dayTimetableEditorComponent(
            timetable,
            groupId,
            selectedDate,
            componentContext.childContext("DayTimetable $groupId ${System.currentTimeMillis()}") // Make random name because context never destroy after deleting component
        )
    }

    val isEdit = MutableStateFlow(false)

    val publishState = MutableStateFlow(PublishState.PREPARATION)

    val timetablesViewStates: MutableStateFlow<List<StateFlow<TimetableState>>> =
        MutableStateFlow(emptyList())

    init {
        componentScope.launch {
            timetablesViewStates.value = editorComponents.value.map { getViewState(it) }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun getViewState(component: DayTimetableEditorComponent) =
        bellSchedule.flatMapLatest { schedule ->
            isEdit.flatMapLatest { isEdit ->
                component.editingWeekTimetable.map {
                    it.toTimetableState(weekOfYear, schedule, isEdit)
                }
            }

        }.stateIn(componentScope)

    val selectedGroup = MutableStateFlow(0)

    private fun onAddStudyGroup(studyGroupResponse: StudyGroupResponse) {
        studyGroups.update { it + studyGroupResponse }

        val dayTimetableEditorComponent = createDayTimetableEditorComponent(
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
                editorComponents.update { it - it[position].apply { onDestroy() } }
                timetablesViewStates.update { it - it[position] }
            }
        }
    }

    fun onStudyGroupSelect(position: Int) {
        selectedGroup.value = position
    }

    fun onEditEnableClick(edit: Boolean) {
        isEdit.value = edit
    }

    fun onAddPeriodClick() {
        val group = studyGroups.value[selectedGroup.value].toStudyGroupName()

        // TODO: Вместо EditingPeriod передавать данные в другой обертке т.к. выбрасывается:
        //  Parcelable encountered IOException writing serializable object (name = com.denchic45.studiversity.ui.timetableLoader.TimetablesPublisherComponent$onAddPeriodClick$2)
        overlayNavigation.activate(
            OverlayConfig.PeriodEditor(
                EditingPeriod(
                    selectedDate.value,
                    group.id,
                    group.name
                ).apply {
                    order = currentSelectedDayTimetable
                        .lastOrNull()?.order?.let { it + 1 } ?: 1
                }
            ) { it?.let(currentEditor::onAddPeriod) })
    }

    fun onEditPeriodClick(periodPos: Int) {
        overlayNavigation.activate(OverlayConfig.PeriodEditor(
            currentSelectedDayTimetable[periodPos].let { period ->
                val group = period.studyGroup
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
            it?.let { currentEditor.onUpdatePeriod(periodPos, it) }
        })
    }

    fun onRemovePeriodSwipe(position: Int) {
        currentEditor.onRemovePeriod(position)
    }

    private val currentSelectedDayTimetable: List<PeriodResponse>
        get() = currentEditor.editingWeekTimetable.value[selectedDate.value.dayOfWeek.ordinal]


    private val currentEditor
        get() = editorComponents.value[selectedGroup.value]

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