package com.denchic45.studiversity.ui.finder

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.ui.course.CourseComponent
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.ui.profile.ProfileComponent
import com.denchic45.studiversity.ui.search.StudyGroupChooserComponent
import com.denchic45.studiversity.ui.search.UserChooserComponent
import com.denchic45.studiversity.ui.studygroup.StudyGroupComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class FinderComponent(
    userChooserComponent: (
            (UserItem) -> Unit,
            ComponentContext,
    ) -> UserChooserComponent,
    studyGroupChooserComponent: (
            (StudyGroupResponse) -> Unit,
            ComponentContext,
    ) -> StudyGroupChooserComponent,
    profileComponent: (onStudyGroupOpen: (UUID) -> Unit, UUID, ComponentContext) -> ProfileComponent,
    studyGroupComponent: (
        onCourseOpen: (UUID) -> Unit,
        onStudyGroupOpen: (UUID) -> Unit,
        UUID,
        ComponentContext,
    ) -> StudyGroupComponent,
    courseComponent: (
        onFinish: () -> Unit,
        onStudyGroupOpen: (UUID) -> Unit, UUID,
        ComponentContext,
    ) -> CourseComponent,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {

    val query = MutableStateFlow("")

    val children = listOf(
        TabChild.Users(userChooserComponent({}, childContext("Users")))
    )

    val selectedTab = MutableStateFlow(0)

    private val activeChild
        get() = children[selectedTab.value]

    private val overlayNavigation = SlotNavigation<OverlayConfig>()

    val childSlot = childSlot(
        source = overlayNavigation,
        childFactory = { config, context ->
            when (config) {
                is OverlayConfig.Profile -> OverlayChild.Profile(
                    profileComponent(
                        { overlayNavigation.activate(OverlayConfig.StudyGroup(it)) },
                        config.userId,
                        context
                    )
                )

                is OverlayConfig.StudyGroup -> OverlayChild.StudyGroup(
                    studyGroupComponent(
                        { overlayNavigation.activate(OverlayConfig.Course(it)) },
                        { overlayNavigation.activate(OverlayConfig.StudyGroup(it)) },
                        config.studyGroupId, context
                    )
                )

                is OverlayConfig.Course -> OverlayChild.Course(
                    courseComponent(
                        overlayNavigation::dismiss,
                        { overlayNavigation.activate(OverlayConfig.StudyGroup(it)) },
                        config.courseId,
                        context
                    )
                )
            }
        }
    )

    fun onTabSelect(position: Int) {
        selectedTab.value = position
    }

    fun onQueryChange(text: String) {
        query.value = text
    }

    private val componentScope = componentScope()

    init {
        componentScope.launch {
            combine(selectedTab, query) { selected, text -> selected to text }
                .collect { (selected, text) ->
                    children[selected].component.onQueryChange(text)
                }
        }
    }

    sealed class TabChild {
        class Users(val component: UserChooserComponent) : TabChild()
        class StudyGroups(val component: StudyGroupChooserComponent) : TabChild()
    }

    @Parcelize
    sealed class OverlayConfig : Parcelable {
        data class Profile(val userId: UUID) : OverlayConfig()
        data class StudyGroup(val studyGroupId: UUID) : OverlayConfig()
        data class Course(val courseId: UUID) : OverlayConfig()
    }

    sealed class OverlayChild {
        class Profile(val component: ProfileComponent) : OverlayChild()
        class StudyGroup(val component: StudyGroupComponent) : OverlayChild()
        class Course(val component: CourseComponent) : OverlayChild()
    }
}