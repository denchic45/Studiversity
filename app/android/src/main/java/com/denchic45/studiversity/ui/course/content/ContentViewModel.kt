package com.denchic45.studiversity.ui.course.content

import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.common.R
import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.usecase.CheckUserCapabilitiesInScopeUseCase
import com.denchic45.studiversity.domain.usecase.FindSelfUserUseCase
import com.denchic45.studiversity.domain.usecase.RemoveCourseElementUseCase
import com.denchic45.studiversity.ui.base.BaseViewModel
import com.denchic45.studiversity.ui.confirm.ConfirmInteractor
import com.denchic45.stuiversity.api.role.model.Capability
import com.denchic45.stuiversity.api.role.model.CheckCapabilitiesResponse
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

//class ContentViewModel @Inject constructor(
//    findSelfUserUseCase: FindSelfUserUseCase,
//    @Named(ContentFragment.COURSE_ID)
//    private val courseId: String,
//    @Named(ContentFragment.TASK_ID)
//    private val taskId: String,
//    private val confirmInteractor: ConfirmInteractor,
//    private val checkUserCapabilitiesInScopeUseCase: CheckUserCapabilitiesInScopeUseCase,
//    private val removeCourseElementUseCase: RemoveCourseElementUseCase,
//) : BaseViewModel() {
//
//    val tabNames = listOf("Инфо", "Ответы")
//
//    val openTaskEditor = SingleLiveData<Pair<String, String>>()
//
//    val submissionVisibility = MutableSharedFlow<Boolean>(replay = 1)
//
//    private val selfUser = findSelfUserUseCase()
//
//    private val capabilities: SharedFlow<Resource<CheckCapabilitiesResponse>> = flow {
//        emit(
//            checkUserCapabilitiesInScopeUseCase(
//                selfUser.id,
//                courseId.toUUID(),
//                listOf(Capability.ReadSubmissions)
//            )
//        )
//    }.shareIn(viewModelScope, SharingStarted.Lazily, 1)
//
//    override fun onOptionClick(itemId: Int) {
//        when (itemId) {
//            R.id.option_task_edit -> openTaskEditor.value = taskId to courseId
//            R.id.options_task_delete -> {
//                openConfirmation(
//                    "Удалить задание" to
//                            "Вместе с заданием безвозратно будут удалены ответы, " +
//                            "а также их оценки"
//                )
//                viewModelScope.launch {
//                    if (confirmInteractor.receiveConfirm()) {
//                        removeCourseElementUseCase(courseId.toUUID(), taskId.toUUID())
//                        finish()
//                    }
//                }
//            }
//        }
//    }
//
//    override fun onCreateOptions() {
//        super.onCreateOptions()
//        viewModelScope.launch {
//            capabilities.first().onSuccess { response ->
//                submissionVisibility.emit(response.hasCapability(Capability.ReadSubmissions))
//                response.ifHasCapability(Capability.WriteCourseElements) {
//                    setMenuItemVisible(
//                        R.id.option_task_edit to true,
//                        R.id.options_task_delete to true
//                    )
//                }
//            }
//        }
//    }
//}