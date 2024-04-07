package com.denchic45.studiversity.ui.subjecteditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.studiversity.api.course.subject.model.UpdateSubjectRequest
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.AddSubjectUseCase
import com.denchic45.studiversity.domain.usecase.FindSubjectByIdUseCase
import com.denchic45.studiversity.domain.usecase.RemoveSubjectUseCase
import com.denchic45.studiversity.domain.usecase.UpdateSubjectUseCase
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.util.componentScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class SubjectEditorComponent(
    private val findSubjectByIdUseCase: FindSubjectByIdUseCase,
    private val addSubjectUseCase: AddSubjectUseCase,
    private val removeSubjectUseCase: RemoveSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    @Assisted
    private val onFinish: (Boolean) -> Unit,
    @Assisted
    private val subjectId: UUID?,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val editingState = EditingSubjectState(subjectId == null)
    val allowSave = MutableStateFlow(false)
    val needToConfirmToDismiss = MutableStateFlow(false)

    private val fieldEditor = FieldEditor(
        mapOf(
            "name" to Field(editingState::name),
            "shortname" to Field(editingState::shortname)
        )
    )
    private val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = editingState::name,
                conditions = listOf(Condition(String::isNotEmpty))
            )
        )
    )

    val stateResource = (subjectId?.let {
        findSubjectByIdUseCase(it).mapResource { response ->
            editingState.apply {
                name = response.name
                shortname = response.shortname.orEmpty()
                fieldEditor.updateOldValues()
            }
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)

    fun onSaveClick() {
        validator.onValid {
            componentScope.launch {
                val result = subjectId?.let {
                    updateSubjectUseCase(
                        it,
                        UpdateSubjectRequest(
                            name = fieldEditor.getOptProperty("name"),
                            shortname = fieldEditor.getOptProperty("shortname")
                        )
                    )
                } ?: addSubjectUseCase(
                    CreateSubjectRequest(
                        name = editingState.name,
                        shortname = editingState.shortname
                    )
                )
                withContext(Dispatchers.Main) {
                    result.onSuccess { onFinish(true) }
                }
            }
        }
    }

    private fun updateAllowSave() {
        allowSave.update { fieldEditor.hasChanges() && validator.validate() }
    }

    fun onNameChange(name: String) {
        editingState.name = name
        updateAllowSave()
    }

    fun onShortnameChange(shortname: String) {
        editingState.shortname = shortname
        updateAllowSave()
    }

    fun onDismissRequest() {
        if (fieldEditor.hasChanges() && !needToConfirmToDismiss.value)
            needToConfirmToDismiss.value = true
        else onFinish(false)
    }

    fun onCancelDismiss() {
        needToConfirmToDismiss.value = false
    }

    fun onRemoveClick() {
        componentScope.launch {
            removeSubjectUseCase(subjectId!!).onSuccess {
                withContext(Dispatchers.Main) {
                    onFinish(true)
                }
            }
        }
    }
}

@Stable
class EditingSubjectState(val isNew: Boolean) {
    var name by mutableStateOf("")
    var shortname by mutableStateOf("")
}