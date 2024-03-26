package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.uivalidator.validator.observable
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.studygroup.model.AcademicYear
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.optPropertyOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@OptIn(ExperimentalCoroutinesApi::class)
@Inject
class StudyGroupEditorComponent(
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    private val findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
    private val addStudyGroupUseCase: AddStudyGroupUseCase,
    private val updateStudyGroupUseCase: UpdateStudyGroupUseCase,
    private val removeStudyGroupUseCase: RemoveStudyGroupUseCase,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val studyGroupId: UUID?,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val isNew = studyGroupId == null
    private val editingState = EditingStudyGroup(isNew)
    val allowSave = MutableStateFlow(false)

    private val fieldEditor = FieldEditor(
        mapOf(
            "name" to Field(editingState::name),
            "startAcademicYear" to Field(editingState::startAcademicYear),
            "endAcademicYear" to Field(editingState::endAcademicYear),
            "specialtyId" to Field { editingState.specialty?.id }
        ))

    private val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = editingState::name,
                conditions = listOf(Condition(String::isNotEmpty))
            ).observable { valid ->
                editingState.nameMessage = "Имя обязательно".takeUnless { valid }
            },
            ValueValidator(
                value = editingState::startAcademicYear,
                conditions = listOf(Condition { it != 0 })
            ).observable { valid ->
                editingState.startYearMessage = "Год начала обязателен".takeUnless { valid }
            },
            ValueValidator(
                value = editingState::endAcademicYear,
                conditions = listOf(Condition { it != 0 })
            ).observable { valid ->
                editingState.endYearMessage = "Год окончания обязателен".takeUnless { valid }
            },
        )
    )

    val viewState = (studyGroupId?.let {
        findStudyGroupByIdUseCase(it).mapResource { response ->
            editingState.apply {
                name = response.name
                specialty = response.specialty
                startAcademicYear = response.academicYear.start
                endAcademicYear = response.academicYear.end
                fieldEditor.updateOldValues()
            }
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)

    private val specialtyQuery = MutableStateFlow("")

    init {
        specialtyQuery.filter(String::isNotEmpty)
            .flatMapLatest(findSpecialtyByContainsNameUseCase::invoke)
            .onEach { resource -> resource.onSuccess { editingState.foundSpecialties = it } }
            .launchIn(componentScope)
    }

    fun onNameChange(name: String) {
        editingState.name = name
        updateAllowSave()
    }

    fun onStartYearChange(startYear: Int) {
        editingState.startAcademicYear = startYear
        updateAllowSave()
    }


    fun onEndYearChange(endYear: Int) {
        editingState.endAcademicYear = endYear
        updateAllowSave()
    }

    fun onSpecialtyQueryChange(text: String) {
        editingState.specialtyQuery = text
        specialtyQuery.update { text }
        updateAllowSave()
    }

    fun onSpecialtySelect(specialty: SpecialtyResponse?) {
        specialtyQuery.value = ""
        editingState.specialtyQuery = ""
        editingState.specialty = specialty
        updateAllowSave()
    }

    fun onSaveClick() {
        if (validator.validate()) {
            componentScope.launch {
                val resource = studyGroupId?.let {
                    updateStudyGroupUseCase(
                        it, UpdateStudyGroupRequest(
                            name = fieldEditor.getOptProperty("name"),
                            academicYear = if (fieldEditor.fieldChanged("startAcademicYear") || fieldEditor.fieldChanged(
                                    "endAcademicYear"
                                )
                            ) {
                                optPropertyOf(
                                    AcademicYear(
                                        editingState.startAcademicYear,
                                        editingState.endAcademicYear
                                    )
                                )
                            } else OptionalProperty.NotPresent,
                            specialtyId = fieldEditor.getOptProperty("specialtyId")
                        )
                    )
                } ?: addStudyGroupUseCase(
                    CreateStudyGroupRequest(
                        name = editingState.name,
                        academicYear = AcademicYear(
                            editingState.startAcademicYear,
                            editingState.endAcademicYear
                        ),
                        specialtyId = editingState.specialty?.id
                    )
                )

                withContext(Dispatchers.Main) {
                    resource.onSuccess {
                        onFinish()
                    }.onFailure {
                        println("Not save study group: $it")
                    }
                }
            }
        }
    }

    fun onDismissClick() {
        onFinish()
    }

    private fun updateAllowSave() {
        allowSave.update { fieldEditor.hasChanges() }
    }

    fun onRemoveStudyGroupClick() {
        componentScope.launch {
            removeStudyGroupUseCase(studyGroupId!!).onSuccess { onFinish() }
        }
    }
}

@Stable
class EditingStudyGroup(val isNew: Boolean) {
    var name: String by mutableStateOf("")
    var specialty: SpecialtyResponse? by mutableStateOf(null)
    var startAcademicYear: Int by mutableStateOf(0)
    var endAcademicYear: Int by mutableStateOf(0)

    var specialtyQuery: String by mutableStateOf("")
    var foundSpecialties: List<SpecialtyResponse> by mutableStateOf(emptyList())

    var nameMessage: String? by mutableStateOf(null)
    var startYearMessage: String? by mutableStateOf(null)
    var endYearMessage: String? by mutableStateOf(null)
}

