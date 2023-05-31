package com.denchic45.kts.ui.courseeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.ChildOverlay
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.decompose.value.Value
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.Field
import com.denchic45.kts.FieldEditor
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.AddCourseUseCase
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.domain.usecase.UpdateCourseUseCase
import com.denchic45.kts.ui.chooser.SubjectChooserComponent
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.confirm.ConfirmState
import com.denchic45.kts.ui.navigation.OverlayChildrenContainer
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.updateOldValues
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.util.optPropertyOf
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseEditorComponent(
    private val addCourseUseCase: AddCourseUseCase,
    private val updateCourseUseCase: UpdateCourseUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val findCourseByIdUseCase: FindCourseByIdUseCase,
    private val _subjectChooserComponent: ((SubjectResponse) -> Unit, ComponentContext) -> SubjectChooserComponent,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val courseId: UUID?,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext,
    OverlayChildrenContainer<CourseEditorComponent.DialogConfig, CourseEditorComponent.DialogChild> {

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

    override val overlayNavigation = OverlayNavigation<DialogConfig>()
    override val childOverlay: Value<ChildOverlay<DialogConfig, DialogChild>> = childOverlay(
        handleBackButton = true,
        source = overlayNavigation,
        childFactory = { config, componentContext ->
            when (config) {
                DialogConfig.SubjectChooser -> DialogChild.SubjectChooser(_subjectChooserComponent({ response ->
                    editingState.subject = SelectedSubject(
                        subjectId = response.id,
                        subjectName = response.name,
                        subjectIconUrl = response.iconUrl
                    )
                    updateEnableSave()
                    overlayNavigation.dismiss()
                }, componentContext))
            }
        }
    )

    private val editingState = EditingCourse()

    val isNew: Boolean = courseId == null
    val saveEnabled = MutableStateFlow(false)

    @Stable
    class EditingCourse {
        var name: String by mutableStateOf("")
        var subject: SelectedSubject? by mutableStateOf(null)

        var nameMessage: String? by mutableStateOf(null)
    }

    data class SelectedSubject(
        val subjectId: UUID,
        val subjectName: String,
        val subjectIconUrl: String,
    )

//    val uiState = MutableStateFlow<Resource<EditingCourse>>(Resource.Loading)

//    private val typedSubjectName = MutableSharedFlow<String>()

//    val showFoundSubjects = typedSubjectName
//        .map { name -> findSubjectByContainsNameUseCase(name) }
//        .stateInResource(componentScope)

//    val subjectNameTypeEnable = MutableLiveData<Boolean>()

    //    private val courseId: UUID? = _courseId?.toUUID()
//    private var foundSubjects: List<SubjectResponse>? = null

    private val fieldEditor = FieldEditor(mapOf(
        "name" to Field("") { editingState.name },
        "subject" to Field(null) { editingState.subject }
    ))

    private val validator = CompositeValidator(
        validators = listOf(
            ValueValidator(
                value = editingState::name,
                conditions = listOf(Condition(String::isNotEmpty))
            )
        )
    )

//    private val uiValidator: UIValidator = UIValidator.of(
//        Validation(Rule({ subjectId.isNullOrEmpty() }, "Предмет отсутствует"))
//    )


//    private fun setupForExistItem() {
//        if (courseId != null) {
//            componentScope.launch {
//                findCourseByIdUseCase(courseId).onSuccess { course ->
//                    uiState.updateResource {
//                        EditingCourse(
//                            name = course.name,
//                            subjectId = course.subject?.id,
//                            subjectName = course.subject?.name,
//                            subjectIconUrl = course.subject?.iconUrl
//                        )
//                    }
//                    uiEditor.oldItem = uiState.value
//                }
//            }
//        }
//    }

    val viewState = (courseId?.let {
        flow<Resource<EditingCourse>> {
            emit(findCourseByIdUseCase(it).map { response ->
                fieldEditor.updateOldValues(
                    "name" to response.name,
                    "subject" to response.subject
                )
                editingState.apply {
                    name = response.name
                    subject = response.subject?.let {
                        SelectedSubject(it.id, it.name, it.iconUrl)
                    }
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
                    is Resource.Success -> withContext(Dispatchers.Main.immediate) {
                        onFinish()
                    }
                }
            }
    }

    fun onCourseNameType(name: String) {
        editingState.name = name
        updateEnableSave()
    }

    private fun updateEnableSave() {
        saveEnabled.update { fieldEditor.hasChanges() }
    }

    private fun onDeleteClick() {
        componentScope.launch {
            if (fieldEditor.hasChanges() || courseId != null) {
                confirmDialogInteractor.set(
                    ConfirmState(
                        uiTextOf("Удаление курса"),
                        uiTextOf("Удаленный курс нельзя будет восстановить")
                    )
                )
                if (confirmDialogInteractor.receiveConfirm()) {
                    removeCourse()
                }
            }
            onFinish()
        }
    }

//    private fun confirmFinish() {
//        viewModelScope.launch {
//            when {
//                uiEditor.isNew -> {
//                    openConfirmation(Pair("Закрыть редактор курса", "Новый курс не будет сохранен"))
//                    if (confirmDialogInteractor.receiveConfirm()) {
//                        finish()
//                    }
//                }
//
//                uiEditor.hasBeenChanged() -> {
//                    openConfirmation(
//                        "Закрыть редактор курса" to
//                                "Изменения курса не будут сохранены"
//                    )
//                    if (confirmDialogInteractor.receiveConfirm()) {
//                        finish()
//                    }
//                }
//
//                else -> finish()
//            }
//        }
//    }

    private suspend fun removeCourse() {
//        when (interactor.removeCourse(courseId!!)) {
//            is Resource.Loading -> {}
//            is Resource.Success -> finish()
//            is Resource.Error -> {}
//        }
    }

    fun onSubjectChoose() {
        overlayNavigation.activate(DialogConfig.SubjectChooser)
    }

    fun onSubjectClose() {
        editingState.subject = null
    }

//    override fun onCreateOptions() {
//        super.onCreateOptions()
//        viewModelScope.launch { setMenuItemVisible(R.id.option_course_delete to !uiEditor.isNew) }
//    }

    @Parcelize
    sealed class DialogConfig : Parcelable {
        object SubjectChooser : DialogConfig()
    }

    sealed class DialogChild {
        class SubjectChooser(val component: SubjectChooserComponent) : DialogChild()
    }
}