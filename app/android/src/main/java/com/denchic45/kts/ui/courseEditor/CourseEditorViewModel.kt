package com.denchic45.kts.ui.courseEditor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.R
import com.denchic45.kts.data.model.domain.*
import com.denchic45.kts.domain.*
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.domain.usecase.FindSubjectByContainsNameUseCase
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.confirm.ConfirmInteractor
import com.denchic45.kts.ui.model.UiImage
import com.denchic45.kts.uieditor.UIEditor
import com.denchic45.kts.uivalidator.Rule
import com.denchic45.kts.uivalidator.UIValidator
import com.denchic45.kts.uivalidator.Validation
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.util.optPropertyOf
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class CourseEditorViewModel @Inject constructor(
    @Named(CourseEditorFragment.COURSE_ID)
    _courseId: String?,
    private val interactor: CourseEditorInteractor,
    private val findSubjectByContainsNameUseCase: FindSubjectByContainsNameUseCase,
    private val confirmInteractor: ConfirmInteractor,
    private val findCourseByIdUseCase: FindCourseByIdUseCase
) : BaseViewModel() {

    data class CourseEditingState(
        val name: String = "",
        val subjectId: UUID?,
        val subjectName: String?,
        val subjectIconUrl: String?,
    )

    val uiState = MutableStateFlow<Resource<CourseEditingState>>(Resource.Loading)

    private val typedSubjectName = MutableSharedFlow<String>()

    val showFoundSubjects = typedSubjectName
        .map { name: String -> findSubjectByContainsNameUseCase(name) }
        .map { resource ->
            resource.map {
                foundSubjects = it
                it.map { (id, name, iconUrl) ->
                    ListItem(
                        id = id.toString(),
                        title = name,
                        icon = UiImage.Url(iconUrl)
                    )
                }
            }
        }

    val subjectNameTypeEnable = MutableLiveData<Boolean>()
    val title = MutableLiveData<String>()

    private val courseId: UUID? = _courseId?.toUUID()
    private var foundSubjects: List<SubjectResponse>? = null
    private val subjectId: String? = null

    private val uiEditor: UIEditor<Resource<CourseEditingState>> = UIEditor(_courseId == null) {
        uiState.value
    }

    init {
        setup()
    }

    private val uiValidator: UIValidator = UIValidator.of(
        Validation(Rule({ subjectId.isNullOrEmpty() }, "Предмет отсутствует"))
    )

    private fun setup() {
        if (uiEditor.isNew) setupForNewItem() else setupForExistItem()
    }

    private fun setupForNewItem() {
        title.value = "Добавить курс"
    }

    private fun setupForExistItem() {
        title.value = "Редактировать курс"
        if (courseId != null) {
            viewModelScope.launch {
                findCourseByIdUseCase(courseId).onSuccess { course ->
                    uiState.updateResource {
                        CourseEditingState(
                            name = course.name,
                            subjectId = course.subject?.id,
                            subjectName = course.subject?.name,
                            subjectIconUrl = course.subject?.iconName
                        )
                    }
                    uiEditor.oldItem = uiState.value
                }
            }
        }
    }

    private fun onSaveClick() {
        (uiState.value as? Resource.Success)?.value?.let { state ->
            viewModelScope.launch {
                val result = if (uiEditor.isNew) {
                    interactor.addCourse(CreateCourseRequest(state.name, state.subjectId))
                } else {
                    interactor.updateCourse(
                        courseId!!, UpdateCourseRequest(
                            optPropertyOf(state.name),
                            optPropertyOf(state.subjectId)
                        )
                    )
                }
                when (result) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> finish()
                }
            }
        }
    }

    fun onSubjectNameType(subjectName: String) {
        viewModelScope.launch { typedSubjectName.emit(subjectName) }
    }


    fun onCourseNameType(name: String) {
        uiState.updateResource {
            enablePositiveBtn()
            it.copy(name = name)
        }
    }

    private fun enablePositiveBtn() {
        viewModelScope.launch {
            delay(500) // Needed for waiting menu of this fragment
            setSaveOptionVisibility(uiValidator.runValidates() && uiEditor.hasBeenChanged())
        }
    }

    private fun setSaveOptionVisibility(visible: Boolean) {
        viewModelScope.launch {
            setMenuItemVisible(R.id.option_course_save to visible)
        }
    }

    fun onSubjectSelect(position: Int) {
        val subject = foundSubjects!![position]
        subjectNameTypeEnable.value = false
        enablePositiveBtn()
        uiState.updateResource {
            it.copy(
                subjectId = subject.id,
                subjectName = subject.name,
                subjectIconUrl = subject.iconName
            )
        }
    }

    fun onSubjectNameClick() {
        subjectNameTypeEnable.value = true
        setSaveOptionVisibility(false)
    }

    fun onSubjectEditClick() {
        subjectNameTypeEnable.value = !(subjectNameTypeEnable.value ?: false)
        enablePositiveBtn()
    }

    fun onSubjectNameFocusChange(focus: Boolean) {
        subjectNameTypeEnable.value = focus
    }

    override fun onCleared() {
        super.onCleared()
        interactor.removeListeners()
    }

    override fun onOptionClick(itemId: Int) {
        when (itemId) {
            android.R.id.home -> {
                confirmFinish()
            }
            R.id.option_course_save -> {
                onSaveClick()
            }
            R.id.option_course_delete -> {
                onDeleteClick()
            }
        }
    }

    private fun onDeleteClick() {
        viewModelScope.launch {
            if (uiEditor.hasBeenChanged() || !uiEditor.isNew) {
                openConfirmation(
                    "Удаление курса" to "Удаленный курс нельзя будет восстановить"
                )
                if (confirmInteractor.receiveConfirm()) {
                    removeCourse()
                }
            } else {
                removeCourse()
            }
        }
    }

    private fun confirmFinish() {
        viewModelScope.launch {
            when {
                uiEditor.isNew -> {
                    openConfirmation(Pair("Закрыть редактор курса", "Новый курс не будет сохранен"))
                    if (confirmInteractor.receiveConfirm()) {
                        finish()
                    }
                }
                uiEditor.hasBeenChanged() -> {
                    openConfirmation(
                        "Закрыть редактор курса" to
                                "Изменения курса не будут сохранены"
                    )
                    if (confirmInteractor.receiveConfirm()) {
                        finish()
                    }
                }
                else -> finish()
            }
        }
    }

    private suspend fun removeCourse() {
        when (interactor.removeCourse(courseId!!)) {
            is Resource.Loading -> {}
            is Resource.Success -> finish()
            is Resource.Error -> {}
        }
    }

    override fun onCreateOptions() {
        super.onCreateOptions()
        viewModelScope.launch { setMenuItemVisible(R.id.option_course_delete to !uiEditor.isNew) }
    }
}