package com.denchic45.kts.ui.subjectEditor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.updateResource
import com.denchic45.kts.domain.usecase.FindSubjectByIdUseCase
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.confirm.ConfirmInteractor
import com.denchic45.kts.ui.iconPicker.IconPickerInteractor
import com.denchic45.kts.uieditor.UIEditor
import com.denchic45.kts.uivalidator.Rule
import com.denchic45.kts.uivalidator.UIValidator
import com.denchic45.kts.uivalidator.Validation
import com.denchic45.kts.util.Colors
import com.denchic45.kts.util.NetworkException
import com.denchic45.kts.util.SameSubjectIconException
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import com.denchic45.stuiversity.util.optPropertyOf
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class SubjectEditorViewModel @Inject constructor(
    @Named(SubjectEditorDialog.SUBJECT_ID) _subjectId: String?,
    private val interactor: SubjectEditorInteractor,
    private val findSubjectByIdUseCase: FindSubjectByIdUseCase,
    private val iconPickerInteractor: IconPickerInteractor,
    private val confirmInteractor: ConfirmInteractor
) : BaseViewModel() {

    data class EditableSubjectState(
        val name: String = "",
        val shortname: String = "",
        val iconUrl: String = ""
    )

    val uiState = MutableStateFlow<Resource<EditableSubjectState>>(Resource.Loading)
    private val _successUiStateValue = (uiState.value as Resource.Success).value

    val title = MutableLiveData<String>()

//    val icon = MutableLiveData<String>()

    val deleteBtnVisibility = MutableLiveData(true)

    val enablePositiveBtn = MutableLiveData(false)

    val currentSelectedColorPosition = MutableLiveData(0)

    val openIconPicker = SingleLiveData<Void>()

    val showColors = MutableLiveData<Pair<List<Int>, Int>>()
    private val uiValidator: UIValidator = UIValidator.of(
        Validation(Rule { uiEditor.hasBeenChanged() }),
        Validation(Rule(_successUiStateValue.name::isNotEmpty, "Нет названия")),
        Validation(Rule(_successUiStateValue.iconUrl::isNotEmpty, "Нет иконки"))
    )

    private val uiEditor: UIEditor<Resource<EditableSubjectState>> = UIEditor(_subjectId == null) {
        uiState.value
    }

    private val subjectId = _subjectId?.toUUID()

    private var colorName = ""

    fun onColorSelect(position: Int) {
        val colorId = Colors.ids[position]
        colorName = Colors.colorNameOfId[colorId]!!
//        colorIcon.value = colorId
        enablePositiveBtn.postValue(uiValidator.runValidates())
    }

    private suspend fun saveChanges() {
        try {
            if (uiEditor.isNew) {
                interactor.add(with(_successUiStateValue) {
                    CreateSubjectRequest(
                        name = name,
                        shortname = shortname,
                        iconUrl = iconUrl
                    )
                })
            } else {
                interactor.update(subjectId!!, with(_successUiStateValue) {
                    UpdateSubjectRequest(
                        name = optPropertyOf(name),
                        shortname = optPropertyOf(shortname),
                        iconUrl = optPropertyOf(iconUrl)
                    )
                })
            }
            finish()
        } catch (e: Exception) {
            when (e) {
                is NetworkException -> {
                    showToast(R.string.error_check_network)
                }
                is SameSubjectIconException -> {
                    showToast("Такая иконка уже используется!")
                }
                else -> e.printStackTrace()
            }

        }
    }

    private fun setupForNewItem() {
        title.value = "Создать предмет"
        colorName = "blue"
        deleteBtnVisibility.value = false
        showColors.value = Pair(Colors.ids, 0)
    }

//    private fun setupForExistItem() {
//        title.value = "Редактировать предмет"
//        viewModelScope.launch {
//            findSubjectByIdUseCase(subjectId!!)
//                .onSuccess { subject ->
//
//                    uiState.updateResource {
//                        EditableSubjectState(
//                            name = subject.name,
//                            shortname = subject.shortname,
//                            iconUrl = subject.iconUrl
//                        )
//                    }
//
//                    uiEditor.oldItem = uiState.value
//
////                    colorIcon.value = Colors.colorIdOfName[subject.colorName]
//
////                    currentSelectedColorPosition.value = IntStream.range(0, Colors.names.size)
////                        .filter { value: Int -> Colors.names[value] == subject.colorName }
////                        .findFirst()
////                        .orElse(-1)
////                    icon.value = subject.iconName
//
////                    Colors.names
////                        .firstOrNull { name -> name == subject.colorName }
////                        ?.let { name ->
////                            colorIcon.value = Colors.colorIdOfName[name]
////                            showColors.setValue(Pair(Colors.ids, Colors.names.indexOf(name)))
////                        }
//
////                    colorName = subject.colorName
//                }.onFailure {
//                    finish()
//                }
//
//        }
//    }

//    private fun findColorId(colorName: String): Int {
//        return colorsNames.firstOrNull { name -> name == colorName }
//            ?.let { it.value }
//            ?: R.color.blue
//    }

    fun onNameType(name: String) {
        uiState.updateResource { it.copy(name = name) }
        enablePositiveBtn.postValue(uiValidator.runValidates())
    }

    fun onPositiveClick() {
        uiValidator.runValidates {
            viewModelScope.launch { saveChanges() }
        }
    }

    fun onDeleteClick() {
        openConfirmation(
            "Удалить предмет" to
                    "Вместе с предметом удалятся принадлежащие ему курсы без возможности восстановления"
        )
        viewModelScope.launch {
            if (confirmInteractor.receiveConfirm()) {
                try {
                    interactor.remove(subjectId!!)
                    finish()
                } catch (e: Exception) {
                    if (e is NetworkException) {
                        showToast(R.string.error_check_network)
                    }
                }
            }
        }
    }

    fun onIconClick() {
        viewModelScope.launch {
            openIconPicker.call()
            iconPickerInteractor.observeSelectedIcon().apply {
                uiState.updateResource { it.copy(iconUrl = this) }
                enablePositiveBtn.postValue(uiValidator.runValidates())
            }
        }
    }

//    init {
//        uiValidator =
//        if (uiEditor.isNew) setupForNewItem() else setupForExistItem()
//    }
}