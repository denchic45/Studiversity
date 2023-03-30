package com.denchic45.kts.ui.adminPanel.finder

import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.ListPopupWindow
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.customPopup.ListPopupWindowAdapter
import com.denchic45.kts.data.domain.NoConnection
import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.data.model.domain.ListItem
import com.denchic45.kts.databinding.FragmentFinderBinding
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.ui.adapter.*
import com.denchic45.kts.ui.base.BaseFragment
import com.denchic45.kts.ui.course.CourseFragment
import com.denchic45.kts.ui.specialtyEditor.SpecialtyEditorDialog
import com.denchic45.kts.ui.studygroup.editor.StudyGroupEditorFragment
import com.denchic45.kts.ui.subjectEditor.SubjectEditorDialog
import com.denchic45.kts.util.Dimensions
import com.denchic45.kts.util.ViewUtils
import com.denchic45.kts.util.collectWhenStarted
import com.denchic45.kts.util.toast
import com.denchic45.sample.SearchBar
import com.denchic45.widget.ListStateLayout
import com.denchic45.widget.extendedAdapter.DelegationAdapterDsl
import com.denchic45.widget.extendedAdapter.adapter
import com.denchic45.widget.extendedAdapter.extension.click
import com.example.appbarcontroller.appbarcontroller.AppBarController
import com.google.android.material.appbar.AppBarLayout
import kotlinx.coroutines.flow.drop

class FinderFragment :
    BaseFragment<FinderViewModel, FragmentFinderBinding>(R.layout.fragment_finder),
    OnItemClickListener,
    OnItemLongClickListener {

    private fun DelegationAdapterDsl.DelegationAdapterBuilder.adapterExtensions() {
        extensions {
            click<GroupHolder>(
                onClick = viewModel::onFinderItemClick,
                onLongClick = {
                    viewModel.onFinderItemLongClick(it)
                    true
                }
            )
        }
    }

    private val listAdapters = listOf(
        adapter {
            delegates(UserAdapterDelegate())
            adapterExtensions()
        },
        adapter {
            delegates(StudyGroupAdapterDelegate())
            adapterExtensions()
        },
        adapter {
            delegates(SubjectAdapterDelegate())
            adapterExtensions()
        },
        adapter {
            delegates(SpecialtyAdapterDelegate())
            adapterExtensions()
        },
        adapter {
            delegates(CourseAdapterDelegate())
            adapterExtensions()
        }
    )

    override val viewModel: FinderViewModel by viewModels { viewModelFactory }

    private lateinit var currentAdapter: ListAdapter<DomainModel, *>

    private lateinit var appBarController: AppBarController
    private lateinit var rvFinderEntities: RecyclerView
    private lateinit var finderEntityAdapter: FinderEntityAdapter
    private lateinit var searchBar: SearchBar
    override val binding by viewBinding({ FragmentFinderBinding.bind(it) })

    private fun hideMainToolbar() {
        appBarController.toolbar.visibility = View.GONE
        appBarController.addView(R.layout.searchbar_appbar)
        appBarController.addView(
            R.layout.rv_finder_entity,
            AppBarLayout.LayoutParams.SCROLL_FLAG_NO_SCROLL
        )
    }

    private fun showToolbar() {
        appBarController.removeView(rvFinderEntities)
        appBarController.removeView(searchBar)
        searchBar.detachNavigationDrawer()
        appBarController.toolbar.visibility = View.VISIBLE
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        appBarController = AppBarController.findController(requireActivity())
        hideMainToolbar()
        with(binding) {
            searchBar = requireActivity().findViewById(R.id.search_bar_finder)
            finderEntityAdapter = FinderEntityAdapter()
            rvFinderEntities = appBarController.getView(R.id.rv_finder_entity)!!
            rvFinderEntities.adapter = finderEntityAdapter
            rvFinderEntities.layoutManager =
                LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
            finderEntityAdapter.setItemClickListener { position: Int ->
                viewModel.onCurrentSearchSelect(position)
            }

            viewModel.currentSearch.collectWhenStarted(viewLifecycleOwner) { position: Int ->
                currentAdapter = listAdapters[position] as ListAdapter<DomainModel, *>
                rvFoundItems.adapter = currentAdapter
                AppBarController.findController(requireActivity())
                    .setExpandableIfViewCanScroll(rvFoundItems, viewLifecycleOwner)
                finderEntityAdapter.selectItem(position)
            }
        }
        searchBar.setOnQueryTextListener(object : SearchBar.OnQueryTextListener() {
            override fun onQueryTextSubmit(query: String) {
                viewModel.onQueryTextSubmit(query)
            }
        })
        searchBar.attachNavigationDrawer(requireActivity().findViewById(R.id.drawer_layout))
        val listStateLayout = view as ListStateLayout
        viewModel.finderEntities.observe(
            viewLifecycleOwner
        ) { list: List<ListItem> -> finderEntityAdapter.submitList(list) }

        viewModel.foundItems
            .drop(1)
            .collectWhenStarted(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Success -> {
                        listStateLayout.showList()
                        currentAdapter.submitList(
                            resource.value,
                            listStateLayout.getCommitCallback(currentAdapter)
                        )
                    }
                    Resource.Loading -> {}
                    is Resource.Error -> {
                        when (resource.failure) {
                            is NoConnection -> {
                                listStateLayout.showView(ListStateLayout.NETWORK_VIEW)
                            }
                            else -> toast("Ошибка: ${resource.failure}")
                        }
                    }
                }
            }

        viewModel.openSubjectEditor.observe(viewLifecycleOwner) { subjectId: String ->
            navController.navigate(
                R.id.action_global_subjectEditorDialog,
                bundleOf(SubjectEditorDialog.SUBJECT_ID to subjectId)
            )
        }
        viewModel.openGroupEditor.observe(viewLifecycleOwner) { groupId ->
            navController.navigate(
                R.id.action_global_groupEditorFragment,
                bundleOf(StudyGroupEditorFragment.GROUP_ID to groupId)
            )
        }
        viewModel.openSpecialtyEditor.observe(viewLifecycleOwner) { id ->
            SpecialtyEditorDialog.newInstance(id).show(
                childFragmentManager, null
            )
        }

        viewModel.openCourse.observe(viewLifecycleOwner) {
            navController.navigate(
                R.id.action_global_courseFragment,
                bundleOf(CourseFragment.COURSE_ID to it)
            )
        }
        viewModel.showOptions.observe(viewLifecycleOwner) { (first, second) ->
            val popupWindow = ListPopupWindow(
                requireActivity()
            )
            val popupAdapter = ListPopupWindowAdapter(requireContext(), second)
            popupWindow.anchorView = binding.rvFoundItems.layoutManager!!.findViewByPosition(first)
            popupWindow.setOnItemClickListener { _, _, position, _ ->
                popupWindow.dismiss()
                viewModel.onOptionClick(second[position].id)
            }
            popupWindow.width = ViewUtils.measureAdapter(popupAdapter, requireContext())
            popupWindow.horizontalOffset = Dimensions.dpToPx(12, requireActivity())
            popupWindow.setAdapter(popupAdapter)
            popupWindow.show()
        }
    }

    override fun onItemClick(position: Int) {
        viewModel.onFinderItemClick(position)
    }

    override fun onLongItemClick(position: Int) {
        viewModel.onFinderItemLongClick(position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        showToolbar()
    }
}