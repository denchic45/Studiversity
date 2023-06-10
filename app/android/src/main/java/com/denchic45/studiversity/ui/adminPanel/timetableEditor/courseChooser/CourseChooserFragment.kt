package com.denchic45.studiversity.ui.adminPanel.timetableEditor.courseChooser

import androidx.fragment.app.viewModels
import com.denchic45.studiversity.ui.adapter.CourseAdapterDelegate
import com.denchic45.studiversity.ui.base.chooser.ChooserFragment
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.studiversity.widget.extendedAdapter.AdapterDelegate

class CourseChooserFragment : ChooserFragment<CourseChooserViewModel, CourseResponse>() {
    override val viewModel: CourseChooserViewModel by viewModels { viewModelFactory }
    override fun adapterDelegates(): List<AdapterDelegate> = listOf(CourseAdapterDelegate())

}