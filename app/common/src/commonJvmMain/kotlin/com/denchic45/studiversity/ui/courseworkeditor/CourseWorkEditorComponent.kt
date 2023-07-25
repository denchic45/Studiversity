package com.denchic45.studiversity.ui.courseworkeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.domain.model.FileState
import com.denchic45.studiversity.domain.resource.EmptyResource
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.emptyResource
import com.denchic45.studiversity.domain.resource.filterSuccess
import com.denchic45.studiversity.domain.resource.map
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.resource.resourceOf
import com.denchic45.studiversity.domain.resource.stateInResource
import com.denchic45.studiversity.domain.resource.suspendBindResources
import com.denchic45.studiversity.domain.usecase.AddCourseWorkUseCase
import com.denchic45.studiversity.domain.usecase.DownloadFileUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseWorkAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseWorkUseCase
import com.denchic45.studiversity.domain.usecase.ObserveCourseTopicsUseCase
import com.denchic45.studiversity.domain.usecase.RemoveAttachmentFromCourseWorkUseCase
import com.denchic45.studiversity.domain.usecase.UpdateCourseElementUseCase
import com.denchic45.studiversity.domain.usecase.UpdateCourseWorkUseCase
import com.denchic45.studiversity.domain.usecase.UploadAttachmentToCourseWorkUseCase
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
import com.denchic45.stuiversity.api.course.element.model.CourseElementResponse
import com.denchic45.stuiversity.api.course.element.model.UpdateCourseElementRequest
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.stuiversity.util.optPropertyOf
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path
import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID


@Inject
class CourseWorkEditorComponent(
    observeCourseTopicsUseCase: ObserveCourseTopicsUseCase,
    private val findCourseWorkUseCase: FindCourseWorkUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
    private val uploadAttachmentToCourseWorkUseCase: UploadAttachmentToCourseWorkUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val removeAttachmentFromCourseWorkUseCase: RemoveAttachmentFromCourseWorkUseCase,
    private val addCourseWorkUseCase: AddCourseWorkUseCase,
    private val updateCourseWorkUseCase: UpdateCourseWorkUseCase,
    private val updateCourseElementUseCase: UpdateCourseElementUseCase,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private var workId: UUID?,
    @Assisted
    topicId: UUID?,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext, EmptyChildrenContainer {

    private val componentScope = componentScope()

    val title = workId?.let { uiTextOf("Редактирование задания") }
        ?: uiTextOf("Новое задание")

    private val editingState = EditingWork()

    val attachmentItems2 = MutableStateFlow(resourceOf(listOf<AttachmentItem>()))

    private val _attachmentItems = workId?.let { id ->
        findCourseWorkAttachmentsUseCase(courseId, id)
    } ?: flowOf(resourceOf(emptyList()))

    private val addedAttachmentItems = MutableStateFlow<List<AttachmentItem>>(emptyList())
    private val removedAttachmentIds = MutableStateFlow(emptyList<UUID>())

    val allowSave = MutableStateFlow(false)

    val openAttachment = MutableSharedFlow<AttachmentItem>()

    val attachmentItems = combine(
        _attachmentItems,
        addedAttachmentItems,
        removedAttachmentIds
    ) { items, addedItems, removedIds ->
        items.map { attachments ->
            (attachments.filter { it.id !in removedIds }.toAttachmentItems() + addedItems)
        }
    }.stateInResource(componentScope)

    private val fieldEditor = FieldEditor(
        mapOf(
            "name" to Field(editingState::name),
            "description" to Field(editingState::description),
            "dueDate" to Field(editingState::dueDate),
            "dueTime" to Field(editingState::dueTime)
        )
    )
    private val topicField = Field { editingState.selectedTopic?.id }

    private val uiValidator = CompositeValidator(
        listOf(
            ValueValidator(
                value = editingState::name,
                conditions = listOf(Condition(String::isNotEmpty))
            )
        )
    )

    val viewState = (workId?.let { workId ->
        flow<Resource<EditingWork>> {
            emit(findCourseWorkUseCase(courseId, workId).map { response ->
                editingState.apply {
                    name = response.name
                    description = response.description ?: ""
                    dueDate = response.dueDate
                    dueTime = response.dueTime

                    response.topicId?.let { topicId ->
                        topicsByCourse.first().onSuccess { topics ->
                            topics.find { it.id == topicId }?.let {
                                DropdownMenuItem(it.id.toString(), uiTextOf(it.name))
                            }
                        }
                    }

                    fieldEditor.updateOldValues(
                        "name" to name,
                        "description" to description,
                        "dueDate" to dueDate,
                        "dueTime" to dueTime,
                        "topicId" to topicId
                    )
                }
            })
        }
    } ?: flowOf(Resource.Success(editingState))).stateInResource(componentScope)

    val savingChangesResource = MutableStateFlow<EmptyResource>(resourceOf(Unit))

    private val topicsByCourse = observeCourseTopicsUseCase(courseId)
        .shareIn(componentScope, SharingStarted.Lazily, replay = 1)

    init {
        // load existing attachments
        workId?.let {
            componentScope.launch {
                findCourseWorkAttachmentsUseCase(courseId, it).first().onSuccess {
                    attachmentItems2.update { it }
                }
            }
        }

        componentScope.launch {
            topicsByCourse.filterSuccess().collect {
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
        allowSave.update { hasChanges() }
    }

    fun onNameType(name: String) {
        editingState.name = name
        updateAllowSave()
    }

    fun onDescriptionType(name: String) {
        editingState.description = name
        updateAllowSave()
    }

    fun onDueDateTimeSelect(dueDate: LocalDate, dueTime: LocalTime) {
        editingState.dueDate = dueDate
        editingState.dueTime = dueTime
        updateAllowSave()
    }

    fun onDueDateTimeClear() {
        editingState.dueDate = null
        editingState.dueTime = null
        updateAllowSave()
    }

    fun onFilesSelect(selectedFiles: List<Path>) {
        addedAttachmentItems.update {
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
                    uiTextOf("Убрать файл?"),
                    uiTextOf("Подтвердите ваш выбор")
                )
            )

            componentScope.launch {
                if (confirmDialogInteractor.receiveConfirm()) {
                    val item = attachments[position]
                    item.attachmentId.let { removedAttachmentId ->
                        removedAttachmentIds.update { it + removedAttachmentId }
                    }
                    addedAttachmentItems.update { it - item }
                    updateAllowSave()
                }
            }
        }
    }

    fun onAttachmentClick(item: AttachmentItem) {
        componentScope.launch {
            when (item) {
                is AttachmentItem.FileAttachmentItem -> when (item.state) {
                    FileState.Downloaded -> openAttachment.emit(item)
                    FileState.Preview, FileState.FailDownload -> downloadFileUseCase(item.attachmentId)
                    // TODO: Возможно использовать в будущем: открывать файл сразу после его загрузки
//                            .collect {
//                            if (it == FileState.Downloaded)
//                                openAttachment.postValue(item.path.toFile())
//                        }


                    else -> {}
                }

                is AttachmentItem.LinkAttachmentItem -> openAttachment.emit(item)
            }
        }
    }

    fun onSaveClick() {
        if (!uiValidator.validate()) return
        componentScope.launch {
            savingChangesResource.value = suspendBindResources {
                if (fieldEditor.hasChanges()) {
                    workId = saveUpdatedWork().bind().id
                }
                if (topicField.hasChanged()) {
                    saveUpdatedTopic().bind()
                }
                if (addedAttachmentItems.value.isNotEmpty())
                    savingChangesResource.update { resourceOf() }
                saveRemovedAttachments().bind()
                saveAddedAttachments().bind()
                savingChangesResource.update { emptyResource() }
            }.onSuccess {
                withContext(Dispatchers.Main) {
                    onFinish()
                }
            }
        }
    }

    private fun hasChanges(): Boolean = fieldEditor.hasChanges()
            || topicField.hasChanged()
            || addedAttachmentItems.value.isNotEmpty()
            || removedAttachmentIds.value.isNotEmpty()

    private suspend fun saveUpdatedTopic(): Resource<CourseElementResponse> {
        return updateCourseElementUseCase(
            courseId = courseId,
            workId = workId!!,
            request = UpdateCourseElementRequest(
                topicId = fieldEditor.getOptProperty("topicId")
            )
        )
    }

    private suspend fun saveUpdatedWork(): Resource<CourseWorkResponse> {
        return workId?.let { workId ->
            updateCourseWorkUseCase(
                courseId = courseId,
                workId = workId,
                request = UpdateCourseWorkRequest(
                    name = fieldEditor.getOptProperty("name"),
                    description = fieldEditor.getOptProperty("description"),
                    dueDate = fieldEditor.getOptProperty("dueDate"),
                    dueTime = fieldEditor.getOptProperty("dueTime"),
                    maxGrade = optPropertyOf(5)
                )
            )
        } ?: addCourseWorkUseCase(
            courseId = courseId,
            request = CreateCourseWorkRequest(
                name = editingState.name,
                description = editingState.description.takeIf(String::isNotEmpty),
                topicId = editingState.selectedTopic?.id?.toUUID(),
                dueDate = editingState.dueDate,
                dueTime = editingState.dueTime,
                workType = CourseWorkType.ASSIGNMENT,
                maxGrade = 5
            )
        )
    }

    private suspend fun saveRemovedAttachments(): EmptyResource {
        val successfullyRemovedAttachmentIds = mutableListOf<UUID>()
        val res = suspendBindResources {
            removedAttachmentIds.value.forEach { attachmentId ->
                removeAttachmentFromCourseWorkUseCase(
                    courseId,
                    workId!!,
                    attachmentId
                ).onSuccess {
                    successfullyRemovedAttachmentIds += attachmentId
                }.bind()
            }
        }
        removedAttachmentIds.update { it - successfullyRemovedAttachmentIds.toSet() }
        return res
    }

    private suspend fun saveAddedAttachments(): EmptyResource {
        val successfullyAddedAttachmentIds = mutableListOf<UUID>()
        val res = suspendBindResources {
            addedAttachmentItems.value.forEach { item ->
                uploadAttachmentToCourseWorkUseCase(
                    courseId = courseId,
                    workId = workId!!,
                    request = item.toRequest()
                ).onSuccess {
                    successfullyAddedAttachmentIds += it.id
                }.bind()
            }
        }
        addedAttachmentItems.update { oldAddedAttachments ->
            oldAddedAttachments.filterNot { item -> item.attachmentId in successfullyAddedAttachmentIds }
        }
        return res
    }

    fun onTopicNameType(name: String) {
        editingState.topicQueryText = name

    }

    fun onTopicSelect(item: DropdownMenuItem) {
        editingState.selectedTopic = item
    }

    @Stable
    class EditingWork {
        var name: String by mutableStateOf("")
        var description: String by mutableStateOf("")
        var dueDate: LocalDate? by mutableStateOf(null)
        var dueTime: LocalTime? by mutableStateOf(null)
        var selectedTopic: DropdownMenuItem? by mutableStateOf(null)

        var foundTopics: List<DropdownMenuItem> by mutableStateOf(emptyList())
        var topicQueryText: String by mutableStateOf("")

        var nameMessage: String? by mutableStateOf(null)
    }
}