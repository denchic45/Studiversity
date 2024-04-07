package com.denchic45.studiversity.ui.studygroup

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.router.pages.Pages
import com.arkivanov.decompose.router.pages.PagesNavigation
import com.arkivanov.decompose.router.pages.childPages
import com.arkivanov.decompose.router.pages.select
import com.arkivanov.decompose.router.slot.ChildSlot
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.decompose.router.stack.pop
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.api.role.model.Capability
import com.denchic45.studiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.studiversity.domain.resource.ClientError
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.loadingResource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onFailure
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.studiversity.ui.navigation.ChildrenContainer
import com.denchic45.studiversity.ui.navigation.isActiveFlow
import com.denchic45.studiversity.ui.navigator.RootNavigator
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorComponent
import com.denchic45.studiversity.ui.studygroupmembers.StudyGroupMembersComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class StudyGroupComponent(
    checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
    private val studyGroupEditorComponent: (
        onFinish: (Boolean) -> Unit,
        UUID?,
        ComponentContext,
    ) -> StudyGroupEditorComponent,
    studyGroupMembersComponent: (
        rootNavigator: RootNavigator,
        UUID,
        ComponentContext,
    ) -> StudyGroupMembersComponent,
    studyGroupCoursesComponent: (
        rootNavigator: RootNavigator,
        UUID,
        ComponentContext,
    ) -> StudyGroupCoursesComponent,
    studyGroupTimetableComponent: (UUID, ComponentContext) -> StudyGroupTimetableComponent,
    findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    @Assisted
    private val rootNavigator: RootNavigator,
    @Assisted
    private val studyGroupId: UUID,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext, ChildrenContainer {

    private val componentScope = componentScope()

    private val checkUserCapabilities = checkUserCapabilitiesInScopeUseCase(
        scopeId = studyGroupId,
        capabilities = listOf(Capability.WriteStudyGroup)
    ).shareIn(componentScope, SharingStarted.Lazily)

    val allowEdit = checkUserCapabilities
        .mapResource { it.hasCapability(Capability.WriteStudyGroup) }
        .stateInResource(componentScope)

    private val slotNavigation = SlotNavigation<SlotConfig>()
    val childSlot: Value<ChildSlot<SlotConfig, SlotChild>> = childSlot(
        source = slotNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                is SlotConfig.StudyGroupEditor -> SlotChild.StudyGroupEditor(
                    studyGroupEditorComponent(
                        { edited ->
                            slotNavigation.dismiss()
                            if (edited)
                                componentScope.launch {
                                    studyGroup.update {
                                        findStudyGroupByIdUseCase(studyGroupId).last()
                                    }
                                }
                        },
                        config.studyGroupId,
                        context
                    )
                )

                SlotConfig.Info -> SlotChild.Info
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
                items = listOf(TabConfig.Members, TabConfig.Courses, TabConfig.Timetable),
                selectedIndex = 0
            )
        },
        childFactory = { config, context ->
            when (config) {
                TabConfig.Members -> {
                    TabChild.Members(
                        studyGroupMembersComponent(
                            rootNavigator,
                            studyGroupId,
                            context
                        )
                    )
                }

                TabConfig.Courses -> {
                    TabChild.Courses(
                        studyGroupCoursesComponent(
                            rootNavigator,
                            studyGroupId,
                            context
                        )
                    )
                }

                TabConfig.Timetable -> {
                    TabChild.Timetable(studyGroupTimetableComponent(studyGroupId, context))
                }
            }
        }
    )

    val studyGroup = MutableStateFlow<Resource<StudyGroupResponse>>(loadingResource())

    init {
        componentScope.launch {
            studyGroup.emitAll(findStudyGroupByIdUseCase(studyGroupId))
        }

        studyGroup.onEach { resource ->
            resource.onFailure {
                if (it is ClientError) {
                    if (it.response.code == 404) onBack()
                }
            }
        }
    }

    @OptIn(ExperimentalDecomposeApi::class)
    fun onTabSelect(index: Int) {
        pagesNavigation.select(index)
    }

    fun onInfoClick() {
        slotNavigation.activate(SlotConfig.Info)
    }

    fun onEditClick() {
        slotNavigation.activate(SlotConfig.StudyGroupEditor(studyGroupId))
    }

    fun onDialogClose() {
        slotNavigation.dismiss()
    }

    @Parcelize
    sealed class SlotConfig : Parcelable {
        data class StudyGroupEditor(val studyGroupId: UUID) : SlotConfig()

        data object Info : SlotConfig() {
            private fun readResolve(): Any = Info
        }
    }

    sealed class SlotChild {
        class StudyGroupEditor(val component: StudyGroupEditorComponent) : SlotChild()
        data object Info : SlotChild()
    }

    @Parcelize
    sealed class TabConfig : Parcelable {

        data object Members : TabConfig() {
            private fun readResolve(): Any = Members
        }

        data object Courses : TabConfig() {
            private fun readResolve(): Any = Courses
        }

        data object Timetable : TabConfig() {
            private fun readResolve(): Any = Timetable
        }
    }

    sealed class TabChild(val title: String) {
        class Members(val component: StudyGroupMembersComponent) : TabChild("Участники")
        class Courses(val component: StudyGroupCoursesComponent) : TabChild("Курсы")
        class Timetable(val component: StudyGroupTimetableComponent) : TabChild("Расписание")
    }

    override fun hasChildrenFlow(): Flow<Boolean> {
        return childSlot.isActiveFlow()
    }

    fun onBack() {
        rootNavigator.pop()
    }
}

fun StudyGroupComponent.TabConfig.tabTitle() = when (this) {
    StudyGroupComponent.TabConfig.Members -> "Участники"
    StudyGroupComponent.TabConfig.Courses -> "Курсы"
    StudyGroupComponent.TabConfig.Timetable -> "Расписание"
}
