package com.denchic45.kts.ui.course.taskInfo

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.domain.model.Attachment
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

class TaskInfoViewModel @Inject constructor(
    @Named(TaskInfoFragment.TASK_ID) val _taskId: String,
    @Named(TaskInfoFragment.COURSE_ID) val _courseId: String,
    findSelfUserUseCase: FindSelfUserUseCase,
    findCourseWorkUseCase: FindCourseWorkUseCase,
    findCourseWorkAttachmentsUseCase: FindCourseWorkAttachmentsUseCase,
    findOwnSubmissionUseCase: FindOwnSubmissionUseCase,
    private val submitSubmissionUseCase: SubmitSubmissionUseCase,
    private val cancelSubmissionUseCase: CancelSubmissionUseCase,
    private val uploadAttachmentToSubmissionUseCase: UploadAttachmentToSubmissionUseCase,
    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
) : BaseViewModel() {

    private val courseId = _courseId.toUUID()
    private val courseWorkId = _taskId.toUUID()

    private val courseWorkFlow = flow {
        emit(findCourseWorkUseCase(courseId, courseWorkId))
    }.shareIn(
        viewModelScope,
        replay = 1,
        started = SharingStarted.WhileSubscribed()
    )
    val attachments =
        flow { emit(findCourseWorkAttachmentsUseCase(courseId, courseWorkId)) }.stateIn(
            viewModelScope,
            SharingStarted.Lazily,
            Resource.Loading()
        )

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
    val focusOnTextField = SingleLiveData<Unit>()

    private var oldContent: SubmissionContent? = null
    private var contentUpdateDate: LocalDateTime? = null

    private val _submission = MutableSharedFlow<Resource<SubmissionResponse>>(replay = 1)

    val submissionUiState = _submission
        .map { it.map { response -> response.toSubmissionUiState(work()) } }
        .shareIn(viewModelScope, SharingStarted.Lazily)


    init {
        viewModelScope.launch {
            checkUserCapabilitiesInScopeUseCase(
                scopeId = courseId,
                capabilities = listOf(Capability.SubmitSubmission)
            ).onSuccess {
                _submission.emit(
                    findOwnSubmissionUseCase(courseId, courseWorkId)
                        .onSuccess { submissionResponse: SubmissionResponse ->
                            oldContent = submissionResponse.content
                            contentUpdateDate = submissionResponse.updatedAt
                        }
                )
            }
        }
    }

    val openFilePicker = SingleLiveData<Unit>()
    val openAttachment = SingleLiveData<File>()

    fun onBottomSheetStateChanged(newState: Int) {
        viewModelScope.launch {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                showSubmissionToolbar.value = false
                val submission = _submission.first()
//                if (expandBottomSheet.value == BottomSheetBehavior.STATE_EXPANDED
//                    && submission.status is Task.SubmissionStatus.NotSubmitted
//                    && oldContent != content
//                ) {
//                    updateSubmissionFromStudentUseCase(submission)
//                }
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
            attachments.value.onSuccess {
                when (val attachment = it[position]) {
                    is FileAttachmentHeader -> openAttachment.value = attachment.fileItem
                    is LinkAttachmentHeader -> TODO()
                }
            }
        }
    }

    fun onAddAttachmentClick() = openFilePicker.call()


    fun onSelectedFile(file: File) {
        viewModelScope.launch {
            content = content.copy(attachments = content.attachments + Attachment(file))
            _submission.emit(_submission.first().copy(content = content))
        }
    }

    fun onTaskFileClick(position: Int) {
        viewModelScope.launch {
            openAttachment.value = attachments.value[position].file
        }
    }

    fun onRemoveSubmissionFileClick(position: Int) {
        viewModelScope.launch {
            content =
                content.copy(attachments = content.attachments - content.attachments[position])
            _submission.emit(_submission.first().copy(content = content))
        }
    }

    fun onSubmissionTextType(text: String) {
        viewModelScope.launch {
            content = content.copy(text = text)
            _submission.emit(_submission.first().copy(content = content))
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
        if (content!!.isEmpty()) {
            expandBottomSheet.value = BottomSheetBehavior.STATE_EXPANDED
        } else {
            val contentUpdateDate =
                if (contentIsChangeAndNotEmpty()) LocalDateTime.now()
                else this.contentUpdateDate

            _submission.first().onSuccess { submissionResponse: SubmissionResponse ->
                _submission.emit(
                    Resource.Success(
                        when (submissionResponse) {
                            is WorkSubmissionResponse -> submissionResponse.copy(
                                state = SubmissionState.SUBMITTED,
                                updatedAt = contentUpdateDate
                            )
                        }
                    )
                )
                submitSubmissionUseCase(courseId, courseWorkId, submissionResponse.id)
            }
        }
    }

    private fun contentIsChangeAndNotEmpty() = oldContent != content && !content!!.isEmpty()

    private suspend fun onCancelSubmitClick() {
        _submission.first().onSuccess { submissionResponse: SubmissionResponse ->
            _submission.emit(
                Resource.Success(
                    when (submissionResponse) {
                        is WorkSubmissionResponse -> submissionResponse.copy(
                            state = SubmissionState.CANCELED_BY_AUTHOR,
                            updatedAt = contentUpdateDate
                        )
                    }
                )
            )
            cancelSubmissionUseCase(courseId, courseWorkId, submissionResponse.id)
        }
    }

    private suspend fun work() = courseWorkFlow
        .map { it as? Resource.Success }.filterNotNull().first().value

    data class WorkUiState(
        val name: String,
        val description: String?,
        val dueDateTime: String?,
    )

    sealed class SubmissionUiState {
        abstract val title: String
        abstract val subtitle: String?

        data class Work(
            override val title: String,
            override val subtitle: String?,
            val btnText: String?,
            val btnTextColor: Int = R.color.blue,
            val btnBackgroundColor: Int = R.color.alpha_blue_10,
            val gradedBy: UserResponse? = null,
            val attachments: List<AttachmentHeader>?,
            val allowEditContent: Boolean,
        ) : SubmissionUiState()
    }

    private fun SubmissionResponse.toSubmissionUiState(courseWorkResponse: CourseWorkResponse): SubmissionUiState {
        return when (this) {
            is WorkSubmissionResponse -> {
                val allowEditContent = state !in SubmissionState.notSubmitted()
                val attachments = content?.attachments

                grade?.let { grade ->
                    SubmissionUiState.Work(
                        title = "Оценено: ${grade}/5",
                        subtitle = null,
                        btnText = null,
                        attachments = attachments,
                        allowEditContent = allowEditContent,
                    )
                } ?: when (val status = state) {
                    SubmissionState.NEW,
                    SubmissionState.CREATED,
                    -> SubmissionUiState.Work(
                        title = "Не сдано",
                        subtitle = null,
                        btnText = if (content == null) "Добавить" else "Отправить",
                        attachments = attachments,
                        allowEditContent = allowEditContent,
                    )
                    SubmissionState.SUBMITTED -> SubmissionUiState.Work(
                        title = "Сдано",
                        subtitle = if (courseWorkResponse.late)
                            "с опозданием: "
                        else
                            "вовремя:" + doneAt!!.toString("dd MMM HH:mm"),
                        btnText = "Отменить",
                        btnTextColor = R.color.red,
                        btnBackgroundColor = R.color.alpha_red_10,
                        attachments = attachments,
                        allowEditContent = allowEditContent
                    )
                    SubmissionState.CANCELED_BY_AUTHOR -> SubmissionUiState.Work(
                        title = "Не сдано",
                        subtitle = "Было отменено вами",
                        btnText = if (content == null) "Добавить" else "Отправить",
                        attachments = attachments,
                        allowEditContent = allowEditContent,
                    )
                }
            }
        }
    }

}
