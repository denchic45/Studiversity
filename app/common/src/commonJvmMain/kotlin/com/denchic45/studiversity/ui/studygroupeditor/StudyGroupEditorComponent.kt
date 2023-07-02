package com.denchic45.studiversity.ui.studygroupeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.domain.*
import com.denchic45.studiversity.domain.usecase.AddStudyGroupUseCase
import com.denchic45.studiversity.domain.usecase.FindSpecialtyByContainsNameUseCase
import com.denchic45.studiversity.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.studiversity.domain.usecase.UpdateStudyGroupUseCase
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.uivalidator.validator.observable
import com.denchic45.studiversity.updateOldValues
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

@Inject
class StudyGroupEditorComponent(
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    private val findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
    private val addStudyGroupUseCase: AddStudyGroupUseCase,
    private val updateStudyGroupUseCase: UpdateStudyGroupUseCase,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val studyGroupId: UUID?,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val isNew = studyGroupId == null

    private val editingState = EditingStudyGroup()
    val allowSave = MutableStateFlow(false)
    val inputState = InputState()

    private val fieldEditor = FieldEditor(
        mapOf(
            "name" to Field("") { editingState.name },
            "startAcademicYear" to Field(0) { editingState.startAcademicYear },
            "endAcademicYear" to Field(0) { editingState.endAcademicYear },
            "specialtyId" to Field(null) { editingState.specialty?.id }
    ))

    private val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = editingState::name,
                conditions = listOf(Condition(String::isNotEmpty))
            ).observable { valid ->
                inputState.nameMessage = "Имя обязательно".takeUnless { valid }
            },
            ValueValidator(
                value = editingState::startAcademicYear,
                conditions = listOf(Condition { it != 0 })
            ).observable { valid ->
                inputState.startYearMessage = "Год начала обязателен".takeUnless { valid }
            },
            ValueValidator(
                value = editingState::endAcademicYear,
                conditions = listOf(Condition { it != 0 })
            ).observable { valid ->
                inputState.endYearMessage = "Год окончания обязателен".takeUnless { valid }
            },
        )
    )

    val viewState = (studyGroupId?.let {
        findStudyGroupByIdUseCase(it).mapResource { response ->
            fieldEditor.updateOldValues(
                "name" to response.name,
                "startAcademicYear" to response.academicYear.start,
                "endAcademicYear" to response.academicYear.end,
                "specialtyId" to response.specialty?.id
            )
            editingState.apply {
                name = response.name
                specialty = response.specialty
                startAcademicYear = response.academicYear.start
                endAcademicYear = response.academicYear.end
            }
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)

    val searchSpecialtiesText = MutableStateFlow("")

    @OptIn(ExperimentalCoroutinesApi::class)
    val searchedSpecialties = searchSpecialtiesText.filter(String::isNotEmpty)
        .flatMapLatest(findSpecialtyByContainsNameUseCase::invoke)
        .filterSuccess()
        .map { it.value }

    fun onNameType(name: String) {
        editingState.name = name
        updateAllowSave()
    }

    fun onSpecialtySelect(specialty: SpecialtyResponse?) {
        searchSpecialtiesText.value = ""
        editingState.specialty = specialty
        updateAllowSave()
    }

    fun onSpecialtyNameType(text: String) {
        searchSpecialtiesText.update { text }
        updateAllowSave()
    }

    fun onStartYearType(startYear: Int) {
        editingState.startAcademicYear = startYear
        updateAllowSave()
    }


    fun onEndYearType(endYear: Int) {
        editingState.endAcademicYear = endYear
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

    private fun updateAllowSave() {
        allowSave.update { fieldEditor.hasChanges() }
    }

    @Stable
    class InputState {
        var nameMessage: String? by mutableStateOf(null)
        var startYearMessage: String? by mutableStateOf(null)
        var endYearMessage: String? by mutableStateOf(null)
    }
}

@Stable
class EditingStudyGroup {
    var name: String by mutableStateOf("")
    var specialty: SpecialtyResponse? by mutableStateOf(null)
    var startAcademicYear: Int by mutableStateOf(0)
    var endAcademicYear: Int by mutableStateOf(0)
}

