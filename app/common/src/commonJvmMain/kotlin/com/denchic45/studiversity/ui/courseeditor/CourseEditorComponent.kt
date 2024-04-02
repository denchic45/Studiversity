package com.denchic45.studiversity.ui.courseeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.*
import com.arkivanov.decompose.value.Value
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
import com.denchic45.studiversity.ui.navigation.SlotChildrenContainer
import com.denchic45.studiversity.ui.search.SubjectChooserComponent
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.util.componentScope
import com.denchic45.studiversity.util.optPropertyOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
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
    private val _subjectChooserComponent: ((SubjectResponse) -> Unit, ComponentContext) -> SubjectChooserComponent,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val courseId: UUID?,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext,
    SlotChildrenContainer<CourseEditorComponent.DialogConfig, CourseEditorComponent.DialogChild> {

    private val componentScope = componentScope()

//    val appBarState = MutableStateFlow(AppBarState(
//        title = courseId?.let { uiTextOf("Редактирование курса") }
//            ?: uiTextOf("Новый курс"),
//        actions = listOf(
//            ActionMenuItem(
//                id = "save",
//                icon = uiIconOf(Icons.Default.Done),
//                enabled = false,
//                onClick = ::onSaveClick
//            )
//        ),
//    ))

    override val slotNavigation = SlotNavigation<DialogConfig>()
    override val childSlot: Value<ChildSlot<DialogConfig, DialogChild>> = childSlot(
        handleBackButton = true,
        source = slotNavigation,
        childFactory = { config, componentContext ->
            when (config) {
                DialogConfig.SubjectChooser -> DialogChild.SubjectChooser(_subjectChooserComponent({ response ->
                    editingState.subject = SelectedSubject(
                        subjectId = response.id,
                        subjectName = response.name,
                        subjectIconUrl = response.iconUrl
                    )
                    updateEnableSave()
                    slotNavigation.dismiss()
                }, componentContext))
            }
        }
    )

    val isNew: Boolean = courseId == null
    val allowSave = MutableStateFlow(false)
    val needToConfirmToDismiss = MutableStateFlow(false)

    private val editingState = EditingCourse(isNew)

    data class SelectedSubject(
        val subjectId: UUID,
        val subjectName: String,
        val subjectIconUrl: String,
    )

    private val fieldEditor = FieldEditor(mapOf(
        "name" to Field { editingState.name },
        "subject" to Field { editingState.subject }
    ))

    private val validator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = editingState::name,
                conditions = listOf(Condition(String::isNotEmpty))
            )
        )
    )

    val stateResource = (courseId?.let {
        flow<Resource<EditingCourse>> {
            emit(findCourseByIdUseCase(it).map { response ->
                editingState.apply {
                    name = response.name
                    subject = response.subject?.let {
                        SelectedSubject(it.id, it.name, it.iconUrl)
                    }
                    fieldEditor.updateOldValues()
                }
            })
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)

    fun onSaveClick() {
        if (validator.validate())
            componentScope.launch {
                val result = courseId?.let {
                    updateCourseUseCase(
                        it, UpdateCourseRequest(
                            optPropertyOf(editingState.name),
                            optPropertyOf(editingState.subject?.subjectId)
                        )
                    )
                } ?: run {
                    addCourseUseCase(
                        CreateCourseRequest(
                            editingState.name,
                            editingState.subject?.subjectId
                        )
                    )
                }
                when (result) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> withContext(Dispatchers.Main) {
                        onFinish()
                    }
                }
            }
    }

    fun onNameChange(name: String) {
        editingState.name = name
        updateEnableSave()
    }

    private fun updateEnableSave() {
        allowSave.update { fieldEditor.hasChanges() }
    }

    private fun onArchiveClick() {
        if (fieldEditor.hasChanges() || courseId != null) {
            componentScope.launch {
                archiveCourseUseCase(courseId!!).onSuccess {
                    withContext(Dispatchers.Main) { onFinish() }
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

    fun onSubjectChoose() {
        slotNavigation.activate(DialogConfig.SubjectChooser)
    }

    fun onSubjectClose() {
        editingState.subject = null
    }

    fun onDismissRequest() {
        if (fieldEditor.hasChanges() && !needToConfirmToDismiss.value)
            needToConfirmToDismiss.value = true
        else onFinish()
    }

    fun onCancelDismiss() {
        needToConfirmToDismiss.value = false
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
    var subject: CourseEditorComponent.SelectedSubject? by mutableStateOf(null)

    var nameMessage: String? by mutableStateOf(null)
}