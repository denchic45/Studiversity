package com.denchic45.kts.ui.courseEditor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.R
import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.data.model.domain.*
import com.denchic45.kts.data.repository.SameCoursesException
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.model.GroupHeader
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.FindCourseByIdUseCase
import com.denchic45.kts.domain.usecase.FindGroupByContainsNameUseCase
import com.denchic45.kts.domain.usecase.FindSubjectByContainsNameUseCase
import com.denchic45.kts.domain.usecase.FindUserByContainsNameUseCase
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.confirm.ConfirmInteractor
import com.denchic45.kts.ui.model.UiImage
import com.denchic45.kts.uieditor.UIEditor
import com.denchic45.kts.uivalidator.Rule
import com.denchic45.kts.uivalidator.UIValidator
import com.denchic45.kts.uivalidator.Validation
import com.denchic45.kts.util.NetworkException
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
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
    private val findCourseByIdUseCase: FindCourseByIdUseCase,
    private val findGroupByContainsNameUseCase: FindGroupByContainsNameUseCase,
    private val findUserByContainsNameUseCase: FindUserByContainsNameUseCase,
) : BaseViewModel() {

    data class CourseEditingState(
        val name: String = "",
        val subjectName: String = "",
        val subjectIconUrl: String = "",
    )

    val uiState = MutableStateFlow(CourseEditingState())

    private val typedSubjectName = MutableSharedFlow<String>()
    private val typedTeacherName = MutableSharedFlow<String>()

    val selectSubject = MutableLiveData<SubjectResponse>()

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
    private var foundTeachers: List<User>? = null
    private var foundSubjects: List<SubjectResponse>? = null
    private val subjectId: String? = null
    private val teacherId: String? = null

    private val uiEditor: UIEditor<CourseEditingState> = UIEditor(_courseId == null) {
        uiState.value
    }

    init {
        setup()
    }

    private val uiValidator: UIValidator = UIValidator.of(
        Validation(Rule({ subjectId.isNullOrEmpty() }, "Предмет отсутствует")),
        Validation(Rule({ teacherId.isNullOrEmpty() }, "Преподаватель отсутствует"))
    )

    private fun setup() {
//        viewModelScope.launch {
//            try {
//                typedTeacherName
//                    .flatMapLatest { name -> findTeacherByContainsNameUseCase(name) }
//                    .collect { result ->
//                        result.mapBoth( //TODO Refactor
//                            success = {
//                                foundTeachers = it
//                                showFoundTeachers.emit(
//                                    it.map { user: User ->
//                                        ListItem(
//                                            id = user.id,
//                                            title = user.fullName,
//                                            icon = UiImage.Url(user.photoUrl),
//                                            type = ListPopupWindowAdapter.TYPE_AVATAR
//                                        )
//                                    }
//                                )
//                            },
//                            failure = {}
//                        )
//                    }
//            } catch (e: Exception) {
//                if (e is NetworkException) {
//                    showToast(R.string.error_check_network)
//                }
//            }
//        }

        if (uiEditor.isNew) setupForNewItem() else setupForExistItem()
    }

    private fun setupForNewItem() {
        title.value = "Добавить курс"
    }

    private fun setupForExistItem() {
        title.value = "Редактировать курс"
    }

    private val course = flow {
        if (courseId != null) {
            emit(findCourseByIdUseCase(courseId).onSuccess { course ->
                uiEditor.oldItem = course
                uiState.update {
                    CourseEditingState(
                        name = course.name,
                        subjectName = course.subject?.name,
                        subjectIconUrl = course.subject?.iconName
                    )
                }
                selectSubject.value = course.subject
            })
        }
    }

//    private val existCourse: Unit
//        get() {
//            viewModelScope.launch {
//               findCourseByIdUseCase(courseId).onSuccess { course ->
//                        uiEditor.oldItem = course
//                        uiState.update { CourseEditingState(
//                            name =
//                        ) }
//                        selectTeacher.value = course.teacher
//                        selectSubject.value = course.subject
//                        groupHeaders = course.groupHeaders.toMutableList()
//                }
//            }
//        }

    private fun addAdderGroupItem(groupHeaders: List<GroupHeader> = emptyList()): List<DomainModel> =
        groupHeaders.map { ListItem(id = it.id, title = it.name, type = 1) } + ListItem(
            id = "ADD_GROUP",
            title = "Добавить",
            icon = UiImage.IdImage(R.drawable.ic_add)
        )

    private fun onSaveClick() {
        viewModelScope.launch {
            try {
                if (uiEditor.isNew)
                    interactor.addCourse(uiEditor.item)
                else
                    interactor.updateCourse(uiEditor.item)
                finish()
            } catch (e: Exception) {
                when (e) {
                    is NetworkException -> {
                        showToast(R.string.error_check_network)
                    }
                    is SameCoursesException -> {
                        showSnackBar("Такой курс уже существует!")
                    }
                }
                e.printStackTrace()
            }
        }
    }

    fun onSubjectNameType(subjectName: String) {
        viewModelScope.launch { typedSubjectName.emit(subjectName) }
    }

    fun onTeacherNameType(teacherName: String) {
        viewModelScope.launch { typedTeacherName.emit(teacherName) }
    }

    fun onCourseNameType(name: String) {
        nameField.postValue(name)
        enablePositiveBtn()
    }

    fun onTeacherSelect(position: Int) {
        val teacher = foundTeachers!![position]
        teacherNameTypeEnable.value = false
        selectTeacher.value = teacher
        enablePositiveBtn()
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
        selectSubject.value = subject
        enablePositiveBtn()

        if ((nameField.value ?: "").isEmpty()) {
            nameField.value = subject.name
        }
    }

    fun onSubjectNameClick() {
        subjectNameTypeEnable.value = true
        setSaveOptionVisibility(false)
    }

    fun onTeacherNameClick() {
        teacherNameTypeEnable.value = true
        setSaveOptionVisibility(false)
    }

    fun onSubjectEditClick() {
        subjectNameTypeEnable.value = !(subjectNameTypeEnable.value ?: false)
        enablePositiveBtn()
    }

    fun onTeacherEditClick() {
        teacherNameTypeEnable.value = !(teacherNameTypeEnable.value ?: false)
        enablePositiveBtn()
    }

    fun onSubjectNameFocusChange(focus: Boolean) {
        subjectNameTypeEnable.value = focus
    }

    fun onTeacherNameFocusChange(focus: Boolean) {
        teacherNameTypeEnable.value = focus
    }

    override fun onCleared() {
        super.onCleared()
        interactor.removeListeners()
    }

    fun onGroupAddClick() {
        viewModelScope.launch {
            navigateTo(MobileNavigationDirections.actionGlobalGroupChooserFragment())
            findGroupByContainsNameUseCase.receiveSelectedGroup()
                .let { courseGroup ->
                    groupHeaders.add(courseGroup)
                    groupList.value = addAdderGroupItem(groupHeaders)
                    enablePositiveBtn()
                }
        }
    }

    fun onGroupRemoveClick(position: Int) {
        groupHeaders.removeAt(position)
        groupList.value = addAdderGroupItem(groupHeaders)
        enablePositiveBtn()
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
        try {
            finish()
            interactor.removeCourse(uiEditor.item)
        } catch (e: Exception) {
            if (e is NetworkException) {
                showToast(R.string.error_check_network)
            }
        }
    }

    override fun onCreateOptions() {
        super.onCreateOptions()
        viewModelScope.launch { setMenuItemVisible(R.id.option_course_delete to !uiEditor.isNew) }
    }
}