package com.denchic45.studiversity.ui.specialtyeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.studiversity.api.specialty.model.UpdateSpecialtyRequest
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.AddSpecialtyUseCase
import com.denchic45.studiversity.domain.usecase.FindSpecialtyByIdUseCase
import com.denchic45.studiversity.domain.usecase.RemoveSpecialtyUseCase
import com.denchic45.studiversity.domain.usecase.UpdateSpecialtyUseCase
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class SpecialtyEditorComponent(
    findSpecialtyByIdUseCase: FindSpecialtyByIdUseCase,
    private val addSpecialtyUseCase: AddSpecialtyUseCase,
    private val updateSpecialtyUseCase: UpdateSpecialtyUseCase,
    private val removeSpecialtyUseCase: RemoveSpecialtyUseCase,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val specialtyId: UUID?,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val needToConfirmToDismiss = MutableStateFlow(false)
    private val editingState = EditingSpecialty(specialtyId == null)

    val stateResource = (specialtyId?.let {
        findSpecialtyByIdUseCase(it).mapResource { response ->
            editingState.apply {
                name = response.name
                shortname = response.shortname ?: ""
                fieldEditor.updateOldValues()
            }
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)

    private val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = editingState::name,
                conditions = listOf(Condition(String::isNotEmpty))
            )
        )
    )

    private val fieldEditor = FieldEditor(
        mapOf(
            "name" to Field(editingState::name),
            "shortname" to Field(editingState::shortname)
        )
    )

    fun onRemoveClick() {
        componentScope.launch {
//            confirmDialogInteractor.set(
//                ConfirmState(
//                    uiTextOf("Удалить несколько предметов группы"),
//                    uiTextOf("Вы уверены?")
//                )
//            )
//            if (confirmDialogInteractor.receiveConfirm()) {
//                removeSpecialtyUseCase(specialtyId!!).onSuccess {
//                    onFinish()
//                }
//            }

            removeSpecialtyUseCase(specialtyId!!).onSuccess {
                onFinish()
            }

        }
    }

    fun onNameChange(typedName: String) {
        editingState.apply {
            name = typedName
            updateSaveEnabled()
        }
    }

    private fun updateSaveEnabled() {
        editingState.allowSave = validator.validate() && fieldEditor.hasChanges()
    }

    fun onShortnameChange(typedName: String) {
        editingState.apply {
            shortname = typedName
            updateSaveEnabled()
        }
    }

    fun onDismissRequest() {
        if (fieldEditor.hasChanges() && !needToConfirmToDismiss.value)
            needToConfirmToDismiss.value = true
        else onFinish()
    }

    fun onCancelDismiss() {
        needToConfirmToDismiss.value = false
    }

    fun onSaveClick() {
        validator.onValid {
            componentScope.launch {
                saveChanges()
            }
        }
    }

    private suspend fun saveChanges() {
        val result = if (editingState.isNew) {
            addSpecialtyUseCase(
                CreateSpecialtyRequest(editingState.name, editingState.shortname)
            )
        } else {
            updateSpecialtyUseCase(
                specialtyId!!,
                UpdateSpecialtyRequest(
                    fieldEditor.getOptProperty("name"),
                    fieldEditor.getOptProperty("shortname")
                )
            )
        }
        withContext(Dispatchers.Main.immediate) {
            result.onSuccess { onFinish() }
        }
    }

    @Stable
    class EditingSpecialty(isNew: Boolean) {
        var name by mutableStateOf("")
        var shortname by mutableStateOf("")
        var allowSave by mutableStateOf(false)
        var isNew by mutableStateOf(isNew)
    }
}