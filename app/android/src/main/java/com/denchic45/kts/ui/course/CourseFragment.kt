package com.denchic45.kts.ui.course

import android.annotation.SuppressLint
import android.graphics.Canvas
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.os.bundleOf
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.databinding.FragmentCourseBinding
import com.denchic45.kts.domain.onSuccess
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.adapter.CourseSectionAdapterDelegate
import com.denchic45.kts.ui.adapter.TaskAdapterDelegate
import com.denchic45.kts.ui.adapter.TaskHolder
import com.denchic45.kts.ui.base.BaseFragment2
import com.denchic45.kts.ui.course.content.ContentFragment
import com.denchic45.kts.ui.course.sections.CourseTopicEditorFragment
import com.denchic45.kts.ui.course.taskEditor.CourseWorkEditorFragment
import com.denchic45.kts.ui.courseEditor.CourseEditorFragment
import com.denchic45.kts.util.collectWhenStarted
import com.denchic45.widget.extendedAdapter.adapter
import com.denchic45.widget.extendedAdapter.extension.clickBuilder
import com.example.appbarcontroller.appbarcontroller.AppBarController
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CourseFragment : BaseFragment2<CourseViewModel, FragmentCourseBinding>(
    R.layout.fragment_course
), HasNavArgs<CourseFragmentArgs> {

    override val navArgs: CourseFragmentArgs by navArgs()

    override val binding: FragmentCourseBinding by viewBinding(FragmentCourseBinding::bind)
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    private lateinit var mainToolbar: Toolbar
    private lateinit var toolbar: Toolbar

    companion object {
        const val COURSE_ID = "CourseFragment COURSE_UUID"
    }

    private lateinit var appBarController: AppBarController

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appBarController = AppBarController.findController(requireActivity())

        requireActivity().findViewById<FloatingActionButton>(R.id.fab_main).setOnClickListener {
            component.onFabClick()
        }

        with(binding) {
            val adapter = adapter {
                delegates(TaskAdapterDelegate(), CourseSectionAdapterDelegate())
                extensions {
                    clickBuilder<TaskHolder> {
                        onClick = {
                            component.onItemClick(it)
                        }
                        onLongClick = {
                            component.onTaskItemLongClick(it)
                            true
                        }
                    }
                }
            }

            val simpleCallback = object : ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
            ) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val move = viewHolder is TaskHolder && target is TaskHolder
                    if (move) {
                        val oldPosition = viewHolder.absoluteAdapterPosition
                        val position = target.absoluteAdapterPosition
                        component.onContentMove(oldPosition, position)
                    }
                    return move
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    viewHolder.itemView.isSelected =
                        actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive
                }

                override fun clearView(recyclerView: RecyclerView,
                                       viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                    component.onContentMoved()
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
            }
            val itemTouchHelper = ItemTouchHelper(simpleCallback)

            itemTouchHelper.attachToRecyclerView(rvCourseItems)

            component.course.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectWhenStarted(viewLifecycleOwner) {
                    it.onSuccess {
                        collapsingToolbarLayout.title = it.name
                    }
                }

            rvCourseItems.adapter = adapter
            component.elements.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
                .collectWhenStarted(viewLifecycleOwner) {
                    it.onSuccess {
                        TODO("Переиспользовать LazyColumn для desktop и android")
                    }
            }

            component.fabVisibility.collectWhenStarted(viewLifecycleOwner) {
                requireActivity().findViewById<FloatingActionButton>(R.id.fab_main)
                    .apply {
                        if (it) show()
                        else hide()
                    }
            }
        }

        component.openTask.observe(viewLifecycleOwner) { (taskId, courseId) ->
            navController.navigate(
                R.id.action_courseFragment_to_contentFragment,
                bundleOf(ContentFragment.TASK_ID to taskId, ContentFragment.COURSE_ID to courseId)
            )
        }

        component.openTaskEditor.observe(viewLifecycleOwner) { (taskId, courseId, sectionId) ->
            navController.navigate(
                R.id.action_courseFragment_to_taskEditorFragment,
                bundleOf(
                    CourseWorkEditorFragment.WORK_ID to taskId,
                    CourseWorkEditorFragment.COURSE_ID to courseId,
                    CourseWorkEditorFragment.SECTION_ID to sectionId
                )
            )
        }

        component.openCourseEditor.observe(viewLifecycleOwner) {
            navController.navigate(
                R.id.action_global_courseEditorFragment,
                bundleOf(CourseEditorFragment.COURSE_ID to it)
            )
        }

        component.openCourseSectionEditor.observe(viewLifecycleOwner) {
            navController.navigate(
                R.id.action_courseFragment_to_courseSectionsFragment,
                bundleOf(CourseTopicEditorFragment.COURSE_ID to it)
            )
        }
    }

    override fun collectOnOptionVisibility() {
        component.optionsVisibility.collectWhenStarted(viewLifecycleOwner) { optionsVisibility ->
            optionsVisibility.forEach { (itemId, visible) ->
                toolbar.menu.findItem(itemId).isVisible = visible
            }
        }
    }

    override fun onStart() {
        super.onStart()
        appBarController.apply {
            mainToolbar = toolbar
            removeView(toolbar)
            collapsingToolbarLayout =
                addView(R.layout.toolbar_course) as CollapsingToolbarLayout
            this@CourseFragment.toolbar = collapsingToolbarLayout.findViewById(R.id.toolbar)
            this@CourseFragment.toolbar.apply {
                setNavigationIcon(R.drawable.ic_arrow_back)
                inflateMenu(R.menu.options_course)
                setNavigationOnClickListener {
                    requireActivity().onBackPressed()
                }
                setOnMenuItemClickListener {
                    component.onOptionClick(it.itemId)
                    false
                }
                component.onCreateOptions()
            }
            setLiftOnScroll(true)
        }
    }

    override fun onStop() {
        super.onStop()
        appBarController.removeView(collapsingToolbarLayout)
        appBarController.addView(mainToolbar)
        appBarController.setExpanded(expand = true, animate = false)
    }
}