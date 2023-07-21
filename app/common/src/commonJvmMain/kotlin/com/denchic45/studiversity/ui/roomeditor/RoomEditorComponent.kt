package com.denchic45.studiversity.ui.roomeditor

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
import com.denchic45.studiversity.domain.usecase.AddRoomUseCase
import com.denchic45.studiversity.domain.usecase.FindRoomByIdUseCase
import com.denchic45.studiversity.domain.usecase.UpdateRoomUseCase
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.updateOldValues
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.UpdateRoomRequest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class RoomEditorComponent(
    findRoomByIdUseCase: FindRoomByIdUseCase,
    private val addRoomUseCase: AddRoomUseCase,
    private val updateRoomUseCase: UpdateRoomUseCase,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val roomId: UUID?,
    @Assisted
    componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    private val editingState = EditingRoom(roomId == null)

    val viewState = (roomId?.let {
        findRoomByIdUseCase(it).mapResource { response ->
            fieldEditor.updateOldValues(
                "name" to response.name,
                "shortname" to response.shortname
            )
            editingState.apply {
                name = response.name
                shortname = response.shortname
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
            "name" to Field(editingState::name),
            "shortname" to Field(editingState::shortname)
        )
    )

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
            addRoomUseCase(
                CreateRoomRequest(editingState.name, editingState.shortname)
            )
        } else {
            updateRoomUseCase(
                roomId!!,
                UpdateRoomRequest(
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
    class EditingRoom(val isNew: Boolean) {
        var name by mutableStateOf("")
        var shortname by mutableStateOf("")

        var allowSave by mutableStateOf(false)
    }
}