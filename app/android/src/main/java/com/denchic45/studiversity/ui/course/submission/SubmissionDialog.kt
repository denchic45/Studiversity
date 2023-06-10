package com.denchic45.studiversity.ui.course.submission

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.denchic45.studiversity.R
import com.denchic45.studiversity.databinding.FragmentSubmissionBinding
import com.denchic45.studiversity.di.viewmodel.ViewModelFactory
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.rx.EditTextTransformer
import com.denchic45.studiversity.ui.course.workEditor.AttachmentAdapterDelegate
import com.denchic45.studiversity.util.ValueFilter
import com.denchic45.studiversity.util.closeKeyboard
import com.denchic45.studiversity.util.collectWhenStarted
import com.denchic45.studiversity.util.windowHeight
import com.denchic45.stuiversity.api.course.work.submission.model.SubmissionState
import com.denchic45.stuiversity.api.course.work.submission.model.WorkSubmissionContent
import com.denchic45.studiversity.widget.extendedAdapter.ItemAdapterDelegate
import com.denchic45.studiversity.widget.extendedAdapter.adapter
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.jakewharton.rxbinding4.widget.textChanges
import dagger.android.support.AndroidSupportInjection
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import javax.inject.Inject


class SubmissionDialog : BottomSheetDialogFragment() {

    private val binding: FragmentSubmissionBinding by viewBinding(FragmentSubmissionBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory<SubmissionViewModel>
    private val viewModel: SubmissionViewModel by viewModels { viewModelFactory }

    private lateinit var callback: BottomSheetBehavior.BottomSheetCallback
    private lateinit var behavior: BottomSheetBehavior<*>

    companion object {
        const val COURSE_ID = "Submission COURSE_ID"
        const val TASK_ID = "Submission TASK_ID"
        const val STUDENT_ID = "Submission STUDENT_ID"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return inflater.inflate(R.layout.fragment_submission, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        behavior = (dialog as BottomSheetDialog).behavior
        callback = object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val bottomY =
                    ((bottomSheet.parent as View).measuredHeight - bottomSheet.top - binding.footer.measuredHeight).toFloat()

                if (slideOffset < -0)
                    return
                binding.footer.y = bottomY
            }
        }

        behavior.addBottomSheetCallback(callback)

        binding.clRoot.viewTreeObserver.addOnGlobalLayoutListener {

            behavior.peekHeight = if (binding.vf.displayedChild == 0) {
                binding.flMain.height
            } else {
                binding.clReject.height
            }

            callback.onSlide(binding.root.parent as View, 0f)
        }

        with(binding) {

            etGrade.filters = arrayOf(ValueFilter(1, 5))

            etGrade.textChanges()
                .skip(1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(CharSequence::toString)
                .subscribe(viewModel::onGradeType)

            etCause.textChanges()
                .skip(1)
                .filter(CharSequence::isNotEmpty)
                .compose(EditTextTransformer())
                .subscribe(viewModel::onCauseType)

            ivReject.setOnClickListener {
                viewModel.onRejectClick()
            }

            tilGrade.setEndIconOnClickListener { viewModel.onSendGradeClick() }

            btnCommentSend.setOnClickListener {
                if (tilCommentSend.visibility == View.GONE) {
                    tilCommentSend.visibility = View.VISIBLE
                    val keyboard: InputMethodManager =
                        requireActivity().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
                    etCommentSend.requestFocus()
                    keyboard.showSoftInput(etCommentSend, 0)
                } else {
                    tilCommentSend.visibility = View.GONE
                    etCommentSend.closeKeyboard()
                }
            }


            val attachmentAdapter = adapter { delegates(AttachmentAdapterDelegate(false)) }
            val commentAdapter = adapter { delegates(ItemAdapterDelegate()) }

            rvAttachments.adapter = attachmentAdapter
            rvComments.adapter = commentAdapter

            lifecycleScope.launchWhenStarted {
                viewModel.gradeButtonVisibility.collect {
                    tilGrade.isEndIconVisible = it
                }
            }


            viewModel.showSubmission.collectWhenStarted(viewLifecycleOwner) {
                it.onSuccess { response ->
                    Glide.with(requireContext())
                        .load(response.author.avatarUrl)
                        .into(ivAvatar)

                    tvName.text = response.author.fullName

                    if (response.content?.isEmpty() == true) {
                        rvAttachments.visibility = View.VISIBLE
                        attachmentAdapter.submit((response.content as WorkSubmissionContent).attachments)
                    } else {
                        rvAttachments.visibility = View.GONE
                    }

                    tvStatus.text = if (response.grade != null) {
                        etGrade.setText(response.grade.toString())
                        "Оценено: ${response.grade}/5"
                    } else when (response.state) {
                        SubmissionState.NEW,
                        SubmissionState.CREATED -> "Не сдано"
                        SubmissionState.SUBMITTED -> "Сдано" // TODO: добавить дату и время отправки
                        SubmissionState.CANCELED_BY_AUTHOR -> "Отклонено автором"
                    }
                }
            }

            viewModel.openRejectConfirmation.observe(viewLifecycleOwner) {
                vf.displayedChild = 1
                animateHeight(requireActivity().windowHeight())
            }

            viewModel.closeRejectConfirmation.observe(viewLifecycleOwner) {
                vf.displayedChild = 0
                animateHeight(requireActivity().windowHeight())
            }

            btnCancel.setOnClickListener { viewModel.onRejectCancelClick() }

            btnReject.setOnClickListener { viewModel.onRejectConfirmClick() }
        }
    }

    private fun animateHeight(windowHeight: Int) {

        fun preMeasureViewHeight(): Int {
            val currentView = binding.vf.currentView
            currentView.measure(windowWidth, windowHeight)
            return currentView.measuredHeight
        }

        fun changeBottomSheetHeight(`val`: Int) {
            val layoutParams: ViewGroup.LayoutParams = binding.root.layoutParams
            layoutParams.height = `val`
            binding.root.layoutParams = layoutParams
        }

        val newHeight = preMeasureViewHeight() + binding.vf.paddingBottom
        val anim = ValueAnimator.ofInt(
            requireView().height,
            if (behavior.state == BottomSheetBehavior.STATE_EXPANDED) (windowHeight).coerceAtMost(
                newHeight
            )
            else
                (windowHeight - windowWidth * 9 / 16).coerceAtMost(newHeight)
        )

        anim.addUpdateListener { valueAnimator ->
            val `val` = valueAnimator.animatedValue as Int
            changeBottomSheetHeight(`val`)
        }
        anim.interpolator = DecelerateInterpolator()
        anim.duration = 300
        anim.start()
        anim.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                changeBottomSheetHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
            }
        })
    }


    private inline val windowWidth: Int
        get() {
            val view = requireActivity().window.decorView
            val insets = WindowInsetsCompat.toWindowInsetsCompat(view.rootWindowInsets, view)
                .getInsets(WindowInsetsCompat.Type.systemBars())
            return resources.displayMetrics.widthPixels - insets.left - insets.right
        }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        AndroidSupportInjection.inject(this)
    }
}

