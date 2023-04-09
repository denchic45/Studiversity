package com.denchic45.kts.ui.yourStudyGroups

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.app
import com.denchic45.kts.databinding.FragmentYourStudyGroupsBinding
import com.denchic45.kts.ui.studygroup.StudyGroupFragment
import com.denchic45.kts.util.collectWhenStarted

class YourStudyGroupsFragment : Fragment(R.layout.fragment_your_study_groups) {

    val binding: FragmentYourStudyGroupsBinding by viewBinding(FragmentYourStudyGroupsBinding::bind)
    val component = app.appComponent.yourStudyGroupsComponent

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("FRAGMENT $this")
        binding.composeView.apply {
            // Dispose the Composition when viewLifecycleOwner is destroyed
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                YourTimetablesScreen(component)
            }
        }
        component.selectedStudyGroupId.collectWhenStarted(viewLifecycleOwner) {
            it?.let {
                childFragmentManager.commit {
                    replace(
                        binding.fragmentContainerView.id,
                        StudyGroupFragment.newInstance(it)
                    )
                }
            }
        }
    }
}