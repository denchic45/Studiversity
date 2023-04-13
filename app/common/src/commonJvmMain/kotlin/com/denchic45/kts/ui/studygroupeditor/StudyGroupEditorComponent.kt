package com.denchic45.kts.ui.studygroupeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.*
import com.denchic45.kts.domain.usecase.FindSpecialtyByContainsNameUseCase
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupEditorComponent(
    @Assisted
    private val studyGroupId: UUID?,
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    private val findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
    @Assisted
    private val componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val editingState = EditingStudyGroup()
    val inputState = MutableStateFlow(InputState())

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
            ),
            ValueValidator(
                value = editingState::startAcademicYear,
                conditions = listOf(Condition { it != 0 })
            ),
            ValueValidator(
                value = editingState::endAcademicYear,
                conditions = listOf(Condition { it != 0 })
            ),
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
    }

    fun onSpecialtySelect(specialty: SpecialtyResponse?) {
        searchSpecialtiesText.value = ""
        editingState.specialty = specialty
    }

    fun onSpecialtyNameType(text: String) {
        searchSpecialtiesText.update { text }
    }

    fun onStartYearType(startYear: Int) {
        editingState.startAcademicYear = startYear
    }


    fun onEndYearType(endYear: Int) {
        editingState.endAcademicYear = endYear
    }

    fun onSaveClick() {}

    data class InputState(
        val nameMessage: String = "",
        val startYearMessage: String = "",
        val endYearMessage: String = "",
    )
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

    fun hasChanged() = fields.any { it.value.hasChanged() }

    @Suppress("UNCHECKED_CAST")
    private fun <T> field(name: String): Field<T> = fields.getValue(name) as Field<T>

    @Suppress("UNCHECKED_CAST")
    fun <T> getValue(name: String) = field<T>(name)

    fun <T> updateOldValueBy(name: String, oldValue: T) {
        field<T>(name).oldValue = oldValue
    }
}

fun FieldEditor.updateOldValues(vararg fields: Pair<String, *>) {
    fields.forEach { (name, value) ->
        updateOldValueBy(name, value)
    }
}