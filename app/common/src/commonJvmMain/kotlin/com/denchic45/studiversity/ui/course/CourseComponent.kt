package com.denchic45.studiversity.ui.course

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.resource.takeValueIfSuccess
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseByIdUseCase
import com.denchic45.studiversity.ui.courseeditor.CourseEditorComponent
import com.denchic45.studiversity.ui.courseelements.CourseElementsComponent
import com.denchic45.studiversity.ui.coursemembers.CourseMembersComponent
import com.denchic45.studiversity.ui.coursestudygroups.CourseStudyGroupsComponent
import com.denchic45.studiversity.ui.coursetimetable.CourseTimetableComponent
import com.denchic45.studiversity.ui.coursetopics.CourseTopicsComponent
import com.denchic45.studiversity.ui.navigation.ChildrenContainer
import com.denchic45.studiversity.ui.navigation.isActiveFlow
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*


@Inject
class CourseComponent(
    findCourseByIdUseCase: FindCourseByIdUseCase,
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    courseTopicsComponent: (
        courseId: UUID,
        ComponentContext,
    ) -> CourseTopicsComponent,
    courseEditorComponent: (
        onFinish: (Boolean) -> Unit,
        courseId: UUID?,
        ComponentContext,
    ) -> CourseEditorComponent,

    courseStudyGroupsComponent: (UUID, ComponentContext) -> CourseStudyGroupsComponent,
    courseElementsComponent: (
        RootNavigator,
        courseId: UUID,
        ComponentContext,
    ) -> CourseElementsComponent,
    courseMembersComponent: (
        RootNavigator,
        UUID,
        ComponentContext,
    ) -> CourseMembersComponent,
    courseTimetableComponent: (
        courseId: UUID,
        ComponentContext,
    ) -> CourseTimetableComponent,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ChildrenContainer {
    private val componentScope = componentScope()

    private val trigger = Channel<Unit>()
    val course = trigger.receiveAsFlow()
        .onStart { emit(Unit) }
        .map { findCourseByIdUseCase(courseId) }
        .stateInResource(componentScope)

    private val checkCapabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf(Capability.WriteCourse)
    ).stateInResource(componentScope)

    val allowEdit = checkCapabilities.map {
        it.takeValueIfSuccess()?.hasCapability(Capability.WriteCourse) ?: false
    }.stateIn(componentScope, SharingStarted.Lazily, false)

    private val courseElementsComponent = courseElementsComponent(
        rootNavigator,
        courseId,
        componentContext.childContext("Elements")
    )

    private val courseMembersComponent = courseMembersComponent(
        rootNavigator,
        courseId,
        componentContext.childContext("Members")
    )

    private val courseTimetableComponent = courseTimetableComponent(
        courseId,
        componentContext.childContext("Timetable")
    )

    val capabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = courseId,
        capabilities = listOf()
    ).shareIn(componentScope, SharingStarted.Lazily)

    private val slotNavigation = SlotNavigation<SlotConfig>()
    val childSlot: Value<ChildSlot<SlotConfig, SlotChild>> = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is SlotConfig.CourseEditor -> SlotChild.CourseEditor(
                    courseEditorComponent(
                        { edited ->
                            slotNavigation.dismiss()
                            if (edited)
                                componentScope.launch {
                                    forceUpdate()
                                }
                        },
                        courseId,
                        context
                    )
                )

                SlotConfig.Info -> SlotChild.Info

                SlotConfig.Topics -> TODO()
            }
        }
    )

    @OptIn(ExperimentalDecomposeApi::class)
    private val pagesNavigation = PagesNavigation<TabConfig>()

    @OptIn(ExperimentalDecomposeApi::class)
    val childPages = childPages(
        source = pagesNavigation,
        initialPages = {
            Pages(
                items = listOf(TabConfig.Members, TabConfig.Elements, TabConfig.Timetable),
                selectedIndex = 0
            )
        },
        childFactory = { config, context ->
            when (config) {
                TabConfig.Members -> {
                    TabChild.Members(courseMembersComponent(rootNavigator, courseId, context))
                }

                TabConfig.Elements -> {
                    TabChild.Elements(courseElementsComponent(rootNavigator, courseId, context))
                }

                TabConfig.Timetable -> {
                    TabChild.Timetable(courseTimetableComponent(courseId, context))
                }

                TabConfig.StudyGroups -> TabChild.StudyGroups(courseStudyGroupsComponent(courseId, context))
            }
        }
    )

//    private val stackNavigation = StackNavigation<Config>()
//    val childStack = childStack(
//        source = stackNavigation,
//        handleBackButton = true,
//        initialConfiguration = Config.None,
//        childFactory = { config, context ->
//            when (config) {
//                is Config.Topics -> Child.Topics(
//                    courseTopicsComponent(courseId, context)
//                )
//
//                is Config.CourseEditor -> Child.CourseEditor(
//                    courseEditorComponent({ edit ->
//                        stackNavigation.pop()
//                        if (edit) forceUpdate()
//                    }, courseId, context)
//                )
//
//                Config.CourseStudyGroupsEditor -> Child.CourseStudyGroups(
//                    courseStudyGroupsComponent(courseId, context)
//                )
//
//                is Config.CourseWorkEditor -> Child.CourseWorkEditor(
//                    courseWorkEditorComponent(
//                        stackNavigation::pop,
//                        courseId,
//                        config.workId,
//                        null,
//                        context
//                    )
//                )
//
//                is Config.CourseWork -> Child.CourseWork(
//                    courseWorkComponent(
//                        { _, workId -> stackNavigation.push(Config.CourseWorkEditor(workId)) },
//                        stackNavigation::pop,
//                        courseId, config.workId,
//                        context
//                    )
//                )
//
//                is Config.CourseMaterial -> Child.CourseMaterial(
//                    _courseMaterialComponent(
//                        stackNavigation::pop,
//                        { courseId, materialId ->
//                            stackNavigation.bringToFront(
//                                Config.CourseMaterialEditor(materialId)
//                            )
//                        },
//                        courseId,
//                        config.materialId,
//                        context
//                    )
//                )
//
//                is Config.CourseMaterialEditor -> Child.CourseWorMaterialEditor(
//                    courseMaterialEditorComponent(
//                        { stackNavigation.pop() },
//                        courseId,
//                        config.materialId,
//                        null,
//                        context
//                    )
//                )
//
//                Config.None -> Child.None
//            }
//        }
//    )

    private fun forceUpdate() {
        componentScope.launch { trigger.send(Unit) }
    }

    @OptIn(ExperimentalDecomposeApi::class)
    fun onTabSelect(index: Int) {
        pagesNavigation.select(index)
    }

    fun onInfoClick() {
        slotNavigation.activate(SlotConfig.Info)
    }

    fun onEditClick() {
        slotNavigation.activate(SlotConfig.CourseEditor)
    }

    fun onDialogClose() {
        slotNavigation.dismiss()
    }

    fun onTopicsClick() {
        slotNavigation.activate(SlotConfig.Topics)
    }

    fun onBack() {
        rootNavigator.pop()
    }

    @Parcelize
    sealed class TabConfig : Parcelable {
        data object Elements : TabConfig() {
            private fun readResolve(): Any = Elements
        }

        data object Members : TabConfig() {
            private fun readResolve(): Any = Members
        }

        data object Timetable : TabConfig() {
            private fun readResolve(): Any = Timetable
        }

        data object StudyGroups : TabConfig() {
            private fun readResolve(): Any = StudyGroups
        }
    }

    sealed class TabChild(val title: String) {

        class Elements(val component: CourseElementsComponent) : TabChild("Элементы")

        class Members(val component: CourseMembersComponent) : TabChild("Участники")

        class Timetable(val component: CourseTimetableComponent) : TabChild("Расписание")

        class StudyGroups(val component: CourseStudyGroupsComponent) : TabChild("Группы")
    }

    @Parcelize
    sealed class SlotConfig : Parcelable {
        data object CourseEditor : SlotConfig() {
            private fun readResolve(): Any = CourseEditor
        }

        data object Info : SlotConfig() {
            private fun readResolve(): Any = Info
        }

        data object Topics : SlotConfig() {
            private fun readResolve(): Any = Topics
        }
    }

    sealed class SlotChild {
        class CourseEditor(val component: CourseEditorComponent) : SlotChild()
        data object Info : SlotChild()

        class Topics(val component: CourseTopicsComponent) : SlotChild()


    }

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childSlot.isActiveFlow()
    }

    fun onCloseClick() {
        onFinish()
    }

    fun onAddWorkClick() {
        getCourseMembersComponent().onAddWorkClick()
    }

    fun onAddMaterialClick() {
        getCourseMembersComponent().onAddMaterialClick()
    }

    @OptIn(ExperimentalDecomposeApi::class)
    private fun CourseComponent.getCourseMembersComponent(): CourseElementsComponent {
        return childPages.value.items.first { it.instance is TabChild.Elements }
            .let { (it.instance as TabChild.Elements) }.component
    }
}

fun CourseComponent.TabConfig.tabTitle() = when (this) {
    CourseComponent.TabConfig.Elements -> "Элементы"
    CourseComponent.TabConfig.Members -> "Участники"
    CourseComponent.TabConfig.Timetable -> "Расписание"
    CourseComponent.TabConfig.StudyGroups -> "Группы"
}
