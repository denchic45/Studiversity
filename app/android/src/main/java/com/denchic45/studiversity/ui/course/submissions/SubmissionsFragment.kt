package com.denchic45.studiversity.ui.course.submissions

//import android.os.Bundle
//import android.view.View
//import android.view.ViewGroup
//import androidx.core.os.bundleOf
//import androidx.fragment.app.viewModels
//import androidx.lifecycle.Lifecycle
//import androidx.lifecycle.lifecycleScope
//import androidx.lifecycle.repeatOnLifecycle
//import androidx.navigation.fragment.findNavController
//import by.kirich1409.viewbindingdelegate.viewBinding
//import com.bumptech.glide.Glide
//import com.denchic45.studiversity.R
//import com.denchic45.studiversity.databinding.FragmentSubmissionsBinding
//import com.denchic45.studiversity.databinding.ItemSubmissionBinding
//import com.denchic45.studiversity.domain.model.Task
//import com.denchic45.studiversity.domain.onSuccess
//import com.denchic45.studiversity.ui.adapter.BaseViewHolder
//import com.denchic45.studiversity.ui.base.BaseFragment
//import com.denchic45.studiversity.ui.course.submission.SubmissionDialog
//import com.denchic45.studiversity.util.viewBinding
//import com.denchic45.studiversity.widget.extendedAdapter.ListItemAdapterDelegate
//import com.denchic45.studiversity.widget.extendedAdapter.adapter
//import com.denchic45.studiversity.widget.extendedAdapter.extension.click
//import kotlinx.coroutines.launch
//
//class SubmissionsFragment :
//    BaseFragment<SubmissionsViewModel, FragmentSubmissionsBinding>(R.layout.fragment_submissions) {
//    override val viewModel: SubmissionsViewModel by viewModels { viewModelFactory }
//    override val binding: FragmentSubmissionsBinding by viewBinding(FragmentSubmissionsBinding::bind)
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        with(binding) {
//            val adapter = adapter {
//                delegates(SubmissionAdapterDelegate())
//                extensions {
//                    click<SubmissionAdapterDelegate.SubmissionHolder>(onClick = {
//                        viewModel.onSubmissionClick(it)
//                    })
//                }
//            }
//
//            rvSubmissions.adapter = adapter
//
//            lifecycleScope.launch {
//                repeatOnLifecycle(Lifecycle.State.STARTED) {
//                    viewModel.submissions.collect {
//                        it.onSuccess(adapter::submit)
//                    }
//                }
//            }
//
//            viewModel.openSubmission.observe(viewLifecycleOwner) { (taskId, studentId) ->
//                findNavController().navigate(
//                    R.id.action_global_submissionFragment, bundleOf(
//                        SubmissionDialog.TASK_ID to taskId, SubmissionDialog.STUDENT_ID to studentId
//                    )
//                )
//            }
//        }
//    }
//
//    companion object {
//        const val TASK_ID = "Submissions TASK_ID"
//        const val COURSE_ID = "Submissions COURSE_ID"
//    }
//}
//
//class SubmissionAdapterDelegate :
//    ListItemAdapterDelegate<Task.Submission, SubmissionAdapterDelegate.SubmissionHolder>() {
//
//    class SubmissionHolder(itemSubmissionBinding: ItemSubmissionBinding) :
//        BaseViewHolder<Task.Submission, ItemSubmissionBinding>(itemSubmissionBinding) {
//        override fun onBind(item: Task.Submission) {
//            with(binding) {
//                tvStudent.text = item.student.fullName
//                Glide.with(itemView).load(item.student.photoUrl).into(ivAvatar)
//                when (val status = item.status) {
//                    is Task.SubmissionStatus.NotSubmitted -> {
//                        tvStatus.text = "Не сдано"
//                    }
//                    is Task.SubmissionStatus.Submitted -> {
//                        tvStatus.text = "Сдано"
//                    }
//                    is Task.SubmissionStatus.Graded -> {
//                        tvStatus.text = "${status.grade}/5"
//                    }
//                    is Task.SubmissionStatus.Rejected -> {
//                        tvStatus.text = "Отклонено"
//                    }
//                }
//            }
//        }
//
//    }
//
//    override fun isForViewType(item: Any): Boolean = item is Task.Submission
//
//    override fun onBindViewHolder(item: Task.Submission, holder: SubmissionHolder) {
//        holder.onBind(item)
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup): SubmissionHolder {
//        return SubmissionHolder(parent.viewBinding(ItemSubmissionBinding::inflate))
//    }
//}