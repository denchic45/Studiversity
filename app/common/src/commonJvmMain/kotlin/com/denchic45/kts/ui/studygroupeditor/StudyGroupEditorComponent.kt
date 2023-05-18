package com.denchic45.kts.ui.studygroupeditor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.Field
import com.denchic45.kts.FieldEditor
import com.denchic45.kts.domain.*
import com.denchic45.kts.domain.usecase.AddStudyGroupUseCase
import com.denchic45.kts.domain.usecase.FindSpecialtyByContainsNameUseCase
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.domain.usecase.UpdateStudyGroupUseCase
import com.denchic45.kts.getOptProperty
import com.denchic45.kts.ui.ActionMenuItem
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.updateOldValues
import com.denchic45.kts.util.componentScope
import com.denchic45.kts.util.copy
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.stuiversity.api.studygroup.model.AcademicYear
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.denchic45.stuiversity.util.OptionalProperty
import com.denchic45.stuiversity.util.optPropertyOf
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import com.denchic45.uivalidator.experimental2.validator.observable
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
    private val _studyGroupId_: UUID?,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val appBarState = MutableStateFlow(AppBarState(
        title = uiTextOf(_studyGroupId_?.let { "Редактирование группы" } ?: "Создание группы"),
        actions = listOf(
            ActionMenuItem(
                id = "save",
                icon = uiIconOf(Icons.Default.Done),
                enabled = false,
                onClick = ::onSaveClick
            )
        ),
    ))

    private val editingState = EditingStudyGroup()
    val inputState = InputState()

    private val fieldEditor = FieldEditor(mapOf(
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

    val viewState = (_studyGroupId_?.let {
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
        updateEnableSave()
    }

    fun onSpecialtySelect(specialty: SpecialtyResponse?) {
        searchSpecialtiesText.value = ""
        editingState.specialty = specialty
        updateEnableSave()
    }

    fun onSpecialtyNameType(text: String) {
        searchSpecialtiesText.update { text }
        updateEnableSave()
    }

    fun onStartYearType(startYear: Int) {
        editingState.startAcademicYear = startYear
        updateEnableSave()
    }


    fun onEndYearType(endYear: Int) {
        editingState.endAcademicYear = endYear
        updateEnableSave()
    }

    fun onSaveClick() {
        if (validator.validate()) {
            componentScope.launch {
                val resource = _studyGroupId_?.let {
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

    private fun updateEnableSave() {
        appBarState.update { state ->
            state.copy(actions = state.actions.copy {
                val itemIndex = state.actions.indexOfFirst { it.id == "save" }
                this[itemIndex] = this[itemIndex].copy(enabled = fieldEditor.hasChanges())
            })
        }
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

