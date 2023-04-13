package com.denchic45.kts.ui.yourStudyGroups

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.childContext
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.android.DefaultViewContext
import com.arkivanov.decompose.extensions.android.ViewContext
import com.arkivanov.decompose.extensions.android.child
import com.arkivanov.decompose.extensions.android.layoutInflater
import com.arkivanov.essenty.lifecycle.essentyLifecycle
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentGroupBinding
import com.denchic45.kts.databinding.FragmentYourStudyGroupsBinding
import com.denchic45.kts.ui.studygroup.StudyGroupFragment
import com.denchic45.kts.ui.studygroup.StudyGroupViewModel
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorFragmentDirections
import com.denchic45.kts.util.collectWhenStarted
import com.google.android.material.tabs.TabLayoutMediator
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class YourStudyGroupsFragment(
    yourStudyGroupsComponent: ((UUID) -> Unit, ComponentContext) -> YourStudyGroupsComponent,
//    private val studyGroupFragment: () -> StudyGroupFragment,
    private val studyGroupViewModel: (String, ComponentContext) -> StudyGroupViewModel,
) : Fragment(R.layout.fragment_your_study_groups) {
    val navController: NavController by lazy { findNavController() }
    val binding: FragmentYourStudyGroupsBinding by viewBinding(FragmentYourStudyGroupsBinding::bind)
    val componentContext by lazy {
        defaultComponentContext(requireActivity().onBackPressedDispatcher)
    }
    val component by lazy {
        yourStudyGroupsComponent({
            navController.navigate(StudyGroupEditorFragmentDirections.actionGlobalGroupEditorFragment(it.toString()))
        }, componentContext)
    }

    @OptIn(ExperimentalDecomposeApi::class)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        println("FRAGMENT $this")
        binding.composeView.apply {
            // Dispose the Composition when viewLifecycleOwner is destroyed
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
            )
            setContent {
                YourStudyGroupsScreen(component)
            }
        }

        val viewContext = DefaultViewContext(binding.studyGroup, essentyLifecycle())

        component.selectedStudyGroup.collectWhenStarted(viewLifecycleOwner) {
            it?.let {
                viewContext.apply {
                    child(parent) {
                        StudyGroupView(
                            component = studyGroupViewModel(
                                it.id.toString(),
                                componentContext
                            )
                        )
                    }
                }
//                childFragmentManager.commit {
//                    replace(
//                        binding.fragmentContainerView.id,
//                        studyGroupFragment().apply {
//                            arguments = bundleOf("groupId" to it.id.toString())
//                        }
//                    )
//                }
            }
        }
    }

    @OptIn(ExperimentalDecomposeApi::class)
    private fun ViewContext.StudyGroupView(
        component: StudyGroupViewModel,
    ): View {
        // Inflate the layout without adding it to the parent
        val binding = FragmentGroupBinding.inflate(layoutInflater, parent, false)

        val menuProvider = object : MenuProvider {
            override fun onPrepareMenu(menu: Menu) {
                component.menuItemVisibility.observe(viewLifecycleOwner) { (id, visible) ->
                    val menuItem = menu.findItem(id)
                    if (menuItem != null) menuItem.isVisible = visible
                }
            }

            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.options_group, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                // Validate and handle the selected menu item
                return false
            }
        }

        requireActivity().addMenuProvider(menuProvider)

        component.openGroupEditor.observe(viewLifecycleOwner) { groupId ->
            findNavController().navigate(
                R.id.action_global_groupEditorFragment,
                bundleOf("studyGroupId" to groupId)
            )
        }
        with(binding) {
            component.tabs.collectWhenStarted(viewLifecycleOwner) { list ->
                val adapter = StudyGroupFragment.GroupFragmentAdapter(
                    this@YourStudyGroupsFragment,
                    component._studyGroupId,
                    list.size,
                    requireContext()
                )
                vp.adapter = adapter
                vp.offscreenPageLimit = 3
                vp.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        component.onPageSelect(position)
                    }
                })
//                tl.setupWithViewPager(vp)
                TabLayoutMediator(tl, vp) { tab, position ->
                    tab.text = list[position]
                }.attach()
            }
        }

        return binding.root // Return the root of the inflated sub-tree
    }
}