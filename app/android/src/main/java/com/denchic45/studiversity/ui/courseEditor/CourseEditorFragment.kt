package com.denchic45.studiversity.ui.courseEditor

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
import com.denchic45.studiversity.data.model.domain.ListItem
import com.denchic45.studiversity.databinding.ItemGroupInCourseBinding
import com.denchic45.studiversity.ui.adapter.BaseViewHolder
import com.denchic45.studiversity.ui.appbar.AppBarInteractor
import com.denchic45.studiversity.ui.base.HasNavArgs
import com.denchic45.studiversity.ui.courseeditor.CourseEditorComponent
import com.denchic45.studiversity.ui.courseeditor.CourseEditorScreen
import com.denchic45.studiversity.ui.theme.AppTheme
import com.denchic45.studiversity.util.viewBinding
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.studiversity.widget.extendedAdapter.ListItemAdapterDelegate
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseEditorFragment(
    private val _courseEditorComponent: (
        onFinish: () -> Unit,
        courseId: UUID?,
        ComponentContext
    ) -> CourseEditorComponent
) : Fragment(), HasNavArgs<CourseEditorFragmentArgs> {
//    override val viewModel: CourseEditorComponent by viewModels { viewModelFactory }
//    private var popupWindow: ListPopupWindow? = null

//    override val binding: FragmentCourseEditorBinding by viewBinding(FragmentCourseEditorBinding::bind)

    override val navArgs: CourseEditorFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                CourseEditorScreen(
                    component = _courseEditorComponent(
                        { requireActivity().onBackPressedDispatcher.onBackPressed() },
                        navArgs.courseId?.toUUID(),
                        defaultComponentContext(requireActivity().onBackPressedDispatcher)
                    )
                )
            }
        }
    }

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        popupWindow = ListPopupWindow(requireActivity())
//
//        binding.apply {
//            tvSubjectName.setOnClickListener { viewModel.onSubjectNameClick() }
//            ivSubjectEdit.setOnClickListener { viewModel.onSubjectEditClick() }
//
//            etSubjectName.onFocusChangeListener =
//                OnFocusChangeListener { _: View?, focus: Boolean ->
//                    viewModel.onSubjectNameFocusChange(focus)
//                }
//
//            etCourseName.textChanges()
//                .compose(EditTextTransformer())
//                .subscribe { viewModel.onCourseNameType(it) }
//
//            etSubjectName.setOnEditorActionListener(OnEditorActionListener { _, actionId: Int, _ ->
//                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
//                    viewModel.onSubjectNameType(etSubjectName.text.toString())
//                    etSubjectName.closeKeyboard()
//                    return@OnEditorActionListener true
//                }
//                false
//            })
//            viewModel.title.observe(viewLifecycleOwner, this@CourseEditorFragment::setActivityTitle)
//
//            viewModel.uiState.collectWhenStarted(viewLifecycleOwner) {
//                when (it) {
//                    is Resource.Error -> TODO()
//                    is Resource.Loading -> TODO()
//                    is Resource.Success -> {
//                        with(it.value) {
//                            if (etCourseName.text.toString() != name) etCourseName.setText(name)
//
//                            Glide.with(requireActivity())
//                                .`as`(PictureDrawable::class.java)
//                                .transition(DrawableTransitionOptions.withCrossFade())
//                                .listener(
//                                    SvgColorListener(
//                                        ivSubjectIcon,
//                                        R.color.dark_blue,
//                                        requireContext()
//                                    )
//                                )
//                                .load(subjectIconUrl)
//                                .into(ivSubjectIcon)
//
//                            tvSubjectName.text = name
//                            etSubjectName.setText("")
//                        }
//                    }
//                }
//            }
//
//            viewModel.subjectNameTypeEnable.observe(viewLifecycleOwner) { visible: Boolean ->
//                vsSubjectName.displayedChild = if (visible) 1 else 0
//                ivSubjectEdit.setImageResource(if (visible) R.drawable.ic_arrow_left else R.drawable.ic_edit)
//                if (visible) {
//                    etSubjectName.showKeyboard()
//                } else {
//                    etSubjectName.closeKeyboard()
//                }
//            }
//
//            viewModel.showFoundSubjects.collectWhenStarted(viewLifecycleOwner) { resource ->
//                when (resource) {
//                    is Resource.Error -> TODO()
//                    is Resource.Loading -> TODO()
//                    is Resource.Success -> {
//                        val subjects = resource.value
//                        popupWindow!!.anchorView = etSubjectName
//                        popupWindow!!.setAdapter(ListPopupWindowAdapter(requireContext(), subjects))
//                        popupWindow!!.setOnItemClickListener { _, _, position: Int, _ ->
//                            popupWindow!!.dismiss()
//                            viewModel.onSubjectSelect(position)
//                        }
//                        popupWindow!!.show()
//                    }
//                }
//            }
//
//            etSubjectName.textChanges()
//                .compose(EditTextTransformer())
//                .filter(NonThrowingPredicate { charSequence: CharSequence -> charSequence.length > 1 && etSubjectName.hasFocus() } as NonThrowingPredicate<CharSequence>)
//                .subscribe { subjectName ->
//                    viewModel.onSubjectNameType(
//                        subjectName
//                    )
//                }
//        }
//    }

    companion object {
        const val COURSE_ID = "CourseEditorFragment COURSE_UUID"
    }
}

class CourseGroupsAdapterDelegate :
    ListItemAdapterDelegate<ListItem, CourseGroupsAdapterDelegate.GroupHolder>() {

    class GroupHolder(val itemGroupInCourseBinding: ItemGroupInCourseBinding) :
        BaseViewHolder<ListItem, ItemGroupInCourseBinding>(itemGroupInCourseBinding) {
        override fun onBind(item: ListItem) {
            with(binding) {
                tvName.text = item.title
            }
        }
    }

    override fun isForViewType(item: Any): Boolean {
        return item is ListItem && item.type == 1
    }

    override fun onBindViewHolder(item: ListItem, holder: GroupHolder) {
        holder.onBind(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup): GroupHolder {
        return GroupHolder(parent.viewBinding(ItemGroupInCourseBinding::inflate))
    }
}
