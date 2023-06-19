package com.denchic45.studiversity.ui.coursestudygroups

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.usecase.AttachStudyGroupsToCourseUseCase
import com.denchic45.studiversity.domain.usecase.DetachStudyGroupsToCourseUseCase
import com.denchic45.studiversity.domain.usecase.FindAttachedStudyGroupsByCourseIdUseCase
import com.denchic45.studiversity.ui.search.StudyGroupChooserComponent
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseStudyGroupsComponent(
    private val findAttachedStudyGroupsByCourseIdUseCase: FindAttachedStudyGroupsByCourseIdUseCase,
    private val attachStudyGroupsToCourseUseCase: AttachStudyGroupsToCourseUseCase,
    private val detachStudyGroupsToCourseUseCase: DetachStudyGroupsToCourseUseCase,
    private val studyGroupChooserComponent: (
            (StudyGroupResponse) -> Unit,
            ComponentContext,
    ) -> StudyGroupChooserComponent,
    @Assisted
    private val courseId: UUID,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val overlayNavigation = OverlayNavigation<Config>()

    val childOverlay: Value<ChildOverlay<Config, Child>> = childOverlay(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { config, context ->
            when (config) {
                Config.StudyGroupChooser -> Child.StudyGroupChooser(
                    studyGroupChooserComponent(::onStudyGroupAdd, context)
                )
            }
        }
    )

    val studyGroups = MutableStateFlow<Resource<List<StudyGroupResponse>>>(resourceOf())

    init {
        componentScope.launch {
            studyGroups.emitAll(findAttachedStudyGroupsByCourseIdUseCase(courseId))
        }
    }

    private fun onStudyGroupAdd(studyGroupResponse: StudyGroupResponse) {
        overlayNavigation.dismiss()
        componentScope.launch {
            attachStudyGroupsToCourseUseCase(courseId, studyGroupResponse.id).last()
                .onSuccess {
                    studyGroups.value.onSuccess {
                        studyGroups.value = resourceOf(it + studyGroupResponse)
                    }
                }
        }
    }

    fun onStudyGroupRemove(studyGroupResponse: StudyGroupResponse) {
        componentScope.launch {
            detachStudyGroupsToCourseUseCase(courseId, studyGroupResponse.id).last()
                .onSuccess {
                    studyGroups.value.onSuccess {
                        studyGroups.value = resourceOf(it - studyGroupResponse)
                    }
                }
        }
    }

    fun onAddStudyGroupClick() {
        overlayNavigation.activate(Config.StudyGroupChooser)
    }

    @Parcelize
    sealed interface Config : Parcelable {
        object StudyGroupChooser : Config
    }

    sealed interface Child {
        class StudyGroupChooser(val component: StudyGroupChooserComponent) : Child
    }
}