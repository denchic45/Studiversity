package com.denchic45.studiversity.ui.login.groupChooser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.studiversity.R
import com.denchic45.studiversity.databinding.FragmentGroupChooserBinding
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.adapter.TaskAdapterDelegate
import com.denchic45.studiversity.ui.base.BaseFragment
import com.denchic45.studiversity.util.collectWhenStarted
import com.denchic45.studiversity.widget.extendedAdapter.adapter

class GroupChooserFragment : BaseFragment<GroupChooserViewModel, FragmentGroupChooserBinding>(
    R.layout.fragment_group_chooser
) {

    override val viewModel: GroupChooserViewModel by viewModels { viewModelFactory }
    override val binding: FragmentGroupChooserBinding by viewBinding(
        FragmentGroupChooserBinding::bind
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = adapter {
            delegates(TaskAdapterDelegate())
            onClick { position ->
                viewModel.onItemClick(position)
            }
        }

        with(binding) {
            rvGroups.adapter = adapter
        }
        viewModel.items.collectWhenStarted(viewLifecycleOwner) {
            it.onSuccess {
                adapter.submit(it)
            }
        }
    }

}