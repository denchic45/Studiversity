package com.denchic45.kts.ui.course.workEditor

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.denchic45.kts.R
import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.domain.model.AttachmentFile
import com.denchic45.kts.data.domain.model.AttachmentLink
import com.denchic45.kts.databinding.ItemAddAttachmentBinding
import com.denchic45.kts.databinding.ItemAttachmentBinding
import com.denchic45.kts.ui.adapter.BaseViewHolder
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.courseworkeditor.CourseWorkEditorComponent
import com.denchic45.kts.ui.courseworkeditor.CourseWorkEditorScreen
import com.denchic45.kts.ui.model.UiModel
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.util.getType
import com.denchic45.kts.util.viewBinding
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.widget.extendedAdapter.ListItemAdapterDelegate
import me.tatarka.inject.annotations.Inject
import java.util.UUID


@Inject
class CourseWorkEditorFragment(
    private val _courseWorkEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID,
        workId: UUID?,
        topicId: UUID?,
        ComponentContext
    ) -> CourseWorkEditorComponent
) : Fragment(), HasNavArgs<CourseWorkEditorFragmentArgs> {

    companion object {
        const val WORK_ID = "TaskEditor TASK_ID"
        const val COURSE_ID = "TaskEditor COURSE_ID"
        const val SECTION_ID = "SECTION_ID"
    }

    override val navArgs: CourseWorkEditorFragmentArgs by navArgs()

    private val component by lazy {
        _courseWorkEditorComponent(
            { requireActivity().onBackPressedDispatcher.onBackPressed() },
            navArgs.courseId.toUUID(),
            navArgs.workId?.toUUID(),
            navArgs.topicId?.toUUID(),
            defaultComponentContext(requireActivity().onBackPressedDispatcher)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                CourseWorkEditorScreen(component)
            }
        }
    }

//    private val adapter = adapter {
//        delegates(AttachmentAdapterDelegate())
//        extensions {
//            clickBuilder<AttachmentHolder> {
//                view = { it.binding.ivFileRemove }
//                onClick = { position ->
//                    viewModel.onRemoveAttachmentClick(position)
//                }
//            }
//            clickBuilder<AttachmentHolder> {
//                onClick = { position: Int -> viewModel.onAttachmentClick(position) }
//            }
//        }
//    }

//    private lateinit var filePicker: FilePicker

//    override val viewModel: CourseWorkEditorViewModel by viewModels { viewModelFactory }

//    @Inject
//    lateinit var courseTopicChooserViewModelFactory: ViewModelFactory<CourseTopicChooserViewModel>

//    private val courseTopicChooserViewModel: CourseTopicChooserViewModel by viewModels(
//        ownerProducer = { this }, factoryProducer = { courseTopicChooserViewModelFactory }
//    )

//    override val binding: FragmentTaskEditorBinding by viewBinding(FragmentTaskEditorBinding::bind)

//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        filePicker = FilePicker(this, {
//            it?.let { viewModel.onFilesSelect(it) }
//        }, true)
//    }

//    @SuppressLint("ClickableViewAccessibility")
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        appBarController = AppBarController.findController(requireActivity()).apply {
//            oldToolbarScrollFlags = toolbarScrollFlags
//            setExpanded(true, false)
//            toolbarScrollFlags = 0
//        }
//        with(binding) {
//
//            rvFiles.adapter = adapter
//
//            etName.textChanges()
//                .compose(EditTextTransformer())
//                .subscribe(viewModel::onNameType)
//
//            etDescription.textChanges()
//                .compose(EditTextTransformer())
//                .subscribe(viewModel::onDescriptionType)
//
////            etAttachmentsLimit.textChanges()
////                .compose(EditTextTransformer())
////                .subscribe(viewModel::onAttachmentsLimitType)
//
////            etAttachmentsSizeLimit.textChanges()
////                .compose(EditTextTransformer())
////                .subscribe(viewModel::onAttachmentsSizeLimitType)
//
//            etAttachmentsLimit.filters = arrayOf(ValueFilter(0, 99), InputFilter.LengthFilter(2))
//
//            etAttachmentsSizeLimit.filters = arrayOf(ValueFilter(0, 999), InputFilter.LengthFilter(3))
//
//            actSection.setOnTouchListener { _, event ->
//                if (MotionEvent.ACTION_DOWN == event.action)
//                    viewModel.onTopicClick()
//                false
//            }
//
//            llAvailabilityDate.setOnClickListener { viewModel.onAvailabilityDateClick() }
//
//            ivRemoveAvailabilityDate.setOnClickListener { viewModel.onRemoveAvailabilityDate() }
//
//            cbAvailabilitySend.setOnCheckedChangeListener { _, check ->
//                viewModel.onAvailabilitySendCheck(check)
//            }
//
//            cbCommentsEnable.setOnCheckedChangeListener { _, check ->
//                viewModel.onCommentsEnableCheck(check)
//            }
//
//            viewModel.showErrorMessage.observe(viewLifecycleOwner) { idWithMessagePair ->
//                val fieldView = view.findViewById<View>(
//                    idWithMessagePair.first
//                )
//
//                if (fieldView is TextInputLayout) {
//                    if (idWithMessagePair.second != null) {
//                        fieldView.error = idWithMessagePair.second
//                    } else {
//                        fieldView.error = null
//                    }
//                } else if (fieldView is EditText) {
//                    if (idWithMessagePair.second != null) {
//                        fieldView.error = idWithMessagePair.second
//                    } else {
//                        fieldView.error = null
//                    }
//                }
//            }
//
//            viewModel.nameField.collectWhenStarted(viewLifecycleOwner) {
//                if (etName.text.toString() != it) etName.setText(it)
//            }
//
//            viewModel.descriptionField.collectWhenStarted(viewLifecycleOwner) {
//                if (etDescription.text.toString() != it) etDescription.setText(it)
//            }
//
//            viewModel.showCompletionDate.observe(viewLifecycleOwner) {
//                val dateNotNull = it != null
//                if (dateNotNull) {
//                    tvAvailabilityDate.text = it
//                } else {
//                    tvAvailabilityDate.text = "Без срока сдачи"
//                }
//                ImageViewCompat.setImageTintList(
//                    ivAvailabilityDate,
//                    if (dateNotNull)
//                        ColorStateList.valueOf(
//                            ContextCompat.getColor(
//                                requireContext(),
//                                R.color.blue
//                            )
//                        )
//                    else null
//                )
//                tvAvailabilityDate.setTextColor(
//                    ContextCompat.getColor(
//                        requireContext(),
//                        if (dateNotNull) R.color.blue
//                        else R.color.dark_gray
//                    )
//                )
//            }
//
//            viewModel.selectedTopic.collectWhenStarted(viewLifecycleOwner) {
//                actSection.setText(it?.name ?: "Без темы")
//            }
//
//            viewModel.openCourseTopics.observe(viewLifecycleOwner) { courseId ->
//                CourseTopicChooserFragment.newInstance(courseId)
//                    .show(childFragmentManager, null)
//            }
//
//                courseTopicChooserViewModel.selectedTopic.collectWhenStarted(viewLifecycleOwner) {
//                    viewModel.onTopicSelected(it)
//                }
//
//            viewModel.openAttachment.observe(viewLifecycleOwner, fileViewer::openFile)
//
//            viewModel.availabilityDateRemoveVisibility.observe(viewLifecycleOwner) {
//                ivRemoveAvailabilityDate.visibility = if (it) View.VISIBLE else View.GONE
//            }
//
//            viewModel.filesVisibility.collectWhenStarted(viewLifecycleOwner) {
//                    tvHeaderFiles.visibility = if (it) View.VISIBLE else View.GONE
//                }
//
//            viewModel.openDatePicker.observe(viewLifecycleOwner) {
//                val picker = MaterialDatePicker.Builder.datePicker()
//                    .setCalendarConstraints(
//                        CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
//                            .build()
//                    )
//                    .setSelection(it)
//                    .build()
//
//                picker.show(parentFragmentManager, null)
//                picker.addOnPositiveButtonClickListener(viewModel::onAvailabilityDateSelect)
//            }
//
//            viewModel.openTimePicker.observe(viewLifecycleOwner) {
//                val picker = MaterialTimePicker.Builder()
//                    .setTimeFormat(CLOCK_24H)
//                    .setHour(it.first)
//                    .setMinute(it.second)
//                    .build()
//                picker.show(parentFragmentManager, null)
//                picker.addOnPositiveButtonClickListener {
//                    viewModel.onAvailabilityTimeSelect(
//                        picker.hour,
//                        picker.minute
//                    )
//                }
//            }
//
//            viewModel.openFileChooser.observe(viewLifecycleOwner) {
//                filePicker.selectFiles()
//            }
//
//            viewModel.attachmentItems.collectWhenStarted(viewLifecycleOwner) {
//                adapter.submit(it)
//            }
//
//            viewModel.disabledSendAfterDate.observe(viewLifecycleOwner) {
//                if (cbAvailabilitySend.isChecked != it)
//                    cbAvailabilitySend.isChecked = it
//            }
//
//            viewModel.commentsEnabled.observe(viewLifecycleOwner) {
//                if (cbCommentsEnable.isChecked != it)
//                    cbCommentsEnable.isChecked = it
//            }
//        }
//    }

//    override fun onDestroyView() {
//        super.onDestroyView()
//        appBarController.toolbarScrollFlags = oldToolbarScrollFlags
//    }
}

class AttachmentAdapterDelegate(private val crossBtnVisibility: Boolean = true) :
    ListItemAdapterDelegate<Attachment, AttachmentHolder>() {
    override fun isForViewType(item: Any): Boolean = item is Attachment

    override fun onBindViewHolder(item: Attachment, holder: AttachmentHolder) {
        holder.onBind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): AttachmentHolder {
        return AttachmentHolder(
            parent.viewBinding(ItemAttachmentBinding::inflate),
            crossBtnVisibility
        )
    }
}

class AddAttachmentAdapterDelegate :
    ListItemAdapterDelegate<AddAttachmentItem, AddAttachmentHolder>() {
    override fun isForViewType(item: Any): Boolean = item is AddAttachmentItem

    override fun onBindViewHolder(item: AddAttachmentItem, holder: AddAttachmentHolder) {
        holder.onBind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): AddAttachmentHolder {
        return AddAttachmentHolder(parent.viewBinding(ItemAddAttachmentBinding::inflate))
    }

}


object AddAttachmentItem : UiModel {
    override fun equals(other: Any?): Boolean = other is AddAttachmentItem
}

class AddAttachmentHolder(itemAddAttachmentBinding: ItemAddAttachmentBinding) :
    BaseViewHolder<AddAttachmentItem, ItemAddAttachmentBinding>(itemAddAttachmentBinding) {
    override fun onBind(item: AddAttachmentItem) {

    }
}

class AttachmentHolder(
    itemAttachmentBinding: ItemAttachmentBinding,
    private val crossBtnVisibility: Boolean,
) :
    BaseViewHolder<Attachment, ItemAttachmentBinding>(itemAttachmentBinding) {
    override fun onBind(item: Attachment) {
        with(binding) {
            if (!crossBtnVisibility) {
                ivFileRemove.visibility = View.GONE
            }
            ivOverlay.setImageDrawable(null)
            when (item) {
                is AttachmentFile -> {
                    tvName.text = item.shortName
                    val glide = Glide.with(ivFile)
                    val load: RequestBuilder<Drawable> = when (item.file.getType()) {
                        "image" -> {
                            glide.load(item.file)
                        }

                        "video" -> {
                            ivOverlay.setImageResource(R.drawable.play_video_btn)
                            glide.load(item.file)
                        }

                        "audio" -> {
                            glide.load(R.drawable.ic_audio_file)
                        }

                        "document" -> {
                            glide.load(R.drawable.ic_document_file)
                        }

                        "sheet" -> {
                            glide.load(R.drawable.ic_sheet_file)
                        }

                        "presentation" -> {
                            glide.load(R.drawable.ic_presentation_file)
                        }

                        "text" -> {
                            glide.load(R.drawable.ic_text_file)
                        }

                        "pdf" -> {
                            glide.load(R.drawable.ic_pdf_file)
                        }

                        else -> {
                            glide.load(R.drawable.ic_unknow_file)
                        }
                    }
                    load
                        .placeholder(R.drawable.loading_attachment)
                        .into(ivFile)
                }

                is AttachmentLink -> TODO()
            }

        }
    }


}