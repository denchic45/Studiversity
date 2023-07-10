package com.denchic45.studiversity.ui.timetableLoader

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.replaceCurrent
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.timetable.model.TimetableParserResult
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class TimetableLoaderComponent(
    private val timetablesCreatorComponent: (
        onResult: (TimetableParserResult) -> Unit,
        ComponentContext,
    ) -> TimetableCreatorComponent,
    private val timetablesPublisherComponent: (
        String,
        List<Pair<StudyGroupResponse, TimetableResponse>>,
        ComponentContext,
    ) -> TimetablesPublisherComponent,
    @Assisted
    private val onClose: () -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    fun onDismissRequest() {
        onClose()
    }

    private val navigation = StackNavigation<TimetableLoaderConfig>()
    val childStack = childStack(
        source = navigation,
        handleBackButton = true,
        initialConfiguration = TimetableLoaderConfig.Creator,
        childFactory = { config, componentContext ->
            when (config) {
                is TimetableLoaderConfig.Creator -> TimetableLoaderChild.Creator(
                    timetablesCreatorComponent(
                        { result ->
                            navigation.replaceCurrent(
                                TimetableLoaderConfig.Publisher(
                                    result.weekOfYear,
                                    result.timetables
                                )
                            )
                        },
                        componentContext
                    )
                )

                is TimetableLoaderConfig.Publisher -> TimetableLoaderChild.Publisher(
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
        class Publisher(
            val weekOfYear: String,
            val studyGroupTimetables: List<Pair<StudyGroupResponse, TimetableResponse>>,
        ) : TimetableLoaderConfig() {
            @Suppress("unused")
            private fun readResolve(): Any = Publisher(weekOfYear, studyGroupTimetables)
        }
    }

    sealed class TimetableLoaderChild {
        class Creator(val component: TimetableCreatorComponent) : TimetableLoaderChild()
        class Publisher(val component: TimetablesPublisherComponent) : TimetableLoaderChild()
    }
}