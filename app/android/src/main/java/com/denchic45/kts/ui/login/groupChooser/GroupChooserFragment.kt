package com.denchic45.kts.ui.login.groupChooser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentGroupChooserBinding
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.adapter.TaskAdapterDelegate
import com.denchic45.kts.ui.base.BaseFragment
import com.denchic45.kts.util.collectWhenStarted
import com.denchic45.widget.extendedAdapter.adapter

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