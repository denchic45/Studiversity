package com.denchic45.kts.ui.yourstudygroups

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.ExperimentalDecomposeApi
import com.arkivanov.decompose.defaultComponentContext
import com.arkivanov.decompose.extensions.android.ViewContext
import com.arkivanov.decompose.extensions.android.layoutInflater
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentGroupBinding
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.root.YourStudyGroupsRootStackChildrenContainer
import com.denchic45.kts.ui.root.YourStudyGroupsRootScreen
import com.denchic45.kts.ui.studygroup.StudyGroupFragment
import com.denchic45.kts.ui.studygroup.StudyGroupViewModel
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.util.collectWhenStarted
import com.google.android.material.tabs.TabLayoutMediator
import me.tatarka.inject.annotations.Inject

@Inject
class YourStudyGroupsFragment(
    private val appBarInteractor: AppBarInteractor,
    yourStudyGroupsRootComponent: (
        ComponentContext,
    ) -> YourStudyGroupsRootStackChildrenContainer,
//    private val studyGroupFragment: () -> StudyGroupFragment,
//    private val studyGroupViewModel: (String, ComponentContext) -> StudyGroupViewModel,
) : Fragment(R.layout.fragment_your_study_groups) {
    val navController: NavController by lazy(::findNavController)

    val component by lazy {
        yourStudyGroupsRootComponent(
            defaultComponentContext(requireActivity().onBackPressedDispatcher)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
        )
        setContent {
            AppTheme {
                YourStudyGroupsRootScreen(component)
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