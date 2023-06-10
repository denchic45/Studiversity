package com.denchic45.studiversity.ui.studygroup.users

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.studiversity.R
import com.denchic45.studiversity.customPopup.OptionsPopupAdapter
import com.denchic45.studiversity.databinding.FragmentGroupMembersBinding
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.ui.adapter.HeaderAdapterDelegate
import com.denchic45.studiversity.ui.adapter.UserAdapterDelegate
import com.denchic45.studiversity.ui.base.BaseFragment
import com.denchic45.studiversity.ui.model.UserItem
import com.denchic45.studiversity.util.Dimensions
import com.denchic45.studiversity.util.ViewUtils
import com.denchic45.studiversity.util.collectWhenStarted
import com.denchic45.studiversity.widget.extendedAdapter.adapter
import com.denchic45.studiversity.widget.extendedAdapter.extension.click

class GroupMembersFragment :
    BaseFragment<GroupMembersViewModel, FragmentGroupMembersBinding>(R.layout.fragment_group_members) {

    override val binding by viewBinding(FragmentGroupMembersBinding::bind)
    override val viewModel: GroupMembersViewModel by viewModels { viewModelFactory }

    override fun onResume() {
        super.onResume()
        // TODO: реилизовать скролл с TopAppBar
//        view?.let {
//            AppBarController.findController(requireActivity())
//                .setExpandableIfViewCanScroll(binding.rvUsers, viewLifecycleOwner)
//        }
    }

    override val navController: NavController by lazy {
        Navigation.findNavController(
            requireActivity().supportFragmentManager.primaryNavigationFragment!!.requireView()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val userAdapter = adapter {
            delegates(HeaderAdapterDelegate(), UserAdapterDelegate())
            extensions {
                click<UserAdapterDelegate.UserHolder>(
                    onClick = { position -> viewModel.onUserItemClick(position) },
                    onLongClick = { position ->
                        viewModel.onMemberClick(position)
                        true
                    }
                )
            }
        }

        with(binding) {
            rvUsers.adapter = userAdapter
            viewModel.showUserOptions.observe(
                viewLifecycleOwner
            ) { (memberPosition, options) ->
                val popupWindow = ListPopupWindow(requireActivity())
                popupWindow.anchorView = rvUsers.layoutManager!!.findViewByPosition(memberPosition)
                val adapter = OptionsPopupAdapter(requireContext(), options)
                popupWindow.setAdapter(adapter)
                popupWindow.width = ViewUtils.measureAdapter(adapter, requireContext())
                popupWindow.horizontalOffset = Dimensions.dpToPx(12, requireActivity())
                popupWindow.setOnItemClickListener { _, _, position, _ ->
                    popupWindow.dismiss()
                    viewModel.onOptionUserClick(
                        optionId = options[position].id,
                        memberId = (userAdapter.listItems[memberPosition] as UserItem).id
                    )
                }
                popupWindow.show()
            }
        }

        viewModel.members.collectWhenStarted(viewLifecycleOwner) { it.onSuccess(userAdapter::submit) }
    }


    companion object {
        const val GROUP_ID = "GroupUsers GROUP_ID"
        fun newInstance(groupId: String): GroupMembersFragment {
            return GroupMembersFragment().apply {
                arguments = bundleOf("groupId" to groupId)
            }
        }
    }
}