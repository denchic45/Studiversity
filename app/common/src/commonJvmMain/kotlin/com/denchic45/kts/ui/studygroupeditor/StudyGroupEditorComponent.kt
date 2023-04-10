package com.denchic45.kts.ui.studygroupeditor

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.UIEditor
import com.denchic45.kts.domain.*
import com.denchic45.kts.domain.usecase.FindSpecialtyByContainsNameUseCase
import com.denchic45.kts.domain.usecase.FindStudyGroupByIdUseCase
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.specialty.model.SpecialtyResponse
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class StudyGroupEditorComponent(
    private val studyGroupId: UUID?,
    private val findStudyGroupByIdUseCase: FindStudyGroupByIdUseCase,
    private val findSpecialtyByContainsNameUseCase: FindSpecialtyByContainsNameUseCase,
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    private val editingState = MutableStateFlow(EditingStudyGroup())
    val inputState = MutableStateFlow(InputState())

    private val uiEditor: UIEditor<EditingStudyGroup> = UIEditor(studyGroupId == null) {
        editingState.value
    }

    private val validator = CompositeValidator(
        listOf(
            ValueValidator(
                value = editingState.value::name,
                conditions = listOf(Condition(String::isNotEmpty))
            ),
            ValueValidator(
                value = editingState.value::startAcademicYear,
                conditions = listOf(Condition { it != 0 })
            ),
            ValueValidator(
                value = editingState.value::endAcademicYear,
                conditions = listOf(Condition { it != 0 })
            ),
        )
    )

    val viewState = (studyGroupId?.let {
        flow<Resource<EditingStudyGroup>> {
            findStudyGroupByIdUseCase(it).onSuccess { response ->
                editingState.emit(
                    EditingStudyGroup(
                        name = response.name,
                        specialty = response.specialty,
                        startAcademicYear = response.academicYear.start,
                        endAcademicYear = response.academicYear.end
                    )
                )
                uiEditor.oldItem = editingState.value
                emitAll(editingState.map { Resource.Success(it) })
            }.onFailure {
                emit(Resource.Error(it))
            }
        }
    } ?: editingState.map { Resource.Success(it) }).stateInResource(componentScope)

    val searchSpecialtiesText = MutableStateFlow("")

    val searchedSpecialties = searchSpecialtiesText.filter(String::isNotEmpty).map {
        findSpecialtyByContainsNameUseCase(it)
    }.filterSuccess().map { it.value }

    fun onNameType(name: String) = editingState.update { it.copy(name = name) }

    fun onSpecialtySelect(specialty: SpecialtyResponse?) = editingState.update {
        searchSpecialtiesText.value = ""
        it.copy(specialty = specialty)
    }

    fun onSpecialtyNameType(text: String) {
        searchSpecialtiesText.value = text
    }

    fun onStartYearType(startYear: Int) = editingState.update {
        it.copy(startAcademicYear = startYear)
    }

    fun onEndYearType(endYear: Int) = editingState.update {
        it.copy(endAcademicYear = endYear)
    }

    data class InputState(
        val nameMessage: String = "",
        val startYearMessage: String = "",
        val endYearMessage: String = ""
    )
}

data class EditingStudyGroup(
    val name: String = "",
    val searchedSpecialtiesText:String = "",
    val specialty: SpecialtyResponse? = null,
    val startAcademicYear: Int = 0,
    val endAcademicYear: Int = 0,
)