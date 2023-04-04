package com.denchic45.kts.ui.adminPanel.timetableEditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.app
import com.denchic45.kts.databinding.FragmentTimetableEditorBinding
import com.denchic45.kts.ui.adminPanel.timetableEditor.finder.TimetableFinderFragment
import com.denchic45.kts.ui.adminPanel.timetableEditor.loader.TimetableLoaderFragment
import com.denchic45.kts.ui.timetableLoader.TimetableLoaderScreen

class TimetableEditorFragment : Fragment(R.layout.fragment_timetable_editor) {

    val binding: FragmentTimetableEditorBinding by viewBinding(
        FragmentTimetableEditorBinding::bind
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                TimetableLoaderScreen(app.appComponent.timetableLoaderComponent)
            }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        val adapter = TimetableEditorPageAdapter(childFragmentManager)
//        with(binding) {
//            vp.adapter = adapter
//            tl.setupWithViewPager(vp)
//            tl.getTabAt(0)!!.setText("Добавить").setIcon(R.drawable.ic_add)
//            tl.getTabAt(1)!!.setText("Посмотреть").setIcon(R.drawable.ic_timetable)
//        }
//    }

    private class TimetableEditorPageAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> TimetableLoaderFragment()
                1 -> TimetableFinderFragment()
                else -> throw RuntimeException("position not is: $position")
            }

        }

        override fun getCount(): Int = 2
    }
}