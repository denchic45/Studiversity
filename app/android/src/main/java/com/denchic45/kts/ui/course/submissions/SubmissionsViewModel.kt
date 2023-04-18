package com.denchic45.kts.ui.course.submissions

//import androidx.lifecycle.viewModelScope
//import com.denchic45.kts.SingleLiveData
//import com.denchic45.kts.domain.onSuccess
//import com.denchic45.kts.domain.stateInResource
//import com.denchic45.kts.domain.usecase.FindCourseWorkSubmissionsUseCase
//import com.denchic45.kts.ui.base.BaseViewModel
//import com.denchic45.stuiversity.util.toUUID
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//import javax.inject.Named
//
//class SubmissionsViewModel @Inject constructor(
//    @Named(SubmissionsFragment.COURSE_ID)
//    private val _courseId: String,
//    @Named(SubmissionsFragment.TASK_ID)
//    private val _taskId: String,
//    findCourseWorkSubmissionsUseCase: FindCourseWorkSubmissionsUseCase
//) : BaseViewModel() {
//
//    private val courseId = _courseId.toUUID()
//    private val taskId = _taskId.toUUID()
//
//    val submissions = flow {
//        emit(findCourseWorkSubmissionsUseCase(courseId, taskId))
//    }.stateInResource(viewModelScope)
//
//    val openSubmission = SingleLiveData<Triple<String, String, String>>()
//
//    fun onSubmissionClick(position: Int) {
//        viewModelScope.launch {
//            submissions.value.onSuccess {
//                val submission = it[position]
//                openSubmission.value = Triple(_courseId, _taskId, submission.author.id.toString())
//            }
//        }
//    }
//}