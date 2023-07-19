package com.denchic45.studiversity.ui.timetableloader

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
import com.denchic45.studiversity.domain.model.toItem
import com.denchic45.studiversity.domain.timetable.model.PeriodDetails
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.studiversity.domain.timetable.model.PeriodSlot
import com.denchic45.studiversity.domain.timetable.model.Window
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
import com.denchic45.studiversity.ui.timetableeditor.TimetableEditorComponent
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.DayOfWeek
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
        (PeriodItem?) -> Unit,
        ComponentContext,
    ) -> PeriodEditorComponent,
    private val putTimetableUseCase: PutTimetableUseCase,
    private val _TimetableEditorComponent: (
        source: TimetableState,
        studyGroupId: UUID,
        ComponentContext,
    ) -> TimetableEditorComponent,
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

    private val bellSchedule = metaRepository.observeBellSchedule
        .shareIn(componentScope, SharingStarted.Lazily, 1)

    val studyGroups = MutableStateFlow(_studyGroupTimetables.map { it.first })

    val editorComponents = MutableStateFlow(emptyList<TimetableEditorComponent>())

    val isEdit = MutableStateFlow(false)

    val publishState = MutableStateFlow(PublishState.PREPARATION)

    val selectedGroup = MutableStateFlow(0)

    init {
        componentScope.launch {
            val bellSchedule = bellSchedule.first()
            editorComponents.value = _studyGroupTimetables.map {
                createDayTimetableEditorComponent(
                    timetable = it.second.toTimetableState(
                        yearWeek = weekOfYear,
                        bellSchedule = bellSchedule
                    ),
                    studyGroupId = it.first.id
                )
            }
        }
    }

    private fun createDayTimetableEditorComponent(
        timetable: TimetableState,
        studyGroupId: UUID,
    ): TimetableEditorComponent {
        return _TimetableEditorComponent(
            timetable,
            studyGroupId,
            componentContext.childContext("DayTimetable $studyGroupId ${System.currentTimeMillis()}") // Make random name because context never destroy after deleting component
        )
    }

    private fun onAddStudyGroup(studyGroupResponse: StudyGroupResponse) {
        componentScope.launch {
            studyGroups.update { it + studyGroupResponse }

            val dayTimetableEditorComponent = createDayTimetableEditorComponent(
                timetable = TimetableState(
                    firstWeekDate = selectedDate.value,
                    dayTimetables = listOf(
                        emptyList(),
                        emptyList(),
                        emptyList(),
                        emptyList(),
                        emptyList(),
                        emptyList()
                    ),
                    bellSchedule = bellSchedule.first()
                ),
                studyGroupId = studyGroupResponse.id,
            )
            editorComponents.update { components ->
                components + dayTimetableEditorComponent
            }

//            timetablesViewStates.update { it + getViewState(dayTimetableEditorComponent) }
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
//                timetablesViewStates.update { it - it[position] }
            }
        }
    }

    fun onStudyGroupSelect(position: Int) {
        selectedGroup.value = position
    }

    fun onEditEnableClick(edit: Boolean) {
        isEdit.value = edit
    }

    fun onAddPeriodClick(dayOfWeek: DayOfWeek) {
        // TODO: Вместо EditingPeriod передавать данные в другой обертке т.к. выбрасывается:
        //  Parcelable encountered IOException writing serializable object (name = com.denchic45.studiversity.ui.timetableLoader.TimetablesPublisherComponent$onAddPeriodClick$2)
        overlayNavigation.activate(
            OverlayConfig.PeriodEditor(
                EditingPeriod(
                    getDateByDayOfWeek(dayOfWeek),
                    selectedStudyGroupItem,
                ).apply {
                    order = getDayTimetable(dayOfWeek).size + 1
                }
            ) { it?.let { currentEditor.onAddPeriod(dayOfWeek, it) } })
    }

    private val selectedStudyGroupItem get() = studyGroups.value[selectedGroup.value].toItem()

    fun onEditPeriodClick(dayOfWeek: DayOfWeek, periodPosition: Int) {
        overlayNavigation.activate(OverlayConfig.PeriodEditor(
            getDayTimetable(dayOfWeek)[periodPosition].let { slot ->
                EditingPeriod(getDateByDayOfWeek(dayOfWeek), selectedStudyGroupItem).apply {
                    order = periodPosition + 1
                    when (slot) {
                        is PeriodItem -> {
                            room = slot.room
                            members = slot.members
                            details = when (val details = slot.details) {
                                is PeriodDetails.Lesson -> EditingPeriodDetails.Lesson().apply {
                                    course = details.course
                                }

                                is PeriodDetails.Event -> EditingPeriodDetails.Event().apply {
                                    name = details.name
                                    color = details.color
                                    iconUrl = details.iconUrl
                                }
                            }
                        }

                        is Window -> EditingPeriod.createEmpty(
                            getDateByDayOfWeek(dayOfWeek),
                            selectedStudyGroupItem,
                            getNewLatestOrder(dayOfWeek)
                        )
                    }
                }
            }
        ) {
            it?.let { currentEditor.onUpdatePeriod(dayOfWeek, periodPosition, it) }
        })
    }

    private fun getNewLatestOrder(dayOfWeek: DayOfWeek): Int {
        return selectedTimetableState.getByDay(dayOfWeek).size + 1
    }

    fun onRemovePeriodSwipe(dayOfWeek: DayOfWeek, position: Int) {
        currentEditor.onRemovePeriod(dayOfWeek, position)
    }

    private fun getDayTimetable(dayOfWeek: DayOfWeek): List<PeriodSlot> {
        return selectedTimetableState.getByDay(dayOfWeek)
    }


    private val selectedTimetableState get() = currentEditor.editingTimetableState.value


    private val currentEditor
        get() = editorComponents.value[selectedGroup.value]

//    private fun getDateByDayOfWeek(dayOfWeek: DayOfWeek): LocalDate {
//        return weekOfYear.toLocalDateOfWeekOfYear(dayOfWeek)
//    }

    fun onPublishClick() {
        publishState.update { PublishState.SENDING }
        componentScope.launch {
            val result = editorComponents.value.map {
                async { putTimetableUseCase(weekOfYear, it.getRequestModel()) }
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

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        object GroupChooser : OverlayConfig()

        data class PeriodEditor(
            val periodConfig: EditingPeriod,
            val onFinish: (PeriodItem?) -> Unit,
        ) : OverlayConfig()
    }

    sealed class OverlayChild {
        class GroupChooser(val component: StudyGroupChooserComponent) : OverlayChild()

        class PeriodEditor(val component: PeriodEditorComponent) : OverlayChild()
    }
}