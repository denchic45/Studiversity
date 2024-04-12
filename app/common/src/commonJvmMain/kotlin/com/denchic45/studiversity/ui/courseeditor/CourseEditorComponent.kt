package com.denchic45.studiversity.ui.courseeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.api.course.model.CreateCourseRequest
import com.denchic45.studiversity.api.course.model.UpdateCourseRequest
import com.denchic45.studiversity.api.course.subject.model.SubjectResponse
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.map
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.AddCourseUseCase
import com.denchic45.studiversity.domain.usecase.ArchiveCourseUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseByIdUseCase
import com.denchic45.studiversity.domain.usecase.UpdateCourseUseCase
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.ui.navigation.SlotChildrenContainer
import com.denchic45.studiversity.ui.search.SubjectChooserComponent
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseEditorComponent(
    private val addCourseUseCase: AddCourseUseCase,
    private val updateCourseUseCase: UpdateCourseUseCase,
    private val archiveCourseUseCase: ArchiveCourseUseCase,
    private val findCourseByIdUseCase: FindCourseByIdUseCase,
    private val _subjectChooserComponent: ((SubjectResponse?) -> Unit, ComponentContext) -> SubjectChooserComponent,
    @Assisted
    private val onFinish: (Boolean) -> Unit,
    @Assisted
    private val courseId: UUID?,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SlotChildrenContainer<CourseEditorComponent.DialogConfig, CourseEditorComponent.DialogChild> {

    private val componentScope = componentScope()

    override val slotNavigation = SlotNavigation<DialogConfig>()
    override val childSlot = childSlot(
        handleBackButton = true,
        source = slotNavigation,
        childFactory = { config, componentContext ->
            when (config) {
                DialogConfig.SubjectChooser -> DialogChild.SubjectChooser(_subjectChooserComponent({ response ->
                    response?.let {
                        state.subject = response
                        updateEnableSave()
                    }
                    slotNavigation.dismiss()
                }, componentContext))
            }
        }
    )

    private val subjectChooserComponent = _subjectChooserComponent({ response ->
        response?.let {
            state.subject = response
            updateEnableSave()
        }
        slotNavigation.dismiss()
    }, childContext("SubjectChooser"))

    val isNew: Boolean = courseId == null
    val allowSave = MutableStateFlow(false)
    val needToConfirmToDismiss = MutableStateFlow(false)

    private val state = EditingCourse(isNew)

    private val fieldEditor = FieldEditor(mapOf(
        "name" to Field { state.name },
        "subjectId" to Field { state.subject?.id }
    ))

    private val validator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = state::name,
                conditions = listOf(Condition(String::isNotEmpty))
            )
        )
    )

    val stateResource = (courseId?.let {
        flow<Resource<EditingCourse>> {
            emit(findCourseByIdUseCase(it).map { response ->
                state.apply {
                    name = response.name
                    subject = response.subject
                    fieldEditor.updateOldValues()
                }
            })
        }
    } ?: flowOf(Resource.Success(state))).stateInResource(componentScope)

    init {
        subjectChooserComponent.foundItems.onEach { resource ->
            resource.onSuccess { state.foundSubjects = it }
        }.launchIn(componentScope)
    }

    fun onNameChange(name: String) {
        state.name = name
        updateEnableSave()
    }

    private fun updateEnableSave() {
        allowSave.update { fieldEditor.hasChanges() }
    }

    fun onSubjectQueryChange(text: String) {
        state.subjectQuery = text
        subjectChooserComponent.onQueryChange(text)
    }

    private fun onArchiveClick() {
        if (fieldEditor.hasChanges() || courseId != null) {
            componentScope.launch {
                archiveCourseUseCase(courseId!!).onSuccess {
                    withContext(Dispatchers.Main) { onFinish(true) }
                }
//                confirmDialogInteractor.set(
//                    ConfirmState(
//                        uiTextOf("Архивировать курс?"),
//                        uiTextOf("Курс ")
//                    )
//                )
//                if (confirmDialogInteractor.receiveConfirm()) {
//                    onRemoveClick()
//                }
            }
        }
    }

    fun onSubjectSelect(subject: SubjectResponse) {
        state.subject = subject
        subjectChooserComponent.onQueryChange("")
    }

    fun onSubjectClose() {
        state.subject = null
    }

    fun onDismissRequest() {
        if (fieldEditor.hasChanges() && !needToConfirmToDismiss.value)
            needToConfirmToDismiss.value = true
        else onFinish(false)
    }

    fun onCancelDismiss() {
        needToConfirmToDismiss.value = false
    }

    fun onSaveClick() {
        if (validator.validate())
            componentScope.launch {
                val result = courseId?.let {
                    updateCourseUseCase(
                        courseId,
                        UpdateCourseRequest(
                            fieldEditor.getOptProperty("name"),
                            fieldEditor.getOptProperty("subjectId")
                        )
                    )
                } ?: addCourseUseCase(CreateCourseRequest(state.name, state.subject?.id))

                result.onSuccess { withContext(Dispatchers.Main) { onFinish(true) } }
            }
    }

    @Parcelize
    sealed class DialogConfig : Parcelable {
        data object SubjectChooser : DialogConfig() {
            private fun readResolve(): Any = SubjectChooser
        }
    }

    sealed class DialogChild {
        class SubjectChooser(val component: SubjectChooserComponent) : DialogChild()
    }
}

@Stable
class EditingCourse(val isNew: Boolean) {
    var name: String by mutableStateOf("")
    var subject: SubjectResponse? by mutableStateOf(null)

    var subjectQuery by mutableStateOf("")
    var foundSubjects by mutableStateOf<List<SubjectResponse>>(emptyList())

    var nameMessage: String? by mutableStateOf(null)
}