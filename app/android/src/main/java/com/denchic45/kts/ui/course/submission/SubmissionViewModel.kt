package com.denchic45.kts.ui.course.submission

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.domain.model.Task
import com.denchic45.kts.domain.usecase.FindSubmissionUseCase
import com.denchic45.kts.domain.usecase.GradeSubmissionUseCase
import com.denchic45.kts.domain.usecase.ReturnSubmissionUseCase
import com.denchic45.kts.ui.base.BaseViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import kotlin.properties.Delegates

class SubmissionViewModel @Inject constructor(
    @Named(SubmissionDialog.TASK_ID) private val taskId: String,
    @Named(SubmissionDialog.STUDENT_ID) private val studentId: String,
    findSubmissionUseCase: FindSubmissionUseCase,
    private val gradeSubmissionUseCase: GradeSubmissionUseCase,
    private val returnSubmissionUseCase: ReturnSubmissionUseCase
) : BaseViewModel() {

    val showSubmission: SharedFlow<Task.Submission> = findSubmissionUseCase(taskId, studentId)
        .shareIn(viewModelScope, SharingStarted.Lazily, 1)

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
                gradeSubmissionUseCase(taskId, studentId, grade)
            }
        }
    }

    fun onRejectClick() {
        openRejectConfirmation.call()
    }

    fun onRejectConfirmClick() {
        viewModelScope.launch {
            if (cause.isNotEmpty())
                returnSubmissionUseCase(taskId, studentId, cause)
            closeRejectConfirmation.call()
        }
    }

    fun onRejectCancelClick() {
        closeRejectConfirmation.call()
    }
}