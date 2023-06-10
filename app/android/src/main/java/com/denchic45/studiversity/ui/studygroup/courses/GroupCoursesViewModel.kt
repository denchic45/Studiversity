package com.denchic45.studiversity.ui.studygroup.courses

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.denchic45.studiversity.SingleLiveData
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.domain.stateInResource
import com.denchic45.studiversity.domain.usecase.FindCoursesByGroupUseCase
import com.denchic45.studiversity.ui.NavigationCommand
import com.denchic45.studiversity.ui.base.BaseViewModel
import com.denchic45.studiversity.ui.course.CourseFragmentDirections
import com.denchic45.stuiversity.util.toUUID
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class GroupCoursesViewModel @Inject constructor(
    findCoursesByGroupUseCase: FindCoursesByGroupUseCase,
    @Named(GroupCoursesFragment.GROUP_ID) _studyGroupId: String,
) : BaseViewModel() {
    val clearItemsSelection = SingleLiveData<Set<Int>>()
    val selectItem = MutableLiveData<Pair<Int, Boolean>>()

    private val studyGroupId = _studyGroupId.toUUID()

    var courses = flow {
        emit(findCoursesByGroupUseCase(studyGroupId))
    }.stateInResource(viewModelScope)

    fun onCourseItemClick(position: Int) {
        courses.value.onSuccess { courses ->
            viewModelScope.launch {
                navigate.emit(
                    NavigationCommand.To(
                        CourseFragmentDirections.actionGlobalCourseFragment(
                            courses[position].id.toString()
                        )
                    )
                )
            }
        }
    }

    fun onCourseLongItemClick(position: Int) {

    }
}