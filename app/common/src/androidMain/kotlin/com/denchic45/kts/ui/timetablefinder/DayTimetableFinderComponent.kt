package com.denchic45.kts.ui.timetablefinder

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.success
import com.denchic45.kts.domain.usecase.PutTimetableUseCase
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.ui.timetableeditor.DayTimetableEditorComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.PutTimetableRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID

@Inject
class DayTimetableFinderComponent(
    private val putTimetableUseCase: PutTimetableUseCase,
    private val _dayTimetableComponent: (
        LocalDate,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> DayTimetableComponent,
    private val dayTimetableEditorComponent: (
        studyGroupId: UUID,
        _selectedDate: LocalDate,
        _weekTimetable: List<List<PeriodResponse>>,
        onFinish: (PutTimetableRequest?) -> Unit,
        ComponentContext,
    ) -> DayTimetableEditorComponent,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val overlayNavigation = OverlayNavigation<Config>()
    private val childOverlay = childOverlay(
        source = overlayNavigation,
        childFactory = { config, componentContext ->
            childFactory(config, componentContext)
        }
    )

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
            val timetableResponse = dayTimetableComponent.weekTimetable.value.success().value

            Child.Editor(
                dayTimetableEditorComponent(
                    owner.value!!.ownerId,
                    dayTimetableComponent.selectedDate.value,
                    timetableResponse.days,
                    { request ->
                        request?.let {
                            componentScope().launch {
                                putTimetableUseCase(
                                    weekOfYear = dayTimetableComponent.selectedWeekOfYear.first(),
                                    putTimetableRequest = request
                                )
                            }
                        }
                    },
                    componentContext
                )
            )
        }
    }

    //    private val selectedDate = MutableStateFlow(LocalDate.now())
    private val owner = MutableStateFlow<TimetableOwner.StudyGroup?>(null)

    private val dayTimetableComponent = _dayTimetableComponent(
        LocalDate.now(),
        owner.filterNotNull(),
        componentContext.childContext("DayTimetable")
    )



    fun onEditClick() {
//        editor.update {
//            DayTimetableEditor(
//                _weekTimetable = dayTimetableComponent.weekTimetable.value.success().value.days
//            )
//        }
        overlayNavigation.activate(Config.Editor)
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