package com.denchic45.studiversity.ui.coursestudygroups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
import com.denchic45.studiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.studiversity.domain.model.StudyGroupItem
import com.denchic45.studiversity.domain.model.toItem
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.usecase.AttachStudyGroupsToCourseUseCase
import com.denchic45.studiversity.domain.usecase.DetachStudyGroupsToCourseUseCase
import com.denchic45.studiversity.domain.usecase.FindAttachedStudyGroupsByCourseIdUseCase
import com.denchic45.studiversity.ui.search.StudyGroupChooserComponent
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseStudyGroupsComponent(
    private val findAttachedStudyGroupsByCourseIdUseCase: FindAttachedStudyGroupsByCourseIdUseCase,
    private val attachStudyGroupsToCourseUseCase: AttachStudyGroupsToCourseUseCase,
    private val detachStudyGroupsToCourseUseCase: DetachStudyGroupsToCourseUseCase,
    private val studyGroupChooserComponent: (
            (StudyGroupItem?) -> Unit, ComponentContext
    ) -> StudyGroupChooserComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val slotNavigation = SlotNavigation<Config>()

    val childSlot: Value<ChildSlot<Config, Child>> = childSlot(
        source = slotNavigation,
        serializer = Config.serializer(),
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                Config.StudyGroupChooser -> Child.StudyGroupChooser(
                    studyGroupChooserComponent({ studyGroup -> studyGroup?.let(::onStudyGroupAdd) }, context)
                )
            }
        }
    )

    val studyGroups = MutableStateFlow<Resource<List<StudyGroupItem>>>(resourceOf())

    init {
        componentScope.launch {
            studyGroups.emitAll(findAttachedStudyGroupsByCourseIdUseCase(courseId).mapResource {
                it.map(StudyGroupResponse::toItem)
            })
        }
    }

    private fun forceUpdate() {
        componentScope.launch {
            studyGroups.value = findAttachedStudyGroupsByCourseIdUseCase(courseId)
                .mapResource { it.map(StudyGroupResponse::toItem) }.last()
        }
    }

    private fun onStudyGroupAdd(item: StudyGroupItem) {
        slotNavigation.dismiss()
        componentScope.launch {
            attachStudyGroupsToCourseUseCase(courseId, item.id).last()
                .onSuccess {
                    studyGroups.value.onSuccess {
                        studyGroups.value = resourceOf(it + item)
                    }
                }
        }
    }

    fun onStudyGroupRemove(studyGroupId: UUID) {
        componentScope.launch {
            detachStudyGroupsToCourseUseCase(courseId, studyGroupId)
                .onSuccess {
                    studyGroups.value.onSuccess { forceUpdate() }
                }
        }
    }

    fun onAddStudyGroupClick() {
        slotNavigation.activate(Config.StudyGroupChooser)
    }

    @Serializable
    sealed interface Config {
        @Serializable
        data object StudyGroupChooser : Config
    }

    sealed interface Child {
        class StudyGroupChooser(val component: StudyGroupChooserComponent) : Child
    }
}