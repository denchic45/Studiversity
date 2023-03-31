package com.denchic45.kts.ui.studygroup

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentGroupBinding
import com.denchic45.kts.ui.base.BaseFragment
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.studygroup.courses.GroupCoursesFragment
import com.denchic45.kts.ui.studygroup.editor.StudyGroupEditorFragment
import com.denchic45.kts.ui.studygroup.users.GroupMembersFragment
import com.denchic45.kts.ui.timetable.TimetableFragment
import com.denchic45.kts.util.collectWhenResumed
import com.example.appbarcontroller.appbarcontroller.AppBarController

class StudyGroupFragment : BaseFragment<StudyGroupViewModel, FragmentGroupBinding>(
    R.layout.fragment_group,
    R.menu.options_group
), HasNavArgs<StudyGroupFragmentArgs> {

    override val navArgs: StudyGroupFragmentArgs by navArgs()
    override val binding: FragmentGroupBinding by viewBinding(FragmentGroupBinding::bind)
    override val viewModel: StudyGroupViewModel by viewModels { viewModelFactory }
    private lateinit var appBarController: AppBarController

    @Deprecated("Deprecated in Java")
    override fun onPrepareOptionsMenu(menu: Menu) {
        super.onPrepareOptionsMenu(menu)
        viewModel.onPrepareOptions(binding.vp.currentItem)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.isExist.collectWhenResumed(viewLifecycleOwner)

        viewModel.menuItemVisibility.observe(viewLifecycleOwner) { (id, visible) ->
            val menuItem = menu.findItem(id)
            if (menuItem != null) menuItem.isVisible = visible
        }

        viewModel.openGroupEditor.observe(viewLifecycleOwner) { groupId ->
            navController.navigate(
                R.id.action_global_groupEditorFragment,
                bundleOf(StudyGroupEditorFragment.GROUP_ID to groupId)
            )
        }
        with(binding) {
            viewModel.initTabs.observe(viewLifecycleOwner) { size: Int ->
                val adapter = GroupFragmentAdapter(
                    childFragmentManager,
                    viewModel.groupId,
                    size,
                    requireContext()
                )
                vp.adapter = adapter
                vp.offscreenPageLimit = 3
                vp.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
                    override fun onPageSelected(position: Int) {
                        viewModel.onPageSelect(position)
                    }
                })
                tl.setupWithViewPager(vp)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appBarController = AppBarController.findController(requireActivity())
        appBarController.setLiftOnScroll(false)
    }

    class GroupFragmentAdapter(
        fm: FragmentManager,
        private val groupId: String,
        private val size: Int,
        private val context: Context
    ) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> context.getString(R.string.group_users)
                1 -> context.getString(R.string.group_courses)
                2 -> context.getString(R.string.timetable)
                else -> throw IllegalStateException()
            }
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> GroupMembersFragment.newInstance(groupId)
                1 -> GroupCoursesFragment.newInstance(groupId)
                2 -> TimetableFragment.newInstance(groupId)
                else -> throw IllegalStateException()
            }
        }

        override fun getCount(): Int {
            return size
        }
    }

    companion object {
        const val GROUP_ID = "Group GROUP_ID"
    }
}