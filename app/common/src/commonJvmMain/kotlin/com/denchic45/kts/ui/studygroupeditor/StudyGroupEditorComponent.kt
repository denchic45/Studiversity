package com.denchic45.kts.ui.studygroupeditor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.essenty.backhandler.BackCallback
import com.denchic45.kts.domain.*
import com.denchic45.kts.domain.usecase.AddStudyGroupUseCase
import com.denchic45.kts.domain.usecase.FindSpecialtyByContainsNameUseCase
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.domain.usecase.UpdateStudyGroupUseCase
import com.denchic45.kts.ui.ActionMenuItem
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.kts.ui.uiTextOf
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
    private val studyGroupId: UUID?,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val appBarState = MutableStateFlow(AppBarState(
        title = uiTextOf(studyGroupId?.let { "Редактирование группы" } ?: "Создание группы"),
        actions = listOf(
            ActionMenuItem(
                id = "save",
                icon = uiIconOf(Icons.Default.Done),
                enabled = false
            )
        ),
        onActionMenuItemClick = {
            when (it.id) {
                "save" -> onSaveClick()
            }
        }
    ))

    private val editingState = EditingStudyGroup()
    val inputState = InputState()

//    private val uiEditor: UIEditor<EditingStudyGroup> = UIEditor(studyGroupId == null) {
//        editingState
//    }

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

    val viewState = (studyGroupId?.let {
        flow<Resource<EditingStudyGroup>> {
            findStudyGroupByIdUseCase(it).onSuccess { response ->
                editingState.apply {
                    name = response.name
                    specialty = response.specialty
                    startAcademicYear = response.academicYear.start
                    endAcademicYear = response.academicYear.end
                }
                fieldEditor.updateOldValues(
                    "name" to response.name,
                    "startAcademicYear" to response.academicYear.start,
                    "endAcademicYear" to response.academicYear.end,
                    "specialtyId" to response.specialty?.id
                )
                emit(Resource.Success(editingState))
            }.onFailure {
                emit(Resource.Error(it))
            }
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)

    val searchSpecialtiesText = MutableStateFlow("")

    val searchedSpecialties = searchSpecialtiesText.filter(String::isNotEmpty).map {
        findSpecialtyByContainsNameUseCase(it)
    }.filterSuccess().map { it.value }

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
                val resource = studyGroupId?.let {
                    updateStudyGroupUseCase(
                        it, UpdateStudyGroupRequest(
                            name = fieldEditor.getOptProperty("name") ,
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

class Field<T>(
    var oldValue: T,
    val currentValue: () -> T,
) {
    fun hasChanged() = oldValue != currentValue()
}

class FieldEditor constructor(private val fields: Map<String, Field<*>>) {

    fun hasChanges() = fields.any { it.value.hasChanged() }

    fun fieldChanged(name: String) = fields.getValue(name).hasChanged()

    @Suppress("UNCHECKED_CAST")
    fun <T> field(name: String): Field<T> = fields.getValue(name) as Field<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(name: String) = field<T>(name)

    fun <T> ifChanged(name: String, value: () -> T): OptionalProperty<T> {
        return if (fields.getValue(name).hasChanged())
            optPropertyOf(value())
        else OptionalProperty.NotPresent
    }



    fun <T> updateOldValueBy(name: String, oldValue: T) {
        field<T>(name).oldValue = oldValue
    }
}

fun FieldEditor.updateOldValues(vararg fields: Pair<String, *>) {
    fields.forEach { (name, value) ->
        updateOldValueBy(name, value)
    }
}

fun <T> FieldEditor.getOptProperty(name: String): OptionalProperty<T> {
    val field = field<T>(name)
    return if (field.hasChanged())
        optPropertyOf(field.currentValue())
    else OptionalProperty.NotPresent
}

fun <T, V> FieldEditor.getOptProperty(name: String, map: (T) -> V): OptionalProperty<V> {
    val field = field<T>(name)
    return if (field.hasChanged())
        optPropertyOf(map(field.currentValue()))
    else OptionalProperty.NotPresent
}