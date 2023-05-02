package com.denchic45.kts.ui.periodeditor

import androidx.compose.runtime.Stable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.resourceOf
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindRoomByContainsNameUseCase
import com.denchic45.kts.ui.chooser.CourseChooserComponent
import com.denchic45.kts.ui.chooser.UserChooserComponent
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.timetable.model.EventDetails
import com.denchic45.stuiversity.api.timetable.model.LessonDetails
import com.denchic45.stuiversity.api.timetable.model.PeriodMember
import com.denchic45.stuiversity.api.timetable.model.PeriodResponse
import com.denchic45.stuiversity.api.timetable.model.StudyGroupName
import com.denchic45.stuiversity.api.timetable.model.toPeriodMember
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate

@Inject
class PeriodEditorComponent(
    private val findRoomByContainsNameUseCase: FindRoomByContainsNameUseCase,
    private val courseChooserComponent: (onFinish: (CourseResponse?) -> Unit, ComponentContext) -> CourseChooserComponent,
    private val userChooserComponent: (onFinish: (UserResponse?) -> Unit, ComponentContext) -> UserChooserComponent,
    private val eventDetailsEditorComponent: (EditingPeriod, ComponentContext) -> EventDetailsEditorComponent,
    private val lessonDetailsEditorComponent: (EditingPeriod, onCourseChoose: (OverlayConfig.CourseChooser) -> Unit, ComponentContext) -> LessonDetailsEditorComponent,
    @Assisted
    private val _period: PeriodResponse,
    @Assisted
    private val _onTeacherChoose: () -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

//    private var draftEventDetails = EditingPeriodDetails.Event()
//    private var draftLessonDetails = EditingPeriodDetails.Lesson()

    private val editingPeriod = EditingPeriod(
        date = _period.date,
        order = _period.order,
        group = _period.studyGroup,
        room = _period.room,
        members = _period.members,
        details = when (val details = _period.details) {
            is EventDetails -> EditingPeriodDetails.Event().apply {
                name = details.name
                color = details.color
                iconUrl = details.iconUrl
            }

            is LessonDetails -> EditingPeriodDetails.Lesson().apply {
                course = details.course
            }
        }
    )
    val state = MutableStateFlow<Resource<EditingPeriod>>(resourceOf(editingPeriod))

    private val roomQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val foundRooms = roomQuery.filter(String::isNotEmpty)
        .mapLatest { findRoomByContainsNameUseCase(it) }
        .stateInResource(componentScope)

    private val stackNavigation = StackNavigation<DetailsConfig>()
    private val childStack = childStack(
        source = stackNavigation,
        initialConfiguration = when (editingPeriod.details) {
            is EditingPeriodDetails.Event -> DetailsConfig.Event

            is EditingPeriodDetails.Lesson -> DetailsConfig.Lesson
        },
        childFactory = { config, componentContext ->
            when (config) {
                is DetailsConfig.Event -> {
                    DetailsChild.Event(eventDetailsEditorComponent(editingPeriod, componentContext))
                }

                is DetailsConfig.Lesson -> {
                    DetailsChild.Lesson(
                        lessonDetailsEditorComponent(
                            editingPeriod,
                            overlayNavigation::activate,
                            componentContext
                        )
                    )
                }
            }
        }
    )

    private val overlayNavigation = OverlayNavigation<OverlayConfig>()
    private val childOverlay = childOverlay(
        source = overlayNavigation,
        childFactory = { config, componentContext ->
            when (config) {
                is OverlayConfig.CourseChooser -> OverlayChild.CourseChooser(
                    courseChooserComponent({ config.onFinish(it) }, componentContext)
                )

                is OverlayConfig.UserChooser -> OverlayChild.UserChooser(
                    userChooserComponent({ config.onFinish(it) }, componentContext)
                )
            }
        }
    )

    fun onRoomType(room: String) {
        roomQuery.update { room }
    }

    fun onDetailsTypeSelect(type: DetailsType) {
        if (editingPeriod.details.type == type) return

        stackNavigation.bringToFront(
            when (type) {
                DetailsType.EVENT -> DetailsConfig.Event
                DetailsType.LESSON -> DetailsConfig.Lesson
            }
        )

//        // save draft
//        when (val details = editingPeriod.details) {
//            is EditingPeriodDetails.Event -> draftEventDetails = details
//            is EditingPeriodDetails.Lesson -> draftLessonDetails = details
//        }
//        // select draft
//        editingPeriod.details = when (type) {
//            DetailsType.EVENT -> draftEventDetails
//            DetailsType.LESSON -> draftLessonDetails
//        }
    }

    fun onTeacherChoose() {
        _onTeacherChoose()
    }

    fun onTeacherSelect(userResponse: UserResponse) {
        editingPeriod.members = editingPeriod.members + userResponse.toPeriodMember()
    }

    enum class DetailsType { EVENT, LESSON }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class CourseChooser(val onFinish: (CourseResponse?) -> Unit) : OverlayConfig()

        data class UserChooser(val onFinish: (UserResponse?) -> Unit) : OverlayConfig()
    }

    sealed class OverlayChild {
        class CourseChooser(val component: CourseChooserComponent) : OverlayChild()

        class UserChooser(val component: UserChooserComponent) : OverlayChild()
    }

    @Parcelize
    sealed class DetailsConfig : Parcelable {

        object Event : DetailsConfig()
        object Lesson : DetailsConfig()
    }

    sealed class DetailsChild {
        class Event(component: EventDetailsEditorComponent) : DetailsChild()
        class Lesson(component: LessonDetailsEditorComponent) : DetailsChild()
    }
}

data class EditingPeriod(
    var date: LocalDate,
    var order: Int,
    var group: StudyGroupName,
    var room: RoomResponse?,
    var members: List<PeriodMember>,
    var details: EditingPeriodDetails,
)

sealed class EditingPeriodDetails {
    abstract val type: PeriodEditorComponent.DetailsType

    @Stable
    class Event : EditingPeriodDetails() {
        var name: String = ""
        var color: String = ""
        var iconUrl: String = ""
        override val type = PeriodEditorComponent.DetailsType.EVENT
    }

    @Stable
    class Lesson : EditingPeriodDetails() {
        var course: CourseResponse? = null
        override val type = PeriodEditorComponent.DetailsType.LESSON
    }
}