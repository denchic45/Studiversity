package com.denchic45.studiversity.ui.yourtimetables

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.domain.resource.map
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.FindYourStudyGroupsUseCase
import com.denchic45.studiversity.domain.usecase.TimetableOwner
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.ui.timetable.TimetableFinderComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerComponent
import com.denchic45.studiversity.ui.timetable.TimetableOwnerDelegate
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class YourTimetablesComponent(
    private val appPreferences: AppPreferences,
    findYourStudyGroupsUseCase: FindYourStudyGroupsUseCase,
    _timetableFinderComponent: (StateFlow<String>, Flow<TimetableOwner>, ComponentContext) -> TimetableFinderComponent,
    @Assisted
    private val onStudyGroupOpen: (UUID) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext,
    EmptyChildrenContainer,
    TimetableOwnerComponent by TimetableOwnerDelegate(componentContext) {

//    private val overlayNavigation = OverlayNavigation<OverlayConfig>()

//    val childOverlay = childOverlay(
//        source = overlayNavigation,
//        handleBackButton = true,
//        childFactory = { config, context ->
//            when (config) {
//                is OverlayConfig.StudyGroup -> OverlayChild.StudyGroup()
//            }
//        }
//    )

    fun onTimetableSelect(position: Int) {
        studyGroups.value.onSuccess { groups ->
            appPreferences.selectedStudyGroupTimetableId = if (position == -1) null
            else groups[position].id.toString()
        }
    }

    fun onStudyGroupClick(studyGroupId: UUID) {
        onStudyGroupOpen(studyGroupId)
    }

    val studyGroups = findYourStudyGroupsUseCase().stateInResource(componentScope)
    val selectedTimetablePosition = combine(
        studyGroups,
        appPreferences.selectedStudyGroupTimetableIdFlow
    ) { groups, selectedStudyGroupId ->
        selectedStudyGroupId?.toUUID()?.let { studyGroupId ->
            groups.map { groups ->
                groups.indexOfFirst { it.id == studyGroupId }
                    .also { index ->
                        if (index == -1)
                            appPreferences.selectedStudyGroupTimetableId = null
                    }
            }
        } ?: resourceOf(-1)
    }.stateInResource(componentScope)

    private val selectedOwner = appPreferences.selectedStudyGroupTimetableIdFlow.map { id ->
        id?.let { TimetableOwner.StudyGroup(id.toUUID()) }
            ?: TimetableOwner.Member(null)
    }

    private val timetableComponent = _timetableFinderComponent(
        selectedWeekOfYear,
        selectedOwner,
        componentContext.childContext("DayTimetable")
    )

    val timetableState = timetableComponent.timetableStateResource

    val refreshing = timetableComponent.refreshing

//    @Parcelize
//    sealed interface OverlayConfig : Parcelable {
//        data class StudyGroup(val studyGroupId: UUID) : OverlayConfig
//    }
//
//    sealed interface OverlayChild {
//        class StudyGroup(val component: StudyGroupComponent) : OverlayChild
//    }
}