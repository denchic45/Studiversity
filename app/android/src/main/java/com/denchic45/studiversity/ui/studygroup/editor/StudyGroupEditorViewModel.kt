package com.denchic45.studiversity.ui.studygroup.editor

//import android.text.TextUtils
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.viewModelScope
//import com.denchic45.studiversity.common.R
//import com.denchic45.studiversity.SingleLiveData
//import com.denchic45.studiversity.data.model.domain.ListItem
//import com.denchic45.studiversity.domain.onFailure
//import com.denchic45.studiversity.domain.onSuccess
//import com.denchic45.studiversity.domain.usecase.*
//import com.denchic45.studiversity.ui.base.BaseViewModel
//import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
//import com.denchic45.studiversity.ui.confirm.ConfirmInteractor
//import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorFragment
//import com.denchic45.studiversity.uieditor.UIEditor
//import com.denchic45.studiversity.uivalidator.Rule
//import com.denchic45.studiversity.uivalidator.UIValidator
//import com.denchic45.studiversity.uivalidator.Validation
//import com.denchic45.studiversity.util.NetworkException
//import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
//import com.denchic45.stuiversity.api.studygroup.model.AcademicYear
//import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
//import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
//import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
//import com.denchic45.stuiversity.util.optPropertyOf
//import com.denchic45.stuiversity.util.toUUID
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.MutableSharedFlow
//import kotlinx.coroutines.flow.flatMapLatest
//import kotlinx.coroutines.flow.map
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//import javax.inject.Named

//@OptIn(ExperimentalCoroutinesApi::class)
//class StudyGroupEditorViewModel @Inject constructor(
//    @Named(StudyGroupEditorFragment.GROUP_ID) _studyGroupId: String?,
//    private val userChooserInteractor: UserChooserInteractor,
//    private val addStudyGroupUseCase: AddStudyGroupUseCase,
//    private val updateStudyGroupUseCase: UpdateStudyGroupUseCase,
//    private val removeStudyGroupUseCase: RemoveStudyGroupUseCase,
//    private val findStudyGroupUseCase: FindStudyGroupUseCase,
//    private val confirmInteractor: ConfirmInteractor,
//    private val findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
//    @Named("courses") val courseList: List<ListItem>
//) : BaseViewModel() {
//    val enableSpecialtyField = MutableLiveData<Boolean>()
//    val nameField = MutableLiveData<String>()
//    val specialtyField = MutableLiveData<SpecialtyResponse?>()
//    val showSpecialties = MutableLiveData<List<ListItem>>()
//    val startYearField = MutableLiveData<Int?>()
//    val endYearField = MutableLiveData<Int?>()
//    val fieldErrorMessage = SingleLiveData<Pair<Int, String?>>()
//    val openTeacherChooser = SingleLiveData<Void>()
//    private val typedSpecialtyByName = MutableSharedFlow<String>()
//    private val uiValidator: UIValidator
//    private val uiEditor: UIEditor<StudyGroupResponse>
//
//    private val studyGroupId = _studyGroupId?.toUUID()
//
//    private var foundSpecialties: List<SpecialtyResponse>? = null
//
//    companion object {
//        const val GROUP_ID = "GroupEditor GROUP_ID"
//    }
//
//    init {
//        viewModelScope.launch {
//            typedSpecialtyByName.flatMapLatest { specialtyName: String ->
//                findSpecialtyByContainsNameUseCase(specialtyName)
//            }.collect { result ->
//                result.onSuccess {
//                    foundSpecialties = it
//                    showSpecialties.postValue(
//                        it.map { specialty ->
//                            ListItem(id = specialty.id, title = specialty.name)
//                        }
//                    )
//                }
//            }
//        }
//        uiEditor = UIEditor(studyGroupId == null) {
//            StudyGroupResponse(
//                id = studyGroupId!!,
//                name = nameField.value ?: "",
//                academicYear = AcademicYear(
//                    startYearField.value!!.toInt(),
//                    endYearField.value!!.toInt()
//                ),
//                specialty = specialtyField.value
//            )
//        }
//        uiValidator = UIValidator.of(
//            Validation(Rule { uiEditor.hasBeenChanged() }),
//            Validation(
//                Rule(
//                    { !TextUtils.isEmpty(uiEditor.item.name) },
//                    "Название группы обязательно"
//                )
//            )
//                .sendMessageResult(R.id.til_group_name, fieldErrorMessage),
//            Validation(
//                Rule(
//                    { specialtyField.value != null },
//                    "Специальность обязательна"
//                )
//            )
//                .sendMessageResult(R.id.til_specialty, fieldErrorMessage),
//            Validation(
//                Rule(
//                    { startYearField.value != null && endYearField != null },
//                    "Курс группы обязателен"
//                )
//            )
//        )
//
//        if (uiEditor.isNew)
//            setupForNewItem()
//        else
//            setupForExistItem()
//
//    }
//
//    private fun setupForNewItem() {
//        toolbarTitle = "Создать группу"
//    }
//
//    private fun setupForExistItem() {
//        existGroup()
//        enableSpecialtyField.value = false
//        toolbarTitle = "Редактировать группу"
//    }
//
//    private fun existGroup() {
//        viewModelScope.launch {
//            findStudyGroupUseCase(studyGroupId!!).collect { resource ->
//                resource.onSuccess { group ->
//                    uiEditor.oldItem = group
//                    nameField.value = group.name
//                    specialtyField.value = group.specialty
//                    startYearField.value = group.academicYear.start
//                    endYearField.value = group.academicYear.end
//                }.onFailure {
//                    showToast(R.string.error_unknown)
//                    finish()
//                }
//            }
//        }
//    }
//
//    fun onStartYearType(startYear: String) {
//        startYearField.value = startYear.toInt()
//    }
//
//    fun onEndYearType(endYear: String) {
//        endYearField.value = endYear.toInt()
//    }
//
//    fun onGroupNameType(name: String) {
//        nameField.postValue(name)
//    }
//
//    fun onSpecialtySelect(position: Int) {
//        specialtyField.value = foundSpecialties!![position]
//    }
//
//    fun onSpecialtyNameType(specialtyName: String) {
//        viewModelScope.launch { typedSpecialtyByName.emit(specialtyName) }
//    }
//
//    fun onBackPress() {
//        if (uiEditor.isNew) confirmExit(
//            Pair(
//                "Отменить создание?",
//                "Новый пользователь не будет сохранен"
//            )
//        ) else confirmExit(
//            Pair("Отменить редактирование?", "Внесенные изменения не будут сохранены")
//        )
//    }
//
//    override fun onOptionClick(itemId: Int) {
//        when (itemId) {
//            R.id.option_group_delete -> confirmDelete()
//        }
//    }
//
//    private fun confirmDelete() {
//        viewModelScope.launch {
//            openConfirmation(
//                "Удаление пользователя" to
//                        "Удаленного пользователя нельзя будет восстановить"
//            )
//            if (confirmInteractor.receiveConfirm()) {
//                try {
//                    finish()
//                    removeStudyGroupUseCase(uiEditor.item.id)
//                } catch (e: Exception) {
//                    if (e is NetworkException) {
//                        showToast(R.string.error_check_network)
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onCreateOptions() {
//        super.onCreateOptions()
//        setMenuItemVisible(R.id.option_group_delete to !uiEditor.isNew)
//    }
//
//    private fun confirmExit(titleWithSubtitlePair: Pair<String, String>) {
//        viewModelScope.launch {
//            openConfirmation(titleWithSubtitlePair)
//            if (confirmInteractor.receiveConfirm())
//                finish()
//        }
//    }
//
//    fun onFabClick() {
//        uiValidator.runValidates { saveChanges() }
//    }
//
//    private fun saveChanges() {
//        viewModelScope.launch {
//            try {
//                if (uiEditor.isNew) {
//                    addStudyGroupUseCase(
//                        CreateStudyGroupRequest(
//                            name = nameField.value!!,
//                            academicYear = AcademicYear(
//                                startYearField.value!!,
//                                endYearField.value!!
//                            ),
//                            specialtyId = specialtyField.value?.id,
//                            curatorId = null
//                        )
//                    )
//                } else {
//                    updateStudyGroupUseCase(
//                        studyGroupId!!, UpdateStudyGroupRequest(
//                            name = optPropertyOf(nameField.value!!),
//                            academicYear = optPropertyOf(
//                                AcademicYear(
//                                    startYearField.value!!,
//                                    endYearField.value!!
//                                )
//                            ),
//                            specialtyId = optPropertyOf(specialtyField.value?.id)
//                        )
//                    )
//                }
//                finish()
//            } catch (e: Exception) {
//                if (e is NetworkException) {
//                    showToast(R.string.error_check_network)
//                }
//            }
//        }
//    }
//}