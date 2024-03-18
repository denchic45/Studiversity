package com.denchic45.studiversity.ui.coursematerialeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.resource.*
import com.denchic45.studiversity.domain.usecase.*
import com.denchic45.studiversity.getOptProperty
import com.denchic45.studiversity.ui.DropdownMenuItem
import com.denchic45.studiversity.ui.confirm.ConfirmDialogInteractor
import com.denchic45.studiversity.ui.confirm.ConfirmState
import com.denchic45.studiversity.ui.model.AttachmentItem
import com.denchic45.studiversity.ui.model.toAttachmentItems
import com.denchic45.studiversity.ui.model.toRequest
import com.denchic45.studiversity.ui.navigation.EmptyChildrenContainer
import com.denchic45.studiversity.ui.uiTextOf
import com.denchic45.studiversity.uivalidator.condition.Condition
import com.denchic45.studiversity.uivalidator.validator.CompositeValidator
import com.denchic45.studiversity.uivalidator.validator.ValueValidator
import com.denchic45.studiversity.updateOldValues
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.course.material.model.CourseMaterialResponse
import com.denchic45.stuiversity.api.course.material.model.CreateCourseMaterialRequest
import com.denchic45.stuiversity.api.course.material.model.UpdateCourseMaterialRequest
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path
import java.util.*


@Inject
class CourseMaterialEditorComponent(
    private val findCourseMaterialUseCase: FindCourseMaterialUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val findCourseMaterialAttachmentsUseCase: FindCourseMaterialAttachmentsUseCase,
    private val addAttachmentToCourseMaterialUseCase: AddAttachmentToCourseMaterialUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val removeAttachmentFromCourseMaterialUseCase: RemoveAttachmentFromCourseMaterialUseCase,
    observeCourseTopicsUseCase: ObserveCourseTopicsUseCase,
    private val addCourseMaterialUseCase: AddCourseMaterialUseCase,
    private val updateCourseMaterialUseCase: UpdateCourseMaterialUseCase,
    @Assisted
    private val onFinish: (CourseMaterialResponse) -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val materialId: UUID?,
    @Assisted
    topicId: UUID?,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext, EmptyChildrenContainer {

    private val componentScope = componentScope()

    val title = materialId?.let { uiTextOf("Редактирование материала") }
        ?: uiTextOf("Новый материал")

    private val editingState = EditingMaterial()

    private val _attachmentItems = materialId?.let {
        findCourseMaterialAttachmentsUseCase(materialId)
    } ?: flowOf(resourceOf(emptyList()))
    private val _addedAttachmentItems = MutableStateFlow<List<AttachmentItem>>(emptyList())
    private val removedAttachmentIds = MutableStateFlow(emptyList<UUID>())

    val allowSave = MutableStateFlow(false)

    val openAttachment = MutableSharedFlow<AttachmentItem>()

    val attachmentItems = combine(
        _attachmentItems,
        _addedAttachmentItems,
        removedAttachmentIds
    ) { items, addedItems, removedIds ->
        items.map { attachments ->
            (attachments.filter { it.id !in removedIds }.toAttachmentItems() + addedItems)
        }
    }.stateInResource(componentScope)

    private val fieldEditor = FieldEditor(
        mapOf<String, Field<*>>(
            "name" to Field(editingState::name),
            "description" to Field(editingState::description),
            "topicId" to Field { editingState.selectedTopic?.id },
            "addedAttachments" to Field(_addedAttachmentItems::value),
            "removedAttachments" to Field(removedAttachmentIds::value)
        )
    )

    private val uiValidator = CompositeValidator(
        listOf(
            ValueValidator(
                value = { editingState.name },
                conditions = listOf(Condition(String::isNotEmpty))
            )
        )
    )

    val viewState = (materialId?.let { materialId ->
        flow<Resource<EditingMaterial>> {
            emit(findCourseMaterialUseCase(materialId).map { response ->
                editingState.apply {
                    name = response.name
                    description = response.description ?: ""

                    response.topicId?.let { topicId ->
                        courseTopics.first().onSuccess { topics ->
                            topics.find { it.id == topicId }?.let {
                                DropdownMenuItem(it.id.toString(), uiTextOf(it.name))
                            }
                        }
                    }


                    fieldEditor.updateOldValues(
                        "name" to name,
                        "description" to description,
                        "topicId" to topicId
                    )
                }
            })
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)


    private val courseTopics = observeCourseTopicsUseCase(courseId)
        .shareIn(componentScope, SharingStarted.Lazily, replay = 1)

    init {
        componentScope.launch {
            courseTopics.filterSuccess().collect {
                editingState.foundTopics = it.value.map { response ->
                    DropdownMenuItem(
                        id = response.id.toString(),
                        title = uiTextOf(response.name)
                    )
                }
            }
        }
    }

    private fun updateAllowSave() {
        allowSave.update { fieldEditor.hasChanges() }
    }

    fun onNameType(name: String) {
        editingState.name = name
        updateAllowSave()
    }

    fun onDescriptionType(name: String) {
        editingState.description = name
        updateAllowSave()
    }

    fun onFilesSelect(selectedFiles: List<Path>) {
        _addedAttachmentItems.update {
            it + selectedFiles.map { path ->
                AttachmentItem.FileAttachmentItem(
                    path.name,
                    null, UUID.randomUUID(),
                    FileState.Downloaded,
                    path
                )
            }
        }
        updateAllowSave()
    }

    fun onAttachmentRemove(position: Int) {
        attachmentItems.value.onSuccess { attachments ->
            confirmDialogInteractor.set(
                ConfirmState(
                    uiTextOf("Убрать файл"),
                    uiTextOf("подтвердите ваш выбор")
                )
            )

            componentScope.launch {
                if (confirmDialogInteractor.receiveConfirm()) {
                    val item = attachments[position]
                    item.attachmentId.let { removedAttachmentId ->
                        removedAttachmentIds.update { it + removedAttachmentId }
                    }
                    _addedAttachmentItems.update { it - item }
                    updateAllowSave()
                }
            }
        }
    }

    fun onAttachmentClick(item: AttachmentItem) {
        when (item) {
            is AttachmentItem.FileAttachmentItem -> when (item.state) {
                FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
                FileState.Preview, FileState.FailDownload -> componentScope.launch {
                    downloadFileUseCase(item.attachmentId)
                }

                else -> {}
            }

            is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
                openAttachment.emit(item)
            }
        }
    }

    fun onSaveClick() {
        if (uiValidator.validate() && fieldEditor.hasChanges()) {
            componentScope.launch {
                val result = materialId?.let { materialId ->
                    updateCourseMaterialUseCase(
                        materialId,
                        UpdateCourseMaterialRequest(
                            name = fieldEditor.getOptProperty("name"),
                            description = fieldEditor.getOptProperty("description"),
                            topicId = fieldEditor.getOptProperty("topicId")
                        )
                    ).onSuccess { material ->
                        removedAttachmentIds.value.map {
                            removeAttachmentFromCourseMaterialUseCase(material.id, it)
                        }
                        loadAddedAttachments(material)
                    }
                } ?: run {
                    addCourseMaterialUseCase(
                        courseId,
                        CreateCourseMaterialRequest(
                            name = editingState.name,
                            description = editingState.description.takeIf(String::isNotEmpty),
                            topicId = editingState.selectedTopic?.id?.toUUID()
                        )
                    ).onSuccess { material ->
                        loadAddedAttachments(material)
                    }
                }
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onFinish(it)
                    }
                }
            }
        }
    }

    private suspend fun loadAddedAttachments(
        courseMaterial: CourseMaterialResponse,
    ) {
        _addedAttachmentItems.value.map { item ->
            addAttachmentToCourseMaterialUseCase(
                materialId = courseMaterial.id,
                request = item.toRequest()
            )
        }
    }

    fun onTopicNameType(name: String) {
        editingState.topicQueryText = name

    }

    fun onTopicSelect(item: DropdownMenuItem) {
        editingState.selectedTopic = item
    }

    @Stable
    class EditingMaterial {
        var name: String by mutableStateOf("")
        var description: String by mutableStateOf("")
        var selectedTopic: DropdownMenuItem? by mutableStateOf(null)

        var foundTopics: List<DropdownMenuItem> by mutableStateOf(emptyList())
        var topicQueryText: String by mutableStateOf("")

        var nameMessage: String? by mutableStateOf(null)
    }
}


