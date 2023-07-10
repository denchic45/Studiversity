package com.denchic45.studiversity.ui.periodeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.bringToFront
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.model.CourseItem
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.domain.model.toItem
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.timetable.model.PeriodDetails
import com.denchic45.studiversity.domain.timetable.model.PeriodItem
import com.denchic45.studiversity.domain.usecase.FindRoomByContainsNameUseCase
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.search.CourseChooserComponent
import com.denchic45.studiversity.ui.search.UserChooserComponent
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.uivalidator.validator.observable
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.timetable.model.StudyGroupNameResponse
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.time.LocalDate
import java.util.UUID
import kotlin.random.Random

@Inject
class PeriodEditorComponent(
    private val findRoomByContainsNameUseCase: FindRoomByContainsNameUseCase,
    private val courseChooserComponent: (onFinish: (CourseResponse) -> Unit, ComponentContext) -> CourseChooserComponent,
    private val userChooserComponent: (onFinish: (UserItem) -> Unit, ComponentContext) -> UserChooserComponent,
    private val eventDetailsEditorComponent: (EditingPeriod, ComponentContext) -> EventDetailsEditorComponent,
    private val lessonDetailsEditorComponent: (EditingPeriod, OverlayNavigation<OverlayConfig>, ComponentContext) -> LessonDetailsEditorComponent,
    @Assisted
    private val config: EditingPeriod,
    @Assisted
    private val onFinish: (PeriodItem?) -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val state: EditingPeriod = config

    private val roomQuery = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val foundRooms = roomQuery.filter(String::isNotEmpty)
        .flatMapLatest { findRoomByContainsNameUseCase(it) }
        .stateInResource(componentScope)

    private val validator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = state::details,
                conditions = listOf(
                    Condition {
                        when (val details = it) {
                            is EditingPeriodDetails.Lesson -> lessonValidator(details)
                            is EditingPeriodDetails.Event -> eventValidator(details)
                        }.validate()
                    }
                )
            )
        )
    )

    private fun lessonValidator(details: EditingPeriodDetails.Lesson) = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = details::course,
                conditions = listOf(Condition { it != null })
            ).observable { details.courseError = !it }
        )
    )

    private fun eventValidator(details: EditingPeriodDetails.Event) = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = details::name,
                conditions = listOf(Condition(String::isNotEmpty))
            ),
            ValueValidator(
                value = details::color,
                conditions = listOf(Condition(String::isNotEmpty)),
            ),
            ValueValidator(
                value = details::iconUrl,
                conditions = listOf(Condition(String::isNotEmpty)),
            )
        )
    )

    private val overlayNavigation = OverlayNavigation<OverlayConfig>()
    val childOverlay = childOverlay(
        handleBackButton = true,
        source = overlayNavigation,
        childFactory = { config, componentContext ->
            when (config) {
                is OverlayConfig.CourseChooser -> OverlayChild.CourseChooser(
                    courseChooserComponent({
                        overlayNavigation.dismiss()
                        config.onFinish(it)
                    }, componentContext)
                )

                is OverlayConfig.UserChooser -> OverlayChild.UserChooser(
                    userChooserComponent({
                        overlayNavigation.dismiss()
                        config.onSelect(it)
                    }, componentContext)
                )
            }
        }
    )

    private val stackNavigation = StackNavigation<DetailsConfig>()
    val childDetailsStack = childStack(
        source = stackNavigation,
        initialConfiguration = when (state.details) {
            is EditingPeriodDetails.Event -> DetailsConfig.Event

            is EditingPeriodDetails.Lesson -> DetailsConfig.Lesson
        },
        childFactory = { config, componentContext ->
            when (config) {
                is DetailsConfig.Event -> {
                    DetailsChild.Event(eventDetailsEditorComponent(state, componentContext))
                }

                is DetailsConfig.Lesson -> {
                    DetailsChild.Lesson(
                        lessonDetailsEditorComponent(
                            state,
                            overlayNavigation,
                            componentContext
                        )
                    )
                }
            }
        }
    )

    fun onRoomType(room: String) {
        roomQuery.update { room }
    }

    fun onDetailsTypeSelect(type: DetailsType) {
        if (state.details.type == type) return

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

    private fun onTeacherSelect(userItem: UserItem) {
        state.members = state.members + userItem
    }

//    fun onCourseChoose() {
//        overlayNavigation.activate(OverlayConfig.CourseChooser {
//            it?.let {
//                (state.details as EditingPeriodDetails.Lesson).course = it
//            }
//        })
//    }

    fun onAddMemberClick() {
        overlayNavigation.activate(OverlayConfig.UserChooser {
            overlayNavigation.dismiss()
            it.let(::onTeacherSelect)
        })
    }

    fun onRoomSelect(room: RoomResponse) {
        state.room = room
    }

    fun onRoomRemove() {
        state.room = null
    }

    fun onRemoveMemberClick(member: UserItem) {
        state.members = state.members - member
    }

    fun onSaveClick() {
        if (!validator.validate()) return
        val studyGroup = state.studyGroup.toItem()

        onFinish(
            PeriodItem(
                id = Random.nextLong(0, 1000),
                studyGroup = studyGroup,
                room = state.room,
                members = state.members,
                details = when (val details = state.details) {
                    is EditingPeriodDetails.Lesson -> {
                        PeriodDetails.Lesson(details.course!!)
                    }

                    is EditingPeriodDetails.Event -> PeriodDetails.Event(
                        name = details.name,
                        color = details.color,
                        iconUrl = details.iconUrl
                    )
                }
            )

        )
    }

    fun onCloseClick() {
        onFinish(null)
    }

    enum class DetailsType { EVENT, LESSON }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class CourseChooser(val onFinish: (CourseResponse) -> Unit) : OverlayConfig()

        data class UserChooser(val onSelect: (UserItem) -> Unit) : OverlayConfig()
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
        class Event(val component: EventDetailsEditorComponent) : DetailsChild()
        class Lesson(val component: LessonDetailsEditorComponent) : DetailsChild()
    }
}

@Parcelize
@Stable
class EditingPeriod(
    private val _date: LocalDate,
    private val groupId: UUID,
    private val groupName: String
) : Parcelable {
    var date: LocalDate by mutableStateOf(_date)
    var order: Int by mutableStateOf(1)
    var studyGroup: StudyGroupNameResponse by mutableStateOf(
        StudyGroupNameResponse(
            groupId,
            groupName
        )
    )
    var room: RoomResponse? by mutableStateOf(null)
    var members: List<UserItem> by mutableStateOf(emptyList())
    var details: EditingPeriodDetails by mutableStateOf(EditingPeriodDetails.Lesson())

    companion object {
        fun createEmpty(
            date: LocalDate,
            studyGroupItem: StudyGroupItem,
            order: Int
        ): EditingPeriod {
            return EditingPeriod(
                _date = date,
                groupId = studyGroupItem.id,
                groupName = studyGroupItem.name
            ).apply {
                this.order = order
            }
        }
    }
}

@Stable
sealed class EditingPeriodDetails {
    abstract val type: PeriodEditorComponent.DetailsType

    @Stable
    class Event : EditingPeriodDetails() {
        var name: String by mutableStateOf("")
        var color: String by mutableStateOf("")
        var iconUrl: String by mutableStateOf("")
        override val type = PeriodEditorComponent.DetailsType.EVENT
    }

    @Stable
    class Lesson : EditingPeriodDetails() {
        var course: CourseItem? by mutableStateOf(null)
        var courseError: Boolean by mutableStateOf(false)
        override val type = PeriodEditorComponent.DetailsType.LESSON
    }
}