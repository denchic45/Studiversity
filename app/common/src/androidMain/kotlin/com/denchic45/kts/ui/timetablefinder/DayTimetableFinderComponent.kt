package com.denchic45.kts.ui.timetablefinder

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.kts.domain.success
import com.denchic45.kts.domain.usecase.TimetableOwner
import com.denchic45.kts.ui.timetable.DayTimetableComponent
import com.denchic45.kts.ui.timetableeditor.DayTimetableEditorComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.timetable.model.PeriodRequest
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate

@Inject
class DayTimetableFinderComponent(
    private val _dayTimetableComponent: (
        LocalDate,
        Flow<TimetableOwner>,
        ComponentContext,
    ) -> DayTimetableComponent,
    private val dayTimetableEditorComponent: (
        _selectedDate: LocalDate,
        _weekTimetable: List<List<PeriodResponse>>,
        onFinish: (List<List<PeriodRequest>>?) -> Unit,
        ComponentContext,
    ) -> DayTimetableEditorComponent,
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val overlayNavigation = OverlayNavigation<Config>()
    private val childStack = childOverlay(
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

            timetable.
            Child.Editor(
                dayTimetableEditorComponent(
                    selectedDate.value,
                    ,
                    {

                    },
                    componentContext
                )
            )
        }
    }

    private val selectedDate = MutableStateFlow(LocalDate.now())
    private val owner = MutableSharedFlow<TimetableOwner.StudyGroup>(1)

    private val dayTimetableComponent = _dayTimetableComponent(
        selectedDate.value,
        owner,
        componentContext.childContext("DayTimetable")
    )

    private val isEdit = MutableStateFlow(false)

    fun onEditClick() {
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