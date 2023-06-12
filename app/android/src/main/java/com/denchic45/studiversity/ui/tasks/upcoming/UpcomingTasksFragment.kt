package com.denchic45.studiversity.ui.tasks.upcoming

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.studiversity.R
import com.denchic45.studiversity.databinding.FragmentListBinding
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.adapter.TaskAdapterDelegate
import com.denchic45.studiversity.ui.base.BaseFragment
import com.denchic45.studiversity.util.collectWhenStarted
import com.denchic45.studiversity.widget.extendedAdapter.adapter

class UpcomingTasksFragment :
    BaseFragment<UpcomingTasksViewModel, FragmentListBinding>(
        R.layout.fragment_list,
        R.menu.options_course_content
    ) {
    override val viewModel: UpcomingTasksViewModel by viewModels { viewModelFactory }
    override val binding: FragmentListBinding by viewBinding(FragmentListBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val adapter = adapter {
                delegates(TaskAdapterDelegate())
            }
            rv.adapter = adapter

            viewModel.tasks.collectWhenStarted(viewLifecycleOwner) { it.onSuccess(adapter::submit) }

        }
    }
}