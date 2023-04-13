package com.denchic45.kts.ui.studygroup

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentGroupBinding
import com.denchic45.kts.ui.BaseFragment2
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.studygroup.courses.GroupCoursesFragment
import com.denchic45.kts.ui.studygroup.users.GroupMembersFragment
import com.google.android.material.tabs.TabLayoutMediator
import me.tatarka.inject.annotations.Inject

@Inject
class StudyGroupFragment(component: (String, ComponentContext) -> StudyGroupViewModel) :
    BaseFragment2<StudyGroupViewModel, FragmentGroupBinding>(
        R.layout.fragment_group,
    ), HasNavArgs<StudyGroupFragmentArgs> {

    override val navArgs: StudyGroupFragmentArgs by navArgs()
    override val component: StudyGroupViewModel by lazy {
        component(
            requireArguments().getString("groupId")!!,
            defaultComponentContext(requireActivity().onBackPressedDispatcher)
        )
    }
    override val binding: FragmentGroupBinding by viewBinding(FragmentGroupBinding::bind)

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        component.onPrepareOptions(binding.vp.currentItem)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        component.menuItemVisibility.observe(viewLifecycleOwner) { (id, visible) ->
//            val menuItem = menu.findItem(id)
//            if (menuItem != null) menuItem.isVisible = visible
//        }
//
//        component.openGroupEditor.observe(viewLifecycleOwner) { groupId ->
//            navController.navigate(
//                R.id.action_global_groupEditorFragment,
//                bundleOf("studyGroupId" to groupId)
//            )
//        }
//        with(binding) {
//            component.tabs.observe(viewLifecycleOwner) { size: Int ->
//                val adapter = GroupFragmentAdapter(
//                    this@StudyGroupFragment,
//                    component._studyGroupId,
//                    size,
//                    requireContext()
//                )
//                vp.adapter = adapter
//                vp.offscreenPageLimit = 3
//                vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
//                    override fun onPageSelected(position: Int) {
//                        super.onPageSelected(position)
//                        component.onPageSelect(position)
//                    }
//                })
////                tl.setupWithViewPager(vp)
//                TabLayoutMediator(tl, vp) { tab, position ->
//                    tab.text = "OBJECT ${(position + 1)}"
//                }.attach()
//            }
//        }
    }

    class GroupFragmentAdapter(
        fm: Fragment,
        private val groupId: String,
        private val size: Int,
        private val context: Context,
    ) : FragmentStateAdapter(fm) {
//        override fun getPageTitle(position: Int): CharSequence {
//            return when (position) {
//                0 -> context.getString(R.string.group_users)
//                1 -> context.getString(R.string.group_courses)
//                2 -> context.getString(R.string.timetable)
//                else -> throw IllegalStateException()
//            }
//        }

        override fun getItemCount(): Int {
            return size
        }

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> GroupMembersFragment.newInstance(groupId)
                1 -> GroupCoursesFragment.newInstance(groupId)
//                2 -> TimetableFragment.newInstance(groupId)
                else -> throw IllegalStateException()
            }
        }
    }

    companion object {
        const val GROUP_ID = "Group GROUP_ID"

//        fun newInstance(groupId: String): StudyGroupFragment {
//            return StudyGroupFragment().apply {
//                arguments = bundleOf("groupId" to groupId)
//            }
//        }
    }
}