package com.denchic45.studiversity.ui.specialtyeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.AddSpecialtyUseCase
import com.denchic45.studiversity.domain.usecase.FindSpecialtyByIdUseCase
import com.denchic45.studiversity.domain.usecase.RemoveSpecialtyUseCase
import com.denchic45.studiversity.domain.usecase.UpdateSpecialtyUseCase
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.updateOldValues
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.specialty.model.CreateSpecialtyRequest
import com.denchic45.stuiversity.api.specialty.model.UpdateSpecialtyRequest
import com.denchic45.studiversity.uivalidator.experimental2.condition.Condition
import com.denchic45.studiversity.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.experimental2.validator.ValueValidator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SpecialtyEditorComponent(
    findSpecialtyByIdUseCase: FindSpecialtyByIdUseCase,
    private val addSpecialtyUseCase: AddSpecialtyUseCase,
    private val updateSpecialtyUseCase: UpdateSpecialtyUseCase,
    private val removeSpecialtyUseCase: RemoveSpecialtyUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val specialtyId: UUID?,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val editingState = EditingSpecialty(specialtyId == null)

    val viewState = (specialtyId?.let {
        findSpecialtyByIdUseCase(it).mapResource { response ->
            fieldEditor.updateOldValues(
                "name" to response.name,
                "shortname" to response.shortname
            )
            editingState.apply {
                name = response.name
                shortname = response.shortname ?: ""
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
            "name" to Field("", editingState::name),
            "shortname" to Field("", editingState::shortname)
        )
    )

//    fun onDeleteClick() {
//        componentScope.launch {
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
//        }
//    }

    fun onNameType(typedName: String) {
        editingState.apply {
            name = typedName
            updateSaveEnabled()
        }
    }

    private fun updateSaveEnabled() {
        editingState.allowSave = validator.validate() && fieldEditor.hasChanges()
    }

    fun onShortnameType(typedName: String) {
        editingState.apply {
            shortname = typedName
            updateSaveEnabled()
        }
    }

    fun onCloseClick() {
        onFinish()
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