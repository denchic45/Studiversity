package com.denchic45.studiversity.ui.courseworkeditor

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.Field
import com.denchic45.studiversity.FieldEditor
import com.denchic45.studiversity.data.domain.model.FileState
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.filterSuccess
import com.denchic45.studiversity.domain.map
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.resourceOf
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.AddCourseWorkUseCase
import com.denchic45.studiversity.domain.usecase.DownloadFileUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseWorkAttachmentsUseCase
import com.denchic45.studiversity.domain.usecase.FindCourseWorkUseCase
import com.denchic45.studiversity.domain.usecase.ObserveCourseTopicsUseCase
import com.denchic45.studiversity.domain.usecase.RemoveAttachmentFromCourseWorkUseCase
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
    private val findCourseWorkUseCase: FindCourseWorkUseCase,
    private val confirmDialogInteractor: ConfirmDialogInteractor,
    private val findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
    private val uploadAttachmentToCourseWorkUseCase: UploadAttachmentToCourseWorkUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val removeAttachmentFromCourseWorkUseCase: RemoveAttachmentFromCourseWorkUseCase,
    observeCourseTopicsUseCase: ObserveCourseTopicsUseCase,
    private val addCourseWorkUseCase: AddCourseWorkUseCase,
    private val updateCourseWorkUseCase: UpdateCourseWorkUseCase,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val workId: UUID?,
    @Assisted
    topicId: UUID?,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext, EmptyChildrenContainer {

    private val componentScope = componentScope()

    val title = workId?.let { uiTextOf("Редактирование задания") }
        ?: uiTextOf("Новое задание")

    private val editingState = EditingWork()

    private val _attachmentItems = workId?.let {
        findCourseWorkAttachmentsUseCase(courseId, workId)
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
            "name" to Field("", editingState::name),
            "description" to Field("", editingState::description),
            "dueDate" to Field(null, editingState::dueDate),
            "dueTime" to Field(null, editingState::dueTime),
            "topicId" to Field(null) { editingState.selectedTopic?.id },
            "addedAttachments" to Field(emptyList()) { _addedAttachmentItems.value },
            "removedAttachments" to Field(emptyList()) { removedAttachmentIds.value }
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

    val viewState = (workId?.let { workId ->
        flow<Resource<EditingWork>> {
            emit(findCourseWorkUseCase(courseId, workId).map { response ->
                editingState.apply {
                    name = response.name
                    description = response.description ?: ""
                    dueDate = response.dueDate
                    dueTime = response.dueTime

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
                        "dueDate" to dueDate,
                        "dueTime" to dueTime,
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
                    // TODO: Возможно использовать в будущем: открывать файл сразу после его загрузки
//                            .collect {
//                            if (it == FileState.Downloaded)
//                                openAttachment.postValue(item.path.toFile())
//                        }
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
                val result = workId?.let { workId ->
                    updateCourseWorkUseCase(
                        courseId, workId, UpdateCourseWorkRequest(
                            name = fieldEditor.getOptProperty("name"),
                            description = fieldEditor.getOptProperty("description"),
                            topicId = fieldEditor.getOptProperty("topicId"),
                            dueDate = fieldEditor.getOptProperty("dueDate"),
                            dueTime = fieldEditor.getOptProperty("dueTime"),
                            maxGrade = optPropertyOf(5)
                        )
                    ).onSuccess { courseWork ->
                        removedAttachmentIds.value.map {
                            removeAttachmentFromCourseWorkUseCase(courseId,courseWork.id, it)
                        }
                        loadAddedAttachments(courseWork)
                    }
                } ?: run {
                    addCourseWorkUseCase(
                        courseId,
                        CreateCourseWorkRequest(
                            name = editingState.name,
                            description = editingState.description.takeIf(String::isNotEmpty),
                            topicId = editingState.selectedTopic?.id?.toUUID(),
                            dueDate = editingState.dueDate,
                            dueTime = editingState.dueTime,
                            workType = CourseWorkType.ASSIGNMENT,
                            maxGrade = 5
                        )
                    ).onSuccess { courseWork ->
                        loadAddedAttachments(courseWork)
                    }
                }
                result.onSuccess {
                    withContext(Dispatchers.Main) {
                        onFinish()
                    }
                }
            }
        }
    }

    private suspend fun loadAddedAttachments(
        courseWork: CourseWorkResponse,
    ) {
        _addedAttachmentItems.value.map { item ->
            uploadAttachmentToCourseWorkUseCase(
                courseId = courseId,
                workId = courseWork.id,
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


