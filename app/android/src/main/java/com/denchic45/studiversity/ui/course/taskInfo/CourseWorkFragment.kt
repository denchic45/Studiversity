package com.denchic45.studiversity.ui.course.taskInfo

//import android.os.Bundle
//import android.view.Gravity
//import android.view.View
//import android.widget.Button
//import android.widget.FrameLayout
//import android.widget.Toast
//import androidx.activity.OnBackPressedCallback
//import androidx.appcompat.widget.Toolbar
//import androidx.core.view.contains
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.lifecycleScope
//import by.kirich1409.viewbindingdelegate.viewBinding
//import com.bumptech.glide.Glide
//import com.denchic45.studiversity.R
//import com.denchic45.studiversity.data.domain.model.Attachment2
//import com.denchic45.studiversity.databinding.FragmentTaskInfoBinding
//import com.denchic45.studiversity.domain.onSuccess
//import com.denchic45.studiversity.ui.base.BaseFragment
//import com.denchic45.studiversity.ui.course.taskEditor.AddAttachmentAdapterDelegate
//import com.denchic45.studiversity.ui.course.taskEditor.AddAttachmentHolder
//import com.denchic45.studiversity.ui.course.taskEditor.AttachmentAdapterDelegate
//import com.denchic45.studiversity.ui.course.taskEditor.AttachmentHolder
//import com.denchic45.studiversity.util.*
//import com.denchic45.stuiversity.api.user.model.UserResponse
//import com.denchic45.studiversity.widget.extendedAdapter.adapter
//import com.denchic45.studiversity.widget.extendedAdapter.extension.clickBuilder
//import com.example.appbarcontroller.appbarcontroller.AppBarController
//import com.google.android.material.bottomsheet.BottomSheetBehavior
//
//class CourseWorkFragment :
//    BaseFragment<CourseWorkViewModel, FragmentTaskInfoBinding>(R.layout.fragment_task_info) {
//
//    override val binding: FragmentTaskInfoBinding by viewBinding(FragmentTaskInfoBinding::bind)
//    override val viewModel: CourseWorkViewModel by viewModels { viewModelFactory }
//    private lateinit var appBarController: AppBarController
//    private val btnActionSubmission: Button by lazy {
//        View.inflate(requireContext(), R.layout.btn_action_submission, null) as Button
//    }
//    private val adapter = adapter {
//        delegates(AttachmentAdapterDelegate(false))
//        extensions {
//            clickBuilder<AttachmentHolder> {
//                onClick = { viewModel.onAttachmentClick(it) }
//            }
//        }
//    }
//
//    private val submissionAttachmentsAdapter =
//        adapter {
//            delegates(AttachmentAdapterDelegate(), AddAttachmentAdapterDelegate())
//            extensions {
//                clickBuilder<AttachmentHolder> {
//                    onClick = {
//                        viewModel.onSubmissionAttachmentClick(it)
//                    }
//                }
//                clickBuilder<AttachmentHolder> {
//                    view = { it.binding.ivFileRemove }
//                    onClick = { position ->
//                        viewModel.onRemoveSubmissionFileClick(position)
//                    }
//                }
//                clickBuilder<AddAttachmentHolder> {
//                    onClick = {
//                        viewModel.onAddAttachmentClick()
//                    }
//                }
//            }
//        }
//
//    private val fileViewer by lazy {
//        FileViewer(requireActivity()) {
//            Toast.makeText(
//                requireContext(),
//                "Невозможно открыть файл на данном устройстве",
//                Toast.LENGTH_SHORT
//            ).show()
//        }
//    }
//
//    private lateinit var filePicker: FilePicker
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        filePicker = FilePicker(this, {
//            it?.let { viewModel.onSelectedFile(it[0]) }
//        })
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        appBarController = AppBarController.findController(requireActivity())
//
//        requireActivity().onBackPressedDispatcher
//            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
//                override fun handleOnBackPressed() {
//                    viewModel.onBackPress()
//                }
//            })
//
//        val bsBehavior = BottomSheetBehavior.from(binding.bsSubmission)
//
//        binding.submissionExpanded.rvSubmissionAttachments.adapter = submissionAttachmentsAdapter
//
//        bsBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                viewModel.onBottomSheetStateChanged(newState)
//                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                    view.postDelayed({
//                        appBarController.showToolbar()
//                    }, 100)
//                }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//                fun calcPercentOf(min: Float, max: Float, input: Float): Float {
//                    return ((input - min) * 1) / (max - min)
//                }
//
//                val fl1 = calcPercentOf(0.2F, 0.8F, slideOffset)
//                val fl2 = calcPercentOf(0.2F, 0.3F, slideOffset)
//                binding.submissionExpanded.root.alpha = fl1
//                binding.submissionCollapsed.root.alpha = 1F - fl2
//            }
//        })
//
//        binding.rvAttachments.adapter = adapter
//
//        viewModel.expandBottomSheet.observe(viewLifecycleOwner) { newState ->
//            if (bsBehavior.state != newState) {
//                bsBehavior.state = newState
//            }
//            binding.apply {
//                when (newState) {
//                    BottomSheetBehavior.STATE_EXPANDED -> {
//                        submissionExpanded.root.translationZ = 1F
//                        submissionCollapsed.root.translationZ = 0F
//
//                        submissionExpanded.root.alpha = 1F
//                        submissionCollapsed.root.alpha = 0F
//                    }
//                    BottomSheetBehavior.STATE_COLLAPSED -> {
//                        submissionExpanded.root.translationZ = 0F
//                        submissionCollapsed.root.translationZ = 1F
//
//                        submissionExpanded.root.alpha = 0F
//                        submissionCollapsed.root.alpha = 1F
//                    }
//                }
//            }
//        }
//
//        viewModel.showSubmissionToolbar.collectWhenStarted(viewLifecycleOwner) {
//            if (it) {
//                appBarController.toolbar.addView(btnActionSubmission)
//                (btnActionSubmission.layoutParams as Toolbar.LayoutParams).apply {
//                    gravity = Gravity.END
//                    marginEnd = requireContext().dpToPx(16)
//                }
//                btnActionSubmission.apply {
//                    alpha = 0F
//                    animate().alpha(1F).setDuration(100).start()
//                }
//            } else {
//                btnActionSubmission.animate()
//                    .alpha(0F)
//                    .setDuration(70)
//                    .withEndAction { appBarController.toolbar.removeView(btnActionSubmission) }
//                    .start()
//            }
//        }
//
//
//            viewModel.workUiState.collectWhenStarted(viewLifecycleOwner) { resource ->
//                with(binding) {
//                    resource.onSuccess {
//                        tvTaskName.text = it.name
//                        tvDescription.text = it.description
//                        tvDescription.visibility = if (it.description?.isNotEmpty() == true)
//                            View.VISIBLE
//                        else View.GONE
//
//                        it.dueDateTime?.let { dueDateTime ->
//                            chpDate.visibility = View.VISIBLE
//                            tvDueDateTime.visibility = View.VISIBLE
//                            chpDate.text = "Напомнить"
//                            tvDueDateTime.text = dueDateTime
//                        } ?: run {
//                            chpDate.visibility = View.GONE
//                            tvDueDateTime.visibility = View.GONE
//                        }
//                        tvComments.text = "Написать комментарий"
//                    }
//                }
//            }
//
//        lifecycleScope.launchWhenStarted {
//            viewModel.workAttachments.collect {
//                it.onSuccess {
//                    binding.tvAttachmentsHeader.visibility =
//                        if (it.isEmpty()) View.GONE else View.VISIBLE
//                    adapter.submit(it)
//                }
//            }
//        }
//
//        binding.apply {
//            btnActionSubmission.setOnClickListener { viewModel.onActionClick() }
//            submissionCollapsed.btnAction.setOnClickListener { viewModel.onActionClick() }
//
//            fun setTeacherData(gradedBy: UserResponse?) {
//                gradedBy?.let {
//                    Glide.with(requireContext())
//                        .load(gradedBy.avatarUrl)
//                        .into(submissionExpanded.ivAvatar)
//                    submissionExpanded.tvTeacherName.text = gradedBy.fullName
//                    submissionExpanded.grpTeacher.visibility = View.VISIBLE
//                } ?: run {
//                    submissionExpanded.grpTeacher.visibility = View.GONE
//                }
//            }
//
//            lifecycleScope.launchWhenStarted {
//                viewModel.submissionUiState.collect { resource ->
//
//                    resource.onSuccess { uiState ->
//                        applyActionButtonProperties {
//                            if (uiState.btnVisibility) {
//                                visibility = View.VISIBLE
//                                setTextColor(requireContext().colors(uiState.btnTextColor))
//                                setBackgroundColor(requireContext().colors(uiState.btnBackgroundColor))
//                                text = uiState.btnText
//
//                            } else {
//                                visibility = View.GONE
//                            }
//                        }
//
//                        submissionExpanded.tvExpandState.text = uiState.title
//                        submissionCollapsed.tvCollapseState.text = uiState.title
//                        uiState.subtitle?.let { subtitle ->
//                            submissionCollapsed.tvCollapseDescription.visibility = View.VISIBLE
//                            submissionExpanded.tvExpandDescription.visibility = View.VISIBLE
//                            submissionCollapsed.tvCollapseDescription.text = subtitle
//                            submissionExpanded.tvExpandDescription.text = subtitle
//                        } ?: run {
//                            submissionCollapsed.tvCollapseDescription.visibility = View.GONE
//                            submissionExpanded.tvExpandDescription.visibility = View.GONE
//                        }
//
//                        setTeacherData(uiState.gradedBy)
//
//
//                        measureBottomSheetPeek(bsBehavior)
//                    }
//                }
//            }
//
//            viewModel.submissionAttachments.collectWhenStarted(viewLifecycleOwner) {
//                it.onSuccess { attachments ->
//                    setSubmissionContent(attachments)
//                }
//            }
//        }
//
//        viewModel.openAttachment.observe(viewLifecycleOwner) { fileViewer.openFile(it) }
//
//        viewModel.openFilePicker.observe(viewLifecycleOwner) { filePicker.selectFiles() }
//    }
//
//
//    private fun setSubmissionContent(attachments: List<Attachment2>) {
//        submissionAttachmentsAdapter.submit(attachments)
//    }
//
//    private fun measureBottomSheetPeek(bsBehavior: BottomSheetBehavior<FrameLayout>) {
//        binding.submissionCollapsed.root.post {
//            bsBehavior.setPeekHeight(
//                binding.submissionCollapsed.clRoot.height + Dimensions.dpToPx(
//                    24, requireContext()
//                )
//            )
//        }
//    }
//
//    private fun applyActionButtonProperties(buttonReceiver: Button.() -> Unit) {
//        buttonReceiver(btnActionSubmission)
//        buttonReceiver(binding.submissionCollapsed.btnAction)
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        if (appBarController.toolbar.contains(btnActionSubmission))
//            appBarController.toolbar.removeView(btnActionSubmission)
//    }
//
//    companion object {
//        const val TASK_ID = "TaskInfo TASK_ID"
//        const val COURSE_ID = "TaskInfo COURSE_ID"
//    }
//}
