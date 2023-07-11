package com.denchic45.studiversity.ui.timetableSearch

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
import com.arkivanov.essenty.backhandler.BackCallback
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.model.toItem
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.success
import com.denchic45.studiversity.domain.timetable.model.PeriodDetails
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.studiversity.domain.timetable.model.Window
import com.denchic45.studiversity.domain.usecase.FindStudyGroupByContainsNameUseCase
import com.denchic45.studiversity.domain.usecase.PutTimetableUseCase
import com.denchic45.studiversity.domain.usecase.TimetableOwner
import com.denchic45.studiversity.ui.periodeditor.EditingPeriod
import com.denchic45.studiversity.ui.periodeditor.EditingPeriodDetails
import com.denchic45.studiversity.ui.periodeditor.PeriodEditorComponent
import com.denchic45.studiversity.ui.timetable.TimetableFinderComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerDelegate
import com.denchic45.studiversity.ui.timetable.state.TimetableState
import com.denchic45.studiversity.ui.timetableeditor.TimetableEditorComponent
import com.denchic45.studiversity.util.asFlow
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.DayOfWeek
import java.util.UUID

@Inject
class TimetableSearchComponent(
    private val putTimetableUseCase: PutTimetableUseCase,
    private val findStudyGroupByContainsNameUseCase: FindStudyGroupByContainsNameUseCase,
    _timetableFinderComponent: (
        StateFlow<String>,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> TimetableFinderComponent,
    private val _timetableEditorComponent: (
        source: TimetableState,
        studyGroupId: UUID,
        ComponentContext,
    ) -> TimetableEditorComponent,
    private val periodEditorComponent: (
        EditingPeriod,
        (PeriodItem?) -> Unit,
        ComponentContext,
    ) -> PeriodEditorComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    TimetableOwnerComponent by TimetableOwnerDelegate(componentContext) {

    private val owner = MutableStateFlow<TimetableOwner.StudyGroup?>(null)

    private val timetableFinderComponent = _timetableFinderComponent(
        selectedWeekOfYear,
        owner.filterNotNull(),
        componentContext.childContext("TimetableFinder")
    )

    private val actualTimetable = timetableFinderComponent.timetableStateResource
        .shareIn(componentScope, SharingStarted.Lazily)

    val state = TimetableFinderState()

    private val query = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    private val foundStudyGroups = query.filter(String::isNotEmpty).flatMapLatest {
        findStudyGroupByContainsNameUseCase(it)
    }

    private val timetableEditorNavigation = OverlayNavigation<TimetableEditorConfig>()
    private val timetableEditorOverlay = childOverlay(
        source = timetableEditorNavigation,
        childFactory = { _, componentContext ->
            TimetableEditorChild(
                _timetableEditorComponent(
                    timetableStateResourceFlow.value.success().value,
                    owner.value!!.ownerId,
                    componentContext
                )
            )
        }
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val timetableStateResourceFlow = editorComponentFlow().flatMapLatest { overlay ->
        overlay.overlay?.instance?.component?.editingTimetableState
            ?.map { resourceOf(it) } ?: actualTimetable
    }.stateInResource(componentScope)


    private fun editorComponentFlow(): SharedFlow<ChildOverlay<TimetableEditorConfig, TimetableEditorChild>> {
        return timetableEditorOverlay.asFlow()
            .shareIn(componentScope, SharingStarted.Lazily, 1)
    }


    private val overlayNavigation = OverlayNavigation<OverlayConfig>()
    val childOverlay = childOverlay<OverlayConfig, OverlayChild>(
        source = overlayNavigation,
        key = "TimetableSearchChildOverlay",
        handleBackButton = true,
        childFactory = { config, componentContext ->
            when (config) {
                is OverlayConfig.PeriodEditor -> OverlayChild.PeriodEditor(
                    periodEditorComponent(
                        config.config,
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

    private val dayTimetableEditorComponent
        get() = timetableEditorOverlay.value.overlay?.instance?.component

    val isEdit = editorComponentFlow().map { it.overlay != null }
        .stateIn(componentScope, SharingStarted.Lazily, false)

    private val backCallback = BackCallback(onBack = ::onCancelChanges)

    fun onEditClick() {
        timetableEditorNavigation.activate(TimetableEditorConfig)
    }

    init {
        backHandler.register(backCallback)
        componentScope.launch {
            isEdit.collect {
                backCallback.isEnabled = it
            }
        }
        componentScope.launch {
            foundStudyGroups.collect {
                state.foundGroups = it
            }
        }
    }

    fun onSaveChangesClick() {
        dayTimetableEditorComponent!!.getRequestModel().let {
            componentScope().launch {
                putTimetableUseCase(
                    weekOfYear = timetableFinderComponent.selectedWeekOfYear.first(),
                    putTimetableRequest = it
                )
            }
        }
        timetableEditorNavigation.dismiss()
    }

    private fun onCancelChanges() {
        dayTimetableEditorComponent!!.onReset()
        timetableEditorNavigation.dismiss()
    }

    fun onQueryType(queryText: String) {
        state.query = queryText
        query.update { queryText }
    }

    fun onGroupSelect(response: StudyGroupResponse) {
        state.selectedStudyGroup = response
        state.query = response.name
        owner.update { TimetableOwner.StudyGroup(response.id) }
    }

    fun onAddPeriodClick(dayOfWeek: DayOfWeek) {
        overlayNavigation.activate(
            OverlayConfig.PeriodEditor(
                EditingPeriod.createEmpty(
                    getDateByDayOfWeek(dayOfWeek),
                    selectedStudyGroupItem, getNewLatestOrder(dayOfWeek)
                )
            ) {
                it?.let { dayTimetableEditorComponent!!.onAddPeriod(dayOfWeek, it) }
            })
    }

    private fun getNewLatestOrder(dayOfWeek: DayOfWeek): Int {
        return dayTimetableEditorComponent!!
            .editingTimetableState.value.getByDay(dayOfWeek).size + 1
    }


    fun onEditPeriodClick(dayOfWeek: DayOfWeek, periodPosition: Int) {
        overlayNavigation.activate(
            OverlayConfig.PeriodEditor(
                dayTimetableEditorComponent!!.editingTimetableState.value
                    .getByDay(dayOfWeek)[periodPosition].let { slot ->

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
                it?.let {
                    dayTimetableEditorComponent!!.onUpdatePeriod(
                        dayOfWeek = dayOfWeek,
                        position = periodPosition,
                        period = it
                    )
                }
            })
    }

    private val selectedStudyGroupItem get() = state.selectedStudyGroup!!.toItem()

    fun onRemovePeriodSwipe(dayOfWeek: DayOfWeek, periodPosition: Int) {
        dayTimetableEditorComponent!!.onRemovePeriod(dayOfWeek, periodPosition)
    }

    @Parcelize
    object TimetableEditorConfig : Parcelable

    class TimetableEditorChild(val component: TimetableEditorComponent)

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class PeriodEditor(
            val config: EditingPeriod,
            val onFinish: (PeriodItem?) -> Unit,
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
}