package com.denchic45.kts.ui.adminPanel.timetableEditor.courseChooser

import androidx.lifecycle.viewModelScope
import com.denchic45.kts.R
import com.denchic45.kts.SingleLiveData
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.domain.usecase.FindCourseByContainsNameUseCase
import com.denchic45.kts.ui.base.chooser.ChooserViewModel
import com.denchic45.stuiversity.api.course.model.CourseResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

class CourseChooserViewModel @Inject constructor(
    private val chooserInteractor: CourseChooserInteractor,
    private val findCourseByContainsNameUseCase: FindCourseByContainsNameUseCase
) : ChooserViewModel<CourseResponse>() {

    val openIconPicker = SingleLiveData<Unit>()

    val openSubjectChooser = SingleLiveData<Unit>()

    override fun onItemClick(position: Int) {
        items.value.onSuccess {
            viewModelScope.launch {
                chooserInteractor.emit(it[position])
                finish()
            }
        }
    }

    override val sourceFlow: (String) -> Flow<Resource<List<CourseResponse>>> = {
       findCourseByContainsNameUseCase(it)
    }


    override fun onOptionClick(itemId: Int) {
        when (itemId) {
            R.id.option_search_subject -> openSubjectChooser.call()
        }
    }

    init {
        toolbarTitle = "Курсы"
    }
}