package com.denchic45.kts.ui.courseworkeditor

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.data.domain.model.FileState
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.filterSuccess
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.AddCourseWorkUseCase
import com.denchic45.kts.domain.usecase.DownloadFileUseCase
import com.denchic45.kts.domain.usecase.FindCourseTopicUseCase
import com.denchic45.kts.domain.usecase.FindCourseWorkAttachmentsUseCase
import com.denchic45.kts.domain.usecase.FindCourseWorkUseCase
import com.denchic45.kts.domain.usecase.ObserveCourseTopicsUseCase
import com.denchic45.kts.domain.usecase.RemoveAttachmentFromCourseWorkUseCase
import com.denchic45.kts.domain.usecase.UpdateCourseWorkUseCase
import com.denchic45.kts.domain.usecase.UploadAttachmentToCourseWorkUseCase
import com.denchic45.kts.ui.ActionMenuItem
import com.denchic45.kts.ui.DropdownMenuItem
import com.denchic45.kts.ui.appbar.AppBarState
import com.denchic45.kts.ui.confirm.ConfirmDialogInteractor
import com.denchic45.kts.ui.confirm.ConfirmState
import com.denchic45.kts.ui.model.AttachmentItem
import com.denchic45.kts.ui.model.toAttachmentItems
import com.denchic45.kts.ui.model.toRequest
import com.denchic45.kts.ui.studygroupeditor.Field
import com.denchic45.kts.ui.studygroupeditor.FieldEditor
import com.denchic45.kts.ui.studygroupeditor.getOptProperty
import com.denchic45.kts.ui.uiIconOf
import com.denchic45.kts.ui.uiTextOf
import com.denchic45.kts.util.componentScope
import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
import com.denchic45.stuiversity.util.optPropertyOf
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.uivalidator.experimental2.condition.Condition
import com.denchic45.uivalidator.experimental2.validator.CompositeValidator
import com.denchic45.uivalidator.experimental2.validator.ValueValidator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import okio.Path.Companion.toOkioPath
import java.io.File
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
    private val findCourseTopicUseCase: FindCourseTopicUseCase,
    private val observeCourseTopicsUseCase: ObserveCourseTopicsUseCase,
    private val addCourseWorkUseCase: AddCourseWorkUseCase,
    private val updateCourseWorkUseCase: UpdateCourseWorkUseCase,
    @Assisted
    private val courseId: UUID,
    @Assisted
    private val workId: UUID?,
    @Assisted
    topicId: UUID?,
    @Assisted
    private val onFinish: () -> Unit,
    @Assisted
    private val componentContext: ComponentContext
) : ComponentContext by componentContext {

    private val componentScope = componentScope()

    val appBarState = MutableStateFlow(AppBarState(
        title = workId?.let { uiTextOf("Редактирование задания") }
            ?: uiTextOf("Новый курс"),
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

    private val editingState = EditingWork()

    private val _attachmentItems = workId?.let {
        findCourseWorkAttachmentsUseCase(courseId, workId)
    } ?: emptyFlow()
    private val _addedAttachmentItems = MutableStateFlow<List<AttachmentItem>>(emptyList())
    private val removedAttachmentIds = MutableStateFlow(emptyList<UUID>())

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
        mapOf(
            "name" to Field("", editingState::name),
            "description" to Field("", editingState::description)
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

    fun onNameType(name: String) {
        editingState.name = name
    }

    fun onDescriptionType(name: String) {
        editingState.description = name
    }

    fun onDueDateTimeSelect(dueDate: LocalDate, dueTime: LocalTime) {
        editingState.dueDate = dueDate
        editingState.dueTime = dueTime
    }

    fun onDueDateTimeClear() {
        editingState.dueDate = null
        editingState.dueTime = null
    }

    fun onFilesSelect(selectedFiles: List<File>) {
        _addedAttachmentItems.update {
            it + selectedFiles.map { file ->
                AttachmentItem.FileAttachmentItem(
                    file.name,
                    null, null,
                    FileState.Downloaded,
                    file.toOkioPath()
                )
            }
        }
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
                    item.attachmentId?.let { removedAttachmentId ->
                        removedAttachmentIds.update { it + removedAttachmentId }
                    }
                    _addedAttachmentItems.update { it - item }
                }
            }
        }
    }

    fun onAttachmentClick(position: Int) {
        attachmentItems.value.onSuccess {
            when (val item = it[position]) {
                is AttachmentItem.FileAttachmentItem -> when (item.state) {
                    FileState.Downloaded -> componentScope.launch { openAttachment.emit(item) }
                    FileState.Preview -> componentScope.launch {
                        downloadFileUseCase(item.attachmentId!!)
                        // TODO: Возможно использовать в будущем: открывать файл сразу после его загрузки

//                            .collect {
//                            if (it == FileState.Downloaded)
//                                openAttachment.postValue(item.path.toFile())
//                        }
                    }

                    else -> {}
                }

                is AttachmentItem.LinkAttachmentItem -> componentScope.launch {
                    openAttachment.emit(
                        item
                    )
                }
            }
        }
    }

    private fun onSaveClick() {
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
                            removeAttachmentFromCourseWorkUseCase(courseId, courseWork.id, it)
                        }
                        _addedAttachmentItems.value.map { item ->
                            uploadAttachmentToCourseWorkUseCase(
                                courseId = courseId,
                                workId = courseWork.id,
                                attachmentRequest = item.toRequest()
                            )
                        }
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
                        _addedAttachmentItems.value.map { item ->
                            uploadAttachmentToCourseWorkUseCase(
                                courseId = courseId,
                                workId = courseWork.id,
                                attachmentRequest = item.toRequest()
                            )
                        }
                    }
                }
                result.onSuccess {
                    onFinish()
                }
            }
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


