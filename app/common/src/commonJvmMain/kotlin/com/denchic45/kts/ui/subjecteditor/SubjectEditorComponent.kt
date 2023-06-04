package com.denchic45.kts.ui.subjecteditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.overlay.OverlayNavigation
import com.arkivanov.decompose.router.overlay.activate
import com.arkivanov.decompose.router.overlay.childOverlay
import com.arkivanov.decompose.router.overlay.dismiss
import com.arkivanov.essenty.parcelable.Parcelable
import com.arkivanov.essenty.parcelable.Parcelize
import com.denchic45.kts.Field
import com.denchic45.kts.FieldEditor
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.mapResource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.AddSubjectUseCase
import com.denchic45.kts.domain.usecase.FindSubjectByIdUseCase
import com.denchic45.kts.domain.usecase.UpdateSubjectUseCase
import com.denchic45.kts.getOptProperty
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.subjecticons.SubjectIconsComponent
import com.denchic45.kts.updateOldValues
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
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
    private val updateSubjectUseCase: UpdateSubjectUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
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

    private val overlayNavigation = OverlayNavigation<SubjectIcons>()
    val childOverlay = childOverlay(
        source = overlayNavigation,
        handleBackButton = true,
        childFactory = { _, context ->
            subjectIconsComponent({
                overlayNavigation.dismiss()
                it?.apply(::onIconSelect)
            }, context)
        })

    @Parcelize
    object SubjectIcons : Parcelable

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
            fieldEditor.updateOldValues(
                "name" to response.name,
                "shortname" to response.shortname,
                "iconUrl" to response.iconUrl,
            )
            editingState.apply {
                name = response.name
                shortname = response.shortname
                iconUrl = response.iconUrl
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
//    private fun findColorId(colorName: String): Int {
//        return colorsNames.firstOrNull { name -> name == colorName }
//            ?.let { it.value }
//            ?: R.color.blue
//    }

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