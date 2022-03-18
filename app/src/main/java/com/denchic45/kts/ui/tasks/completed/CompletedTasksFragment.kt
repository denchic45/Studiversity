package com.denchic45.kts.ui.tasks.completed

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentListBinding
import com.denchic45.kts.ui.BaseFragment
import com.denchic45.kts.ui.adapter.TaskAdapterDelegate
import com.denchic45.widget.extendedAdapter.adapter
import kotlinx.coroutines.flow.collect

class CompletedTasksFragment :
    BaseFragment<CompletedTasksViewModel, FragmentListBinding>(R.layout.fragment_list) {
    override val viewModel: CompletedTasksViewModel by viewModels { viewModelFactory }
    override val binding: FragmentListBinding by viewBinding(FragmentListBinding::bind)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_course_content, menu)
        viewModel.onCreateOptions()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            val adapter = adapter {
                delegates(TaskAdapterDelegate())
            }
            rvList.adapter = adapter
            lifecycleScope.launchWhenStarted {
                viewModel.tasks.collect {
                    adapter.submit(it)
                }
            }
        }
    }
}