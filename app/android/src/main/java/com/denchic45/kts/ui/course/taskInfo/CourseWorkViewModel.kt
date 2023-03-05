package com.denchic45.kts.ui.course.taskInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.domain.model.Attachment2
import com.denchic45.kts.data.domain.model.FileAttachment2
import com.denchic45.kts.data.domain.model.LinkAttachment2
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.map
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.*
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.stuiversity.api.course.element.model.*
import com.denchic45.stuiversity.api.course.work.model.CourseWorkResponse
import com.denchic45.stuiversity.api.course.work.submission.model.*
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.toString
import com.denchic45.stuiversity.util.toUUID
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File
import java.time.*
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Named

class CourseWorkViewModel @Inject constructor(
    @Named(CourseWorkFragment.TASK_ID) val _taskId: String,
    @Named(CourseWorkFragment.COURSE_ID) val _courseId: String,
    findCourseWorkUseCase: FindCourseWorkUseCase,
    findSubmissionAttachmentsUseCase: FindSubmissionAttachmentsUseCase,
    findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
    findOwnSubmissionUseCase: FindOwnSubmissionUseCase,
    private val submitSubmissionUseCase: SubmitSubmissionUseCase,
    private val cancelSubmissionUseCase: CancelSubmissionUseCase,
    private val uploadAttachmentToCourseWorkUseCase: UploadAttachmentToCourseWorkUseCase,
    private val removeAttachmentFromCourseWorkUseCase: RemoveAttachmentFromCourseWorkUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
) : BaseViewModel() {

    private val courseId = _courseId.toUUID()
    private val courseWorkId = _taskId.toUUID()

    private val capabilities = flow {
        emit(
            checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf(Capability.SubmitSubmission)
            )
        )
    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)

    private val courseWorkFlow = flow {
        emit(findCourseWorkUseCase(courseId, courseWorkId))
    }.shareIn(
        viewModelScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )


    private val _workAttachments = MutableStateFlow<Resource<List<Attachment2>>>(Resource.Loading)
    val workAttachments = _workAttachments.asStateFlow()

    private val _submissionAttachments =
        MutableStateFlow<Resource<List<Attachment2>>>(Resource.Loading)
    val submissionAttachments = _submissionAttachments.asStateFlow()

    val workUiState = courseWorkFlow.map { resource ->
        resource.map { element ->
            WorkUiState(
                name = element.name,
                description = element.description,
                dueDateTime = element.dueDate?.let { date ->
                    val pattern = DateTimeFormatter.ofPattern("dd MMM")
                    date.format(pattern)
                } + element.dueTime?.let { time ->
                    val pattern = DateTimeFormatter.ofPattern("HH:mm")
                    time.format(pattern)
                }
            )
        }
    }

    val showSubmissionToolbar = MutableLiveData<Boolean>()
    val expandBottomSheet = MutableLiveData(BottomSheetBehavior.STATE_COLLAPSED)

    private val _submission = MutableSharedFlow<Resource<WorkSubmissionResponse>>(replay = 1)

    val submissionUiState = _submission
        .map { it.map { response -> response.toSubmissionUiState(work()) } }
        .shareIn(viewModelScope, SharingStarted.Lazily)


    init {
        viewModelScope.launch {
            capabilities.collect { resource ->
                resource.onSuccess { response ->
                    response.onHasCapability(Capability.SubmitSubmission) {
                        _submission.emit(
                            findOwnSubmissionUseCase(courseId, courseWorkId)
                                .map { it as WorkSubmissionResponse }
                        )
                    }
                }
            }
        }

        viewModelScope.launch {
            findCourseWorkAttachmentsUseCase(courseId, courseWorkId).collect { resource ->
                _workAttachments.update { resource }
            }
        }

        viewModelScope.launch {
            _submission.first().onSuccess { workSubmissionResponse ->
                findSubmissionAttachmentsUseCase(
                    courseId,
                    courseWorkId,
                    workSubmissionResponse.id
                ).collect { resource ->
                    _submissionAttachments.update { resource }
                }
            }
        }
    }

    val openFilePicker = SingleLiveData<Unit>()
    val openAttachment = SingleLiveData<File>()

    fun onBottomSheetStateChanged(newState: Int) {
        viewModelScope.launch {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                showSubmissionToolbar.value = false
            } else if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                showSubmissionToolbar.value = true
            }
            when (newState) {
                BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_COLLAPSED -> {
                    expandBottomSheet.value = newState
                }
            }
        }
    }

    fun onSubmissionAttachmentClick(position: Int) {
        viewModelScope.launch {
            submissionAttachments.value.onSuccess {
                openAttachment(it[position])
            }
        }
    }

    private fun openAttachment(attachment: Attachment2) {
        when (attachment) {
            is FileAttachment2 -> openAttachment.value = attachment.path.toFile()
            is LinkAttachment2 -> TODO()
        }
    }

    fun onAddAttachmentClick() = openFilePicker.call()


    fun onSelectedFile(file: File) {
        viewModelScope.launch {
            when (uploadAttachmentToCourseWorkUseCase(
                courseId,
                courseWorkId,
                CreateFileRequest(file.name, file.readBytes())
            )) {
                is Resource.Error -> toast.emit("Произошла ошибка при загрузке...")
                Resource.Loading -> toast.emit("Загрузка файла...")
                is Resource.Success -> toast.emit("Произошла ошибка...")
            }
        }
    }

    fun onAttachmentClick(position: Int) {
        viewModelScope.launch {
            workAttachments.value.onSuccess { attachments ->
                openAttachment(attachments[position])
            }
        }
    }

    fun onRemoveSubmissionFileClick(position: Int) {
        viewModelScope.launch {
            workAttachments.value.onSuccess { attachments ->
                removeAttachmentFromCourseWorkUseCase(
                    courseId,
                    courseWorkId,
                    attachments[position].id
                )
            }
        }
    }

    fun onBackPress() {
        if (expandBottomSheet.value != BottomSheetBehavior.STATE_COLLAPSED) {
            showSubmissionToolbar.value = false
            expandBottomSheet.value = BottomSheetBehavior.STATE_COLLAPSED
        } else {
            viewModelScope.launch { finish() }
        }
    }

    fun onActionClick() {
        viewModelScope.launch {
            _submission.first().onSuccess {
                when (it.state) {
                    SubmissionState.NEW,
                    SubmissionState.CREATED,
                    SubmissionState.CANCELED_BY_AUTHOR,
                    -> onSubmitActionClick()
                    SubmissionState.SUBMITTED -> {
                        onCancelSubmitClick()
                    }
                }
            }
        }
    }

    private suspend fun onSubmitActionClick() {
        _submissionAttachments.value.onSuccess { attachments ->
            if (attachments.isEmpty()) {
                expandBottomSheet.value = BottomSheetBehavior.STATE_EXPANDED
            } else {
                _submission.first().onSuccess { submissionResponse: SubmissionResponse ->
                    _submission.emit(
                        submitSubmissionUseCase(courseId, courseWorkId, submissionResponse.id)
                            .map { it as WorkSubmissionResponse }
                    )
                }
            }
        }
    }

    private suspend fun onCancelSubmitClick() {
        _submission.first().onSuccess { submissionResponse ->
            _submission.emit(
                cancelSubmissionUseCase(courseId, courseWorkId, submissionResponse.id)
                    .map { it as WorkSubmissionResponse }
            )
        }
    }

    private suspend fun work() = courseWorkFlow
        .map { it as? Resource.Success }.filterNotNull().first().value

    data class WorkUiState(
        val name: String,
        val description: String?,
        val dueDateTime: String?,
    )


    data class WorkSubmissionUiState(
        val title: String,
        val subtitle: String?,
        val btnVisibility: Boolean,
        val btnText: String?,
        val btnTextColor: Int = R.color.blue,
        val btnBackgroundColor: Int = R.color.alpha_blue_10,
        val gradedBy: UserResponse? = null,
        val attachments: List<AttachmentHeader>?,
        val allowEditContent: Boolean,
    )


    private fun WorkSubmissionResponse.toSubmissionUiState(courseWorkResponse: CourseWorkResponse): WorkSubmissionUiState {
        val allowEditContent = state !in SubmissionState.notSubmitted()
        val attachments = content.attachments

        return grade?.let { grade ->
            WorkSubmissionUiState(
                title = "Оценено: ${grade}/5",
                subtitle = null,
                btnVisibility = false,
                btnText = null,
                attachments = attachments,
                allowEditContent = allowEditContent,
            )
        } ?: when (state) {
            SubmissionState.NEW,
            SubmissionState.CREATED,
            -> WorkSubmissionUiState(
                title = "Не сдано",
                subtitle = null,
                btnVisibility = true,
                btnText = if (content.isEmpty()) "Добавить" else "Отправить",
                attachments = attachments,
                allowEditContent = allowEditContent,
            )
            SubmissionState.SUBMITTED -> WorkSubmissionUiState(
                title = "Сдано",
                subtitle = if (courseWorkResponse.late)
                    "с опозданием: "
                else
                    "вовремя:" + doneAt!!.toString("dd MMM HH:mm"),
                btnVisibility = false,
                btnText = "Отменить",
                btnTextColor = R.color.red,
                btnBackgroundColor = R.color.alpha_red_10,
                attachments = attachments,
                allowEditContent = allowEditContent
            )
            SubmissionState.CANCELED_BY_AUTHOR -> WorkSubmissionUiState(
                title = "Не сдано",
                subtitle = "Было отменено вами",
                btnVisibility = true,
                btnText = if (content == null) "Добавить" else "Отправить",
                attachments = attachments,
                allowEditContent = allowEditContent,
            )
        }
    }
}
