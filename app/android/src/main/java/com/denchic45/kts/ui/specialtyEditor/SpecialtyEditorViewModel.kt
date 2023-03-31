package com.denchic45.kts.ui.specialtyEditor

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.R
import com.denchic45.kts.data.domain.*
import com.denchic45.kts.data.repository.SpecialtyRepository
import com.denchic45.kts.domain.onFailure
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.kts.ui.confirm.ConfirmInteractor
import com.denchic45.kts.uieditor.UIEditor
import com.denchic45.kts.uivalidator.Rule
import com.denchic45.kts.uivalidator.UIValidator
import com.denchic45.kts.uivalidator.Validation
import com.denchic45.kts.util.NetworkException
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import com.denchic45.stuiversity.util.optPropertyOf
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Named

class SpecialtyEditorViewModel @Inject constructor(
    @Named(SpecialtyEditorDialog.SPECIALTY_ID) _specialtyId: String?,
    private val specialtyRepository: SpecialtyRepository,
    private val confirmInteractor: ConfirmInteractor
) : BaseViewModel() {
    val title = MutableLiveData<String>()

    val nameField = MutableStateFlow("")

    val enablePositiveBtn = MutableLiveData(false)

    val deleteBtnVisibility = MutableLiveData(true)

    private val specialtyId = _specialtyId?.toUUID()
    private val uiValidator: UIValidator
    private val uiEditor: UIEditor<SpecialtyResponse> =
        UIEditor(_specialtyId == null) {
            SpecialtyResponse(
                id = this.specialtyId!!,
                name = nameField.value,
                shortname = nameField.value
            )
        }

    private fun setupForNewItem() {
        deleteBtnVisibility.value = false
        title.value = "Создать специальность"
    }

    private fun setupForExistItem() {
        title.value = "Редактировать специальность"
        viewModelScope.launch {
            flowOf(specialtyRepository.findById(specialtyId!!)).collect { resource ->
                resource.onSuccess { specialty ->
                    uiEditor.oldItem = specialty
                    nameField.value = specialty.name
                }.onFailure {
                    finish()
                }
            }
        }
    }

    private suspend fun saveChanges() {
        try {
            if (uiEditor.isNew) {
                specialtyRepository.add(
                    CreateSpecialtyRequest(
                        nameField.value,
                        nameField.value
                    )
                )
            } else {
                specialtyRepository.update(
                    specialtyId!!,
                    UpdateSpecialtyRequest(
                        optPropertyOf(nameField.value),
                        optPropertyOf(nameField.value)
                    )
                )
            }
            finish()
        } catch (e: Exception) {
            if (e is NetworkException) {
                showToast(R.string.error_check_network)
            } else e.printStackTrace()
        }
    }

    fun onPositiveClick() {
        uiValidator.runValidates {
            viewModelScope.launch { saveChanges() }
        }
    }

    fun onDeleteClick() {
        viewModelScope.launch {
            openConfirmation(Pair("Удалить несколько предметов группы", "Вы точно уверены???"))
            if (confirmInteractor.receiveConfirm()) {
                specialtyRepository.remove(specialtyId!!).onSuccess {
                    finish()
                }.onFailure {
                    when (it) {
                        NoConnection -> showToast(R.string.error_check_network)
                        else -> {}
                    }
                }
            }
        }
    }

    fun onNameType(name: String) {
        nameField.value = name
        enablePositiveBtn.postValue(uiValidator.runValidates())
    }

    init {
        uiValidator = UIValidator.of(
            Validation(Rule({ uiEditor.item.name.isNotEmpty() }, "Нет названия!")),
            Validation(Rule { uiEditor.hasBeenChanged() })
        )
        if (uiEditor.isNew) setupForNewItem() else setupForExistItem()
    }
}