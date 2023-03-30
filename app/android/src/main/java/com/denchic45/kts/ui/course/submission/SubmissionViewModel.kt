package com.denchic45.kts.ui.course.submission

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.model.Task
import com.denchic45.kts.domain.stateInResource
import com.denchic45.kts.domain.usecase.FindSubmissionUseCase
import com.denchic45.kts.domain.usecase.GradeSubmissionUseCase
import com.denchic45.kts.ui.base.BaseViewModel
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionResponse
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates

class SubmissionViewModel @Inject constructor(
    @Named(SubmissionDialog.COURSE_ID)
    private val _courseId: String,
    @Named(SubmissionDialog.TASK_ID)
    private val _taskId: String,
    @Named(SubmissionDialog.STUDENT_ID)
    private val _studentId: String,
    findSubmissionUseCase: FindSubmissionUseCase,
    private val gradeSubmissionUseCase: GradeSubmissionUseCase
) : BaseViewModel() {

    private val courseId = _courseId.toUUID()
    private val taskId = _taskId.toUUID()
    private val studentId = _studentId.toUUID()

    val showSubmission: StateFlow<Resource<SubmissionResponse>> = flow {
        emit(findSubmissionUseCase(courseId,taskId, studentId))}
        .stateInResource(viewModelScope)

    private var grade by Delegates.notNull<Int>()

    private var cause = ""

    val gradeButtonVisibility = MutableSharedFlow<Boolean>()

    val openRejectConfirmation = SingleLiveData<Unit>()

    val closeRejectConfirmation = SingleLiveData<Unit>()

    private suspend fun submission() = showSubmission.first()

    fun onGradeType(typedGrade: String) {
        grade = if (typedGrade.isNotEmpty()) typedGrade.toInt() else 0
        viewModelScope.launch {
            val submission = submission()
            val oldGrade = (submission.status as? Task.SubmissionStatus.Graded)?.grade ?: 0
            gradeButtonVisibility.emit(oldGrade != grade && grade != 0)
        }
    }

    fun onCauseType(cause: String) {
        this.cause = cause
    }

    fun onSendGradeClick() {
        viewModelScope.launch {
            val submission = submission()
            if (submission.status !is Task.SubmissionStatus.Graded ||
                (submission.status as Task.SubmissionStatus.Graded).grade != grade
            ) {
                gradeSubmissionUseCase(_taskId, _studentId, grade)
            }
        }
    }

    fun onRejectClick() {
        openRejectConfirmation.call()
    }

    fun onRejectConfirmClick() {
        viewModelScope.launch {
            if (cause.isNotEmpty())
                returnSubmissionUseCase(_taskId, _studentId, cause)
            closeRejectConfirmation.call()
        }
    }

    fun onRejectCancelClick() {
        closeRejectConfirmation.call()
    }
}