package com.denchic45.studiversity.ui.subjecteditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.slot.SlotNavigation
import com.arkivanov.decompose.router.slot.activate
import com.arkivanov.decompose.router.slot.childSlot
import com.arkivanov.decompose.router.slot.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.mapResource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.usecase.AddSubjectUseCase
import com.denchic45.studiversity.domain.usecase.FindSubjectByIdUseCase
import com.denchic45.studiversity.domain.usecase.UpdateSubjectUseCase
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.ui.subjecticons.SubjectIconsComponent
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class SubjectEditorComponent(
    private val findSubjectByIdUseCase: FindSubjectByIdUseCase,
    private val addSubjectUseCase: AddSubjectUseCase,
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val subjectIconsComponent: (
        onSelect: (iconUrl: String?) -> Unit,
        ComponentContext
    ) -> SubjectIconsComponent,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val subjectId: UUID?,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    @Stable
    class EditableSubjectState {
        var name by mutableStateOf("")
        var shortname by mutableStateOf("")
        var iconUrl by mutableStateOf<String?>(null)
    }

    private val overlayNavigation = SlotNavigation<SubjectIcons>()
    val childSlot = childSlot(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { _, context ->
            subjectIconsComponent({
                overlayNavigation.dismiss()
                it?.apply(::onIconSelect)
            }, context)
        })

    @Parcelize
    object SubjectIcons : Parcelable {
        private fun readResolve(): Any = SubjectIcons
    }

    private val componentScope = componentScope()

    private val editingState = EditableSubjectState()

    val isNew: Boolean = subjectId == null
    val allowSave = MutableStateFlow(false)

    private val fieldEditor = FieldEditor(
        mapOf(
            "name" to Field(editingState::name),
            "shortname" to Field(editingState::shortname),
            "iconUrl" to Field(editingState::iconUrl)
        )
    )
    private val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = editingState::name,
                conditions = listOf(Condition(String::isNotEmpty))
            ),
            ValueValidator(
                value = editingState::iconUrl,
                conditions = listOf(Condition { !it.isNullOrEmpty() })
            )
        )
    )

    val viewState = (subjectId?.let {
        findSubjectByIdUseCase(it).mapResource { response ->
            editingState.apply {
                name = response.name
                shortname = response.shortname
                iconUrl = response.iconUrl
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
                            shortname = fieldEditor.getOptProperty("shortname"),
                            iconUrl = fieldEditor.getOptProperty("iconUrl")
                        )
                    )
                } ?: addSubjectUseCase(
                    CreateSubjectRequest(
                        name = editingState.name,
                        shortname = editingState.shortname,
                        iconUrl = editingState.iconUrl!!
                    )
                )
                withContext(Dispatchers.Main.immediate) {
                    result.onSuccess { onFinish() }
                }
            }
        }
    }

    private fun updateAllowSave() {
        allowSave.update { fieldEditor.hasChanges() && validator.validate() }
    }

    private fun onIconSelect(iconUrl: String) {
        editingState.iconUrl = iconUrl
        updateAllowSave()
    }

    fun onNameType(name: String) {
        editingState.name = name
        updateAllowSave()
    }

    fun onShortnameType(shortname: String) {
        editingState.shortname = shortname
        updateAllowSave()
    }

    fun onIconClick() {
        overlayNavigation.activate(SubjectIcons)
    }

    fun onCancel() {
        onFinish()
    }
}