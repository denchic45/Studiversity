package com.denchic45.kts.ui.timetableLoader

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TimetableLoaderComponent(
    private val timetablesCreatorComponent: ((String, List<Pair<StudyGroupResponse, TimetableResponse>>) -> Unit, ComponentContext) -> TimetableCreatorComponent,
    private val timetablesPublisherComponent: (String, List<Pair<StudyGroupResponse, TimetableResponse>>, ComponentContext) -> TimetablesPublisherComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    private val navigation = StackNavigation<TimetableLoaderConfig>()
    val childStack = childStack(
        source = navigation,
        initialConfiguration = TimetableLoaderConfig.Creator,
        childFactory = { config, componentContext ->
            when (config) {
                is TimetableLoaderConfig.Creator -> TimetableLoaderChild.Creator(
                    timetablesCreatorComponent({ weekOfYear, timetables ->
                        navigation.replaceCurrent(
                            TimetableLoaderConfig.Editor(
                                weekOfYear,
                                timetables
                            )
                        )
                    }, componentContext)
                )
                is TimetableLoaderConfig.Editor -> TimetableLoaderChild.Publisher(
                    timetablesPublisherComponent(
                        config.weekOfYear,
                        config.studyGroupTimetables,
                        componentContext
                    )
                )
            }
        }
    )


    sealed class TimetableLoaderConfig : Parcelable {
        @Parcelize
        object Creator : TimetableLoaderConfig() {
            @Suppress("unused")
            private fun readResolve(): Any = Creator
        }

        @Parcelize
        class Editor(
            val weekOfYear: String,
            val studyGroupTimetables: List<Pair<StudyGroupResponse, TimetableResponse>>,
        ) : TimetableLoaderConfig() {
            @Suppress("unused")
            private fun readResolve(): Any = Editor(weekOfYear, studyGroupTimetables)
        }
    }

    sealed class TimetableLoaderChild {
        class Creator(val component: TimetableCreatorComponent) : TimetableLoaderChild()
        class Publisher(val component: TimetablesPublisherComponent) : TimetableLoaderChild()
    }
}