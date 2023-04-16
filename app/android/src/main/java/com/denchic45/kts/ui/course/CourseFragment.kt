package com.denchic45.kts.ui.course

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import by.kirich1409.viewbindingdelegate.viewBinding
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.MobileNavigationDirections
import com.denchic45.kts.databinding.FragmentCourseBinding
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.courseelements.CourseUiComponent
import com.denchic45.kts.ui.fab.FabInteractor
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.stuiversity.util.toUUID
import com.google.android.material.appbar.CollapsingToolbarLayout
import me.tatarka.inject.annotations.Inject
import java.util.*

@Inject
class CourseFragment(
    private val appBarInteractor: AppBarInteractor,
    private val fabInteractor: FabInteractor,
    component: (
        UUID,
        onOpenElement: () -> Unit, (courseId: UUID, elementId: UUID) -> Unit,
        onOpenCourseEditor: (courseId: UUID) -> Unit,
        onOpenCourseTopics: (courseId: UUID) -> Unit,
        ComponentContext,
    ) -> CourseUiComponent,
) : Fragment(), HasNavArgs<CourseFragmentArgs> {

    override val navArgs: CourseFragmentArgs by navArgs()

    val component: CourseUiComponent by lazy {
        component(
            navArgs.courseId.toUUID(),
            {},
            { courseId, elementId ->
                findNavController().navigate(
                    CourseFragmentDirections.actionCourseFragmentToContentFragment(
                        courseId.toString(),
                        elementId.toString()
                    )
                )
            },
            {
                findNavController().navigate(
                    MobileNavigationDirections.actionGlobalCourseEditorFragment(
                        it.toString()
                    )
                )
            },
            {
                findNavController().navigate(
                    CourseFragmentDirections.actionCourseFragmentToCourseSectionsFragment(it.toString())
                )
            },
            defaultComponentContext(requireActivity().onBackPressedDispatcher)
        )
    }

    val binding: FragmentCourseBinding by viewBinding(FragmentCourseBinding::bind)
    private lateinit var collapsingToolbarLayout: CollapsingToolbarLayout

    //    private lateinit var mainToolbar: Toolbar
    private lateinit var toolbar: Toolbar

    companion object {
        const val COURSE_ID = "CourseFragment COURSE_UUID"
    }

//    private lateinit var appBarController: AppBarController

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme{
                CourseScreen(
                    component = component,
                    fabInteractor = fabInteractor
                )
            }
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        appBarController = AppBarController.findController(requireActivity())
//        component.appBarState.collectWhenStarted(viewLifecycleOwner) {
//            appBarInteractor.set(it)
//        }

//        requireActivity().findViewById<FloatingActionButton>(R.id.fab_main).setOnClickListener {
//            component.onFabClick()
//        }

//        with(binding) {
//            val adapter = adapter {
//                delegates(TaskAdapterDelegate(), CourseTopicAdapterDelegate())
//                extensions {
//                    clickBuilder<TaskHolder> {
//                        onClick = {
//                            component.onItemClick(it)
//                        }
//                        onLongClick = {
//                            component.onTaskItemLongClick(it)
//                            true
//                        }
//                    }
//                }
//            }
//
//            val simpleCallback = object : ItemTouchHelper.SimpleCallback(
//                ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0
//            ) {
//                override fun onMove(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    target: RecyclerView.ViewHolder,
//                ): Boolean {
//                    val move = viewHolder is TaskHolder && target is TaskHolder
//                    if (move) {
//                        val oldPosition = viewHolder.absoluteAdapterPosition
//                        val position = target.absoluteAdapterPosition
//                        component.onContentMove(oldPosition, position)
//                    }
//                    return move
//                }
//
//                override fun onChildDraw(
//                    c: Canvas,
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                    dX: Float,
//                    dY: Float,
//                    actionState: Int,
//                    isCurrentlyActive: Boolean,
//                ) {
//                    super.onChildDraw(
//                        c,
//                        recyclerView,
//                        viewHolder,
//                        dX,
//                        dY,
//                        actionState,
//                        isCurrentlyActive
//                    )
//                    viewHolder.itemView.isSelected =
//                        actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive
//                }
//
//                override fun clearView(
//                    recyclerView: RecyclerView,
//                    viewHolder: RecyclerView.ViewHolder,
//                ) {
//                    super.clearView(recyclerView, viewHolder)
//                    component.onContentMoved()
//                }
//
//                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}
//            }
//            val itemTouchHelper = ItemTouchHelper(simpleCallback)
//
//            itemTouchHelper.attachToRecyclerView(rvCourseItems)

//            component.course.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                .collectWhenStarted(viewLifecycleOwner) {
//                    it.onSuccess {
//                        collapsingToolbarLayout.title = it.name
//                    }
//                }

//            rvCourseItems.adapter = adapter
//            component.elements.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED)
//                .collectWhenStarted(viewLifecycleOwner) {
//                    it.onSuccess {
//                        binding.root.addView(ComposeView(requireContext()).apply {
//                            setContent { Text(text = "Sample text") }
//                        })
//                        // TODO("Переиспользовать LazyColumn для desktop и android")
//                    }
//                }

//            component.fabVisibility.collectWhenStarted(viewLifecycleOwner) {
//                requireActivity().findViewById<FloatingActionButton>(R.id.fab_main)
//                    .apply {
//                        if (it) show()
//                        else hide()
//                    }
//            }
    }

//        component.openTask.observe(viewLifecycleOwner) { (taskId, courseId) ->
//            navController.navigate(
//                R.id.action_courseFragment_to_contentFragment,
//                bundleOf(ContentFragment.TASK_ID to taskId, ContentFragment.COURSE_ID to courseId)
//            )
//        }

//        component.openTaskEditor.observe(viewLifecycleOwner) { (taskId, courseId, sectionId) ->
//            navController.navigate(
//                R.id.action_courseFragment_to_taskEditorFragment,
//                bundleOf(
//                    CourseWorkEditorFragment.WORK_ID to taskId,
//                    CourseWorkEditorFragment.COURSE_ID to courseId,
//                    CourseWorkEditorFragment.SECTION_ID to sectionId
//                )
//            )
//        }

//        component.openCourseEditor.observe(viewLifecycleOwner) {
//            navController.navigate(
//                R.id.action_global_courseEditorFragment,
//                bundleOf(CourseEditorFragment.COURSE_ID to it)
//            )
//        }

//        component.openCourseSectionEditor.observe(viewLifecycleOwner) {
//            navController.navigate(
//                R.id.action_courseFragment_to_courseSectionsFragment,
//                bundleOf(CourseTopicEditorFragment.COURSE_ID to it)
//            )
//        }
//}

//    override fun collectOnOptionVisibility() {
//        component.optionsVisibility.collectWhenStarted(viewLifecycleOwner) { optionsVisibility ->
//            optionsVisibility.forEach { (itemId, visible) ->
//                toolbar.menu.findItem(itemId).isVisible = visible
//            }
//        }
//    }

    override fun onStart() {
        super.onStart()

//    collapsingToolbarLayout = requireActivity().findViewById<LinearLayout>(R.id.ll_main).let {
//        it.addView(
//            requireActivity().layoutInflater.inflate(R.layout.toolbar_course, it, false),
//            0
//        )
//        requireActivity().findViewById(R.id.toolbar_layout)
//    }
//    this@CourseFragment.toolbar = collapsingToolbarLayout.findViewById(R.id.toolbar)
//    this@CourseFragment.toolbar.apply {
//        setNavigationIcon(R.drawable.ic_arrow_back)
//        inflateMenu(R.menu.options_course)
//        setNavigationOnClickListener {
//            requireActivity().onBackPressed()
//        }
//        setOnMenuItemClickListener {
//            component.onOptionClick(it.itemId)
//            false
//        }
//        component.onCreateOptions()
//    }
    }

    override fun onStop() {
        super.onStop()
//    requireActivity().findViewById<LinearLayout>(R.id.ll_main).removeViewAt(0)
//        appBarController.removeView(collapsingToolbarLayout)
//        appBarController.addView(mainToolbar)
//        appBarController.setExpanded(expand = true, animate = false)
    }
}