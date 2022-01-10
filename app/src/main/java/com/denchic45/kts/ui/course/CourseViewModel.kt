package com.denchic45.kts.ui.course

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.data.model.DomainModel
import com.denchic45.kts.data.model.domain.Task
import com.denchic45.kts.domain.usecase.FindSelfUserUseCase
import com.denchic45.kts.uipermissions.Permission
import com.denchic45.kts.uipermissions.UIPermissions
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class CourseViewModel @Inject constructor(
    @Named(CourseFragment.COURSE_UUID) private val courseId: String,
    findCourseUseCase: FindCourseUseCase,
    private val findCourseContentUseCase: FindCourseContentUseCase,
    findSelfUserUseCase: FindSelfUserUseCase
) : ViewModel() {

    val openTaskEditor: SingleLiveData<Triple<String?, String, String>> = SingleLiveData()
    val courseName: MutableLiveData<String> = MutableLiveData()
    val showContents: MutableLiveData<List<DomainModel>> = MutableLiveData()
    private val findCourseFlow = findCourseUseCase(courseId)

    init {
        viewModelScope.launch {
            findCourseFlow.collect { course ->
                courseName.value = course.info.name
                uiPermissions.addPermissions(Permission(
                    PERMISSION_EDIT,
                    { it == course.info.teacher },
                    { it.admin }
                )
                )

            }
        }
        viewModelScope.launch {
            findCourseContentUseCase.invoke(courseId).collect {
                showContents.value = it
            }
        }

    }

    fun onFabClick() {
        openTaskEditor.value = Triple(null, courseId, "")
    }

    fun onTaskItemClick(position: Int) {
        openTaskEditor.value = Triple(
            showContents.value!![position].uuid,
            courseId,
            (showContents.value!![position] as Task).sectionId
        )
    }

    private val uiPermissions: UIPermissions = UIPermissions(findSelfUserUseCase()).apply {

    }

    companion object {
        const val PERMISSION_EDIT = "PERMISSION_EDIT"
    }
}