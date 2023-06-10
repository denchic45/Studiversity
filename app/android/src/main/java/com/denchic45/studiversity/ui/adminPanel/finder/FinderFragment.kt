package com.denchic45.studiversity.ui.adminPanel.finder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.studiversity.ui.finder.FinderComponent
import com.denchic45.studiversity.ui.finder.FinderScreen
import com.denchic45.studiversity.ui.theme.AppTheme
import me.tatarka.inject.annotations.Inject

@Inject
class FinderFragment(
    private val component: (ComponentContext) -> FinderComponent
) : Fragment() {

//    private fun DelegationAdapterDsl.DelegationAdapterBuilder.adapterExtensions() {
//        extensions {
//            click<GroupHolder>(
//                onClick = viewModel::onFinderItemClick,
//                onLongClick = {
//                    viewModel.onFinderItemLongClick(it)
//                    true
//                }
//            )
//        }
//    }
//
//    private val listAdapters = listOf(
//        adapter {
//            delegates(UserAdapterDelegate())
//            adapterExtensions()
//        },
//        adapter {
//            delegates(StudyGroupAdapterDelegate())
//            adapterExtensions()
//        },
//        adapter {
//            delegates(SubjectAdapterDelegate())
//            adapterExtensions()
//        },
//        adapter {
//            delegates(SpecialtyAdapterDelegate())
//            adapterExtensions()
//        },
//        adapter {
//            delegates(CourseAdapterDelegate())
//            adapterExtensions()
//        }
//    )

//    override val viewModel: FinderViewModel by viewModels { viewModelFactory }

//    private lateinit var currentAdapter: DelegationAdapterExtended

//    private lateinit var appBarController: AppBarController
//    private lateinit var rvFinderEntities: RecyclerView
//    private lateinit var finderEntityAdapter: FinderEntityAdapter
//    private lateinit var searchBar: SearchBar
//    override val binding by viewBinding({ FragmentFinderBinding.bind(it) })

//    private fun hideMainToolbar() {
//        appBarController.toolbar.visibility = View.GONE
//        appBarController.addView(R.layout.searchbar_appbar)
//        appBarController.addView(
//            R.layout.rv_finder_entity,
//            AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
//        )
//    }

//    private fun showToolbar() {
//        appBarController.removeView(rvFinderEntities)
//        appBarController.removeView(searchBar)
//        searchBar.detachNavigationDrawer()
//        appBarController.toolbar.visibility = View.VISIBLE
//    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                FinderScreen(component(defaultComponentContext(requireActivity().onBackPressedDispatcher)))
            }
        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        appBarController = AppBarController.findController(requireActivity())
//        hideMainToolbar()
//        with(binding) {
//            searchBar = requireActivity().findViewById(R.id.search_bar_finder)
//            finderEntityAdapter = FinderEntityAdapter()
//            rvFinderEntities = appBarController.getView(R.id.rv_finder_entity)!!
//            rvFinderEntities.adapter = finderEntityAdapter
//            rvFinderEntities.layoutManager =
//                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
//            finderEntityAdapter.setItemClickListener { position: Int ->
//                viewModel.onCurrentSearchSelect(position)
//            }
//
//            viewModel.currentSearch.collectWhenStarted(viewLifecycleOwner) { position: Int ->
//                currentAdapter = listAdapters[position]
//                rvFoundItems.adapter = currentAdapter
//                AppBarController.findController(requireActivity())
//                    .setExpandableIfViewCanScroll(rvFoundItems, viewLifecycleOwner)
//                finderEntityAdapter.selectItem(position)
//            }
//        }
//        searchBar.setOnQueryTextListener(object : SearchBar.OnQueryTextListener() {
//            override fun onQueryTextSubmit(query: String) {
//                viewModel.onQueryTextSubmit(query)
//            }
//        })
//        searchBar.attachNavigationDrawer(requireActivity().findViewById(R.id.drawer_layout))
//        val listStateLayout = view as ListStateLayout
//        viewModel.finderEntities.observe(
//            viewLifecycleOwner
//        ) { list: List<ListItem> -> finderEntityAdapter.submitList(list) }
//
//        viewModel.foundItems
//            .drop(1)
//            .collectWhenStarted(viewLifecycleOwner) { resource ->
//                when (resource) {
//                    is Resource.Success -> {
//                        listStateLayout.showList()
//                        currentAdapter.submit(resource.value)
//                    }
//                    Resource.Loading -> {}
//                    is Resource.Error -> {
//                        when (resource.failure) {
//                            is NoConnection -> {
//                                listStateLayout.showView(ListStateLayout.NETWORK_VIEW)
//                            }
//                            else -> toast("Ошибка: ${resource.failure}")
//                        }
//                    }
//                }
//            }
//
//        viewModel.openSubjectEditor.observe(viewLifecycleOwner) { subjectId: String ->
//            navController.navigate(
//                R.id.action_global_subjectEditorDialog,
//                bundleOf(SubjectEditorDialog.SUBJECT_ID to subjectId)
//            )
//        }
//        viewModel.openGroupEditor.observe(viewLifecycleOwner) { groupId ->
//            navController.navigate(
//                R.id.action_global_groupEditorFragment,
//                bundleOf(StudyGroupEditorFragment.GROUP_ID to groupId)
//            )
//        }
//        viewModel.openSpecialtyEditor.observe(viewLifecycleOwner) { id ->
//            ComposeView(requireContext()).apply {
//                setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
//                setContent {
//                    AppTheme {
//                        SpecialtyEditorDialog()
//                    }
//                }
//            }
////            SpecialtyEditorDialog.newInstance(id).show(
////                childFragmentManager, null
////            )
//        }
//
//        viewModel.openCourse.observe(viewLifecycleOwner) {
//            navController.navigate(
//                R.id.action_global_courseFragment,
//                bundleOf(CourseFragment.COURSE_ID to it)
//            )
//        }
//        viewModel.showOptions.observe(viewLifecycleOwner) { (first, second) ->
//            val popupWindow = ListPopupWindow(
//                requireActivity()
//            )
//            val popupAdapter = ListPopupWindowAdapter(requireContext(), second)
//            popupWindow.anchorView = binding.rvFoundItems.layoutManager!!.findViewByPosition(first)
//            popupWindow.setOnItemClickListener { _, _, position, _ ->
//                popupWindow.dismiss()
//                viewModel.onOptionClick(second[position].id.toString())
//            }
//            popupWindow.width = ViewUtils.measureAdapter(popupAdapter, requireContext())
//            popupWindow.horizontalOffset = Dimensions.dpToPx(12, requireActivity())
//            popupWindow.setAdapter(popupAdapter)
//            popupWindow.show()
//        }
//    }

//    override fun onItemClick(position: Int) {
//        viewModel.onFinderItemClick(position)
//    }

//    override fun onLongItemClick(position: Int) {
//        viewModel.onFinderItemLongClick(position)
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        showToolbar()
//    }
}