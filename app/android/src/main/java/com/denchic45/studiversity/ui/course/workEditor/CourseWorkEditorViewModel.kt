package com.denchic45.studiversity.ui.course.workEditor

//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.viewModelScope
//import com.denchic45.studiversity.common.R
//import com.denchic45.studiversity.SingleLiveData
//import com.denchic45.studiversity.data.domain.*
//import com.denchic45.studiversity.data.domain.model.*
//import com.denchic45.studiversity.domain.*
//import com.denchic45.studiversity.domain.usecase.*
//import com.denchic45.studiversity.ui.base.BaseViewModel
//import com.denchic45.studiversity.ui.confirm.ConfirmInteractor
//import com.denchic45.studiversity.ui.model.AttachmentItem
//import com.denchic45.studiversity.uieditor.UIEditor
//import com.denchic45.studiversity.uivalidator.Rule
//import com.denchic45.studiversity.uivalidator.UIValidator
//import com.denchic45.studiversity.uivalidator.Validation
//import com.denchic45.stuiversity.api.course.element.model.CreateFileRequest
//import com.denchic45.stuiversity.api.course.element.model.CreateLinkRequest
//import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
//import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
//import com.denchic45.stuiversity.api.course.work.model.CourseWorkType
//import com.denchic45.stuiversity.api.course.work.model.CreateCourseWorkRequest
//import com.denchic45.stuiversity.api.course.work.model.UpdateCourseWorkRequest
//import com.denchic45.stuiversity.util.optPropertyOf
//import com.denchic45.stuiversity.util.toUUID
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import okio.Path.Companion.toOkioPath
//import java.io.File
//import java.time.*
//import java.time.format.DateTimeFormatter
//import java.util.*
//import javax.inject.Inject
//import javax.inject.Named
//
//class CourseWorkEditorViewModel @Inject constructor(
//    @Named(CourseWorkEditorFragment.WORK_ID) _workId: String?,
//    @Named(CourseWorkEditorFragment.COURSE_ID) private val _courseId: String,
//    @Named(CourseWorkEditorFragment.SECTION_ID) _topicId: String?,
//    private val confirmInteractor: ConfirmInteractor,
//    private val findCourseWorkUseCase: FindCourseWorkUseCase,
//    private val findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
//    private val uploadAttachmentToCourseWorkUseCase: UploadAttachmentToCourseWorkUseCase,
//    private val downloadFileUseCase: DownloadFileUseCase,
//    private val removeAttachmentFromCourseWorkUseCase: RemoveAttachmentFromCourseWorkUseCase,
//    private val findCourseTopicUseCase: FindCourseTopicUseCase,
//    private val addCourseWorkUseCase: AddCourseWorkUseCase,
//    private val updateCourseWorkUseCase: UpdateCourseWorkUseCase,
//) : BaseViewModel() {
//
//    private val courseId = _courseId.toUUID()
//    private val workId = _workId?.toUUID()
//    private val topicId = _topicId?.toUUID()
//
//    val nameField = MutableStateFlow("")
//    val descriptionField = MutableStateFlow<String?>(null)
//    val showCompletionDate = MutableLiveData<String?>()
//    val disabledSendAfterDate = MutableLiveData<Boolean>()
//    val selectedTopic = MutableStateFlow<TopicResponse?>(null)
//
//    val availabilityDateRemoveVisibility = MutableLiveData<Boolean>()
//    val openCourseTopics = SingleLiveData<String>()
//    val filesVisibility = MutableStateFlow(false)
//    val commentsEnabled = MutableLiveData<Boolean>()
//
//    val openFileChooser = SingleLiveData<Unit>()
//    val openAttachment = SingleLiveData<File>()
//    val openDatePicker: SingleLiveData<Long> = SingleLiveData()
//    val openTimePicker: SingleLiveData<Pair<Int, Int>> = SingleLiveData()
//    val showErrorMessage = SingleLiveData<Pair<Int, String?>>()
//
//    private var dueDate: LocalDate? = null
//    private var dueTime: LocalTime? = null
//
//    private val _oldAttachments = MutableStateFlow<List<Attachment2>>(emptyList())
//
//    //    private val _attachments = MutableStateFlow<List<Attachment2>>(emptyList())
//
//    private val _addedAttachmentItems = MutableStateFlow<List<AttachmentItem>>(emptyList())
//    private val _attachmentItems = MutableStateFlow<List<AttachmentItem>>(emptyList())
//    val attachmentItems = combine(_attachmentItems, _addedAttachmentItems) { items, addedItems ->
//        items + addedItems
//    }
//
//    private val _addedAttachmentsRequests = _addedAttachmentItems.map {
//        it.map { attachmentItem ->
//            when (attachmentItem) {
//                is AttachmentItem.FileAttachmentItem -> CreateFileRequest(
//                    attachmentItem.name,
//                    attachmentItem.path.toFile().readBytes()
//                )
//                is AttachmentItem.LinkAttachmentItem -> CreateLinkRequest(attachmentItem.url)
//            }
//        }
//    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)
//
//    private val removedAttachments = mutableListOf<UUID>()
//
//    private var createdAt: LocalDateTime? = null
//
//    private val courseWorkResponse =
//        MutableStateFlow<Resource<CourseWorkResponse>>(Resource.Loading)
//
//    private val uiEditor: UIEditor<Resource<CourseWorkResponse>> = UIEditor(_workId == null) {
//        courseWorkResponse.value
////        Task(
////            this.taskId,
////            courseId,
////            this.topic.id,
////            nameField.value ?: "",
////            descriptionField.value ?: "",
////            order,
////            if (showCompletionDate.value != null) LocalDateTime.parse(
////                showCompletionDate.value,
////                DateTimeFormatter.ofPattern("EE, dd LLLL yyyy, HH:mm")
////            ) else null,
////            disabledSendAfterDate.value ?: false,
////            with(submissionSettings.value) {
////                SubmissionSettings(
////                    textAvailable,
////                    charsLimit.toInt(),
////                    attachmentsAvailable,
////                    attachmentsLimit.toInt(),
////                    attachmentsSizeLimit.toInt()
////                )
////            },
////            commentsEnabled.value ?: false,
////            createdDate,
////            timestamp
////        )
//    }
//
//    private val uiValidator: UIValidator = UIValidator.of(
//        Validation(
//            Rule({ !nameField.value.isNullOrEmpty() }, "Название задания обязательно!")
//        ).sendMessageResult(R.id.til_name, showErrorMessage)
//    )
//
//    init {
//        setTitle("")
//        if (uiEditor.isNew) {
//            setupForNew()
//        } else setupForExist()
//
//        availabilityDateRemoveVisibility.postValue(dueDate != null)
//    }
//
//
//    private fun setupForNew() {
//
//    }
//
//    private fun setupForExist() {
//        viewModelScope.launch {
//            findCourseWorkUseCase(courseId, workId!!).onSuccess { courseWork ->
//                courseWorkResponse.updateResource { courseWork }
//                nameField.value = courseWork.name
//                descriptionField.value = courseWork.description
//                dueDate = courseWork.dueDate
//                dueTime = courseWork.dueTime
//                createdAt = courseWork.createdAt
//                postCompletionDate()
//                disabledSendAfterDate.value = courseWork.submitAfterDueDate
//
//                commentsEnabled.value = false // TODO: stub
//                selectedTopic.value = courseWork.topicId?.let {
//                    findCourseTopicUseCase(courseId, it).first()
//                }
//                findCourseWorkAttachmentsUseCase(courseId, workId).collect {
//                    it.onSuccess { attachments ->
//                        _oldAttachments.value = attachments
//                        _attachmentItems.update {
//                            attachments.map { attachment ->
//                                when (attachment) {
//                                    is FileAttachment2 -> AttachmentItem.FileAttachmentItem(
//                                        name = attachment.name,
//                                        previewUrl = null,
//                                        attachmentId = attachment.id,
//                                        state = attachment.state,
//                                        path = attachment.path
//                                    )
//                                    is LinkAttachment2 -> AttachmentItem.LinkAttachmentItem(
//                                        name = attachment.url,
//                                        previewUrl = null,
//                                        attachmentId = attachment.id,
//                                        url = attachment.url
//                                    )
//                                }
//                            }
//                        }
//                    }
//                }
//
//
//                uiEditor.oldItem = courseWorkResponse.value
//            }.onFailure {
//                when (it) {
//                    is Cause -> TODO()
//                    is ClientError -> TODO()
//                    Forbidden -> TODO()
//                    NoConnection -> TODO()
//                    NotFound -> finish()
//                    ServerError -> TODO()
//                }
//            }
//        }
//
//    }
//
//    fun onAvailabilityDateClick() {
//        dueDate?.also {
//            openDatePicker.postValue(it.atTime(dueTime).toEpochSecond(ZoneOffset.UTC))
//        } ?: run {
//            openDatePicker.postValue(System.currentTimeMillis())
//        }
//    }
//
//    fun onAvailabilityDateSelect(milliseconds: Long) {
//        availabilityDateRemoveVisibility.postValue(true)
//        val dateIsNull = dueDate == null
//        dueDate = Instant.ofEpochMilli(milliseconds).atZone(ZoneId.systemDefault()).toLocalDate()
//        if (dateIsNull)
//            dueTime = LocalTime.of(23, 59)
//        openTimePicker.postValue(dueTime!!.hour to dueTime!!.minute)
//
//        postCompletionDate()
//    }
//
//    private fun postCompletionDate() {
//        dueDate?.also {
//            showCompletionDate.postValue(
//                dueDate!!.format(DateTimeFormatter.ofPattern("EE, dd LLLL yyyy, HH:mm"))
//            )
//        } ?: run {
//            showCompletionDate.postValue(null)
//        }
//    }
//
////    private fun postAttachments(attachments: List<Attachment2>) {
////        filesVisibility.postValue(attachments.isNotEmpty())
////        this._attachments.value = attachments
////    }
//
//    fun onAvailabilityTimeSelect(hour: Int, minute: Int) {
//        dueTime = LocalTime.of(hour, minute)
//        postCompletionDate()
//    }
//
//    fun onRemoveAvailabilityDate() {
//        dueDate = null
//        availabilityDateRemoveVisibility.postValue(false)
//        postCompletionDate()
//    }
//
//    override fun onOptionClick(itemId: Int) {
//        when (itemId) {
//            R.id.option_attachment -> {
//                openFileChooser.call()
//            }
//            R.id.option_save_task -> onSaveClick()
//        }
//    }
//
//    fun onFilesSelect(selectedFiles: List<File>) {
//        _addedAttachmentItems.update {
//            it + selectedFiles.map { file ->
//                AttachmentItem.FileAttachmentItem(
//                    file.name,
//                    null, null,
//                    FileState.Downloaded,
//                    file.toOkioPath()
//                )
//            }
//        }
//    }
//
//    fun onRemoveAttachmentClick(position: Int) {
//        openConfirmation("Убрать файл" to "подтвердите ваш выбор")
//        viewModelScope.launch {
//            if (confirmInteractor.receiveConfirm()) {
//                val item = _attachmentItems.value[position]
//                item.attachmentId?.let {
//                    removedAttachments.add(it)
//                }
//                _attachmentItems.update { it.minusElement(it[position]) }
//            }
//        }
//    }
//
//    fun onAttachmentClick(position: Int) {
//        when (val item = _attachmentItems.value[position]) {
//            is AttachmentItem.FileAttachmentItem -> when (item.state) {
//                FileState.Downloaded -> openAttachment.postValue(item.path.toFile())
//                FileState.Preview -> viewModelScope.launch {
//                    downloadFileUseCase(item.attachmentId!!).collect {
//                        openAttachment.postValue(item.path.toFile())
//                    }
//                }
//                else -> {}
//            }
//            is AttachmentItem.LinkAttachmentItem -> TODO()
//        }
//    }
//
//    fun onAvailabilitySendCheck(check: Boolean) {
//        disabledSendAfterDate.postValue(check)
//    }
//
//    fun onNameType(name: String) {
//        nameField.value = name
//    }
//
//
//    fun onDescriptionType(name: String) {
//        descriptionField.value = name
//    }
//
//    fun onCommentsEnableCheck(check: Boolean) {
//        commentsEnabled.postValue(check)
//    }
//
//    private fun onSaveClick() {
//        if (uiValidator.runValidates() && uiEditor.hasBeenChanged()) {
//            viewModelScope.launch {
//                val result = if (uiEditor.isNew) {
//                    addCourseWorkUseCase(
//                        courseId,
//                        CreateCourseWorkRequest(
//                            nameField.value!!,
//                            descriptionField.value,
//                            selectedTopic.value?.id,
//                            dueDate,
//                            dueTime,
//                            CourseWorkType.ASSIGNMENT,
//                            5
//                        )
//                    ).onSuccess { courseWork ->
//                        _addedAttachmentsRequests.map {
//                            it.map { attachmentRequest ->
//                                uploadAttachmentToCourseWorkUseCase(
//                                    courseId = courseId,
//                                    workId = courseWork.id,
//                                    attachmentRequest = attachmentRequest
//                                )
//                            }
//                        }
//                    }
//                } else {
//                    updateCourseWorkUseCase(
//                        courseId, workId!!, UpdateCourseWorkRequest(
//                            optPropertyOf(nameField.value!!),
//                            optPropertyOf(descriptionField.value),
//                            optPropertyOf(selectedTopic.value?.id),
//                            optPropertyOf(dueDate),
//                            optPropertyOf(dueTime),
//                            optPropertyOf(5)
//                        )
//                    ).onSuccess { courseWork ->
//                        removedAttachments.map {
//                            removeAttachmentFromCourseWorkUseCase(courseId, courseWork.id, it)
//                        }
//                        _addedAttachmentsRequests.map {
//                            it.map { attachmentRequest ->
//                                uploadAttachmentToCourseWorkUseCase(
//                                    courseId = courseId,
//                                    workId = courseWork.id,
//                                    attachmentRequest = attachmentRequest
//                                )
//                            }
//                        }
//                    }
//                }
//
//                result.onSuccess {
//                    finish()
//                }
//            }
//        }
//    }
//
//    fun onTopicClick() {
//        openCourseTopics.value = _courseId
//    }
//
//    fun onTopicSelected(topic: TopicResponse?) {
//        selectedTopic.value = topic
//    }
//
//    data class SubmissionSettingsState(
//        val textAvailable: Boolean,
//        val charsLimit: String,
//        val attachmentsAvailable: Boolean,
//        val attachmentsLimit: String,
//        val attachmentsSizeLimit: String,
//    )
//}