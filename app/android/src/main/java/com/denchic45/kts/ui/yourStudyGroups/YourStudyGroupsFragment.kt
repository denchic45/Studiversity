package com.denchic45.kts.ui.yourStudyGroups

import android.os.Bundle
import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.R
import com.denchic45.kts.app
import com.denchic45.kts.databinding.FragmentYourStudyGroupsBinding
import com.denchic45.kts.ui.studygroup.StudyGroupFragment
import com.denchic45.kts.util.collectWhenStarted
import me.tatarka.inject.annotations.Inject

@Inject
class YourStudyGroupsFragment(
    yourStudyGroupsComponent: (ComponentContext) -> YourStudyGroupsComponent,
    private val studyGroupFragment:()->StudyGroupFragment
) :
    Fragment(R.layout.fragment_your_study_groups) {

    val binding: FragmentYourStudyGroupsBinding by viewBinding(FragmentYourStudyGroupsBinding::bind)
    val component = yourStudyGroupsComponent(defaultComponentContext(requireActivity().onBackPressedDispatcher))

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
        component.selectedStudyGroup.collectWhenStarted(viewLifecycleOwner) {
            it?.let {
                childFragmentManager.commit {
                    replace(
                        binding.fragmentContainerView.id,
                        studyGroupFragment().apply {
                            arguments = bundleOf("groupId" to it.id.toString())
                        }
                    )
                }
            }
        }
    }
}