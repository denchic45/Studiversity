package com.denchic45.kts.ui.courseEditor

import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView.OnEditorActionListener
import androidx.appcompat.widget.ListPopupWindow
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.denchic45.kts.R
import com.denchic45.kts.SvgColorListener
import com.denchic45.kts.customPopup.ListPopupWindowAdapter
import com.denchic45.kts.data.model.domain.ListItem
import com.denchic45.kts.databinding.FragmentCourseEditorBinding
import com.denchic45.kts.databinding.ItemGroupInCourseBinding
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.rx.EditTextTransformer
import com.denchic45.kts.ui.adapter.BaseViewHolder
import com.denchic45.kts.ui.base.BaseFragment
import com.denchic45.kts.ui.courseeditor.CourseEditorComponent
import com.denchic45.kts.util.*
import com.denchic45.widget.extendedAdapter.ListItemAdapterDelegate
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.internal.util.AppendOnlyLinkedArrayList.NonThrowingPredicate

class CourseEditorFragment : BaseFragment<CourseEditorComponent, FragmentCourseEditorBinding>(
    layoutId = R.layout.fragment_course_editor,
    menuResId = R.menu.options_course_editor
) {
    override val viewModel: CourseEditorComponent by viewModels { viewModelFactory }
    private var popupWindow: ListPopupWindow? = null

    override val binding: FragmentCourseEditorBinding by viewBinding(FragmentCourseEditorBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        popupWindow = ListPopupWindow(requireActivity())

        binding.apply {
            tvSubjectName.setOnClickListener { viewModel.onSubjectNameClick() }
            ivSubjectEdit.setOnClickListener { viewModel.onSubjectEditClick() }

            etSubjectName.onFocusChangeListener =
                OnFocusChangeListener { _: View?, focus: Boolean ->
                    viewModel.onSubjectNameFocusChange(focus)
                }

            etCourseName.textChanges()
                .compose(EditTextTransformer())
                .subscribe { viewModel.onCourseNameType(it) }

            etSubjectName.setOnEditorActionListener(OnEditorActionListener { _, actionId: Int, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    viewModel.onSubjectNameType(etSubjectName.text.toString())
                    etSubjectName.closeKeyboard()
                    return@OnEditorActionListener true
                }
                false
            })
            viewModel.title.observe(viewLifecycleOwner, this@CourseEditorFragment::setActivityTitle)

            viewModel.uiState.collectWhenStarted(viewLifecycleOwner) {
                when (it) {
                    is Resource.Error -> TODO()
                    is Resource.Loading -> TODO()
                    is Resource.Success -> {
                        with(it.value) {
                            if (etCourseName.text.toString() != name) etCourseName.setText(name)

                            Glide.with(requireActivity())
                                .`as`(PictureDrawable::class.java)
                                .transition(DrawableTransitionOptions.withCrossFade())
                                .listener(
                                    SvgColorListener(
                                        ivSubjectIcon,
                                        R.color.dark_blue,
                                        requireContext()
                                    )
                                )
                                .load(subjectIconUrl)
                                .into(ivSubjectIcon)

                            tvSubjectName.text = name
                            etSubjectName.setText("")
                        }
                    }
                }
            }

            viewModel.subjectNameTypeEnable.observe(viewLifecycleOwner) { visible: Boolean ->
                vsSubjectName.displayedChild = if (visible) 1 else 0
                ivSubjectEdit.setImageResource(if (visible) R.drawable.ic_arrow_left else R.drawable.ic_edit)
                if (visible) {
                    etSubjectName.showKeyboard()
                } else {
                    etSubjectName.closeKeyboard()
                }
            }

            viewModel.showFoundSubjects.collectWhenStarted(viewLifecycleOwner) { resource ->
                when (resource) {
                    is Resource.Error -> TODO()
                    is Resource.Loading -> TODO()
                    is Resource.Success -> {
                        val subjects = resource.value
                        popupWindow!!.anchorView = etSubjectName
                        popupWindow!!.setAdapter(ListPopupWindowAdapter(requireContext(), subjects))
                        popupWindow!!.setOnItemClickListener { _, _, position: Int, _ ->
                            popupWindow!!.dismiss()
                            viewModel.onSubjectSelect(position)
                        }
                        popupWindow!!.show()
                    }
                }
            }

            etSubjectName.textChanges()
                .compose(EditTextTransformer())
                .filter(NonThrowingPredicate { charSequence: CharSequence -> charSequence.length > 1 && etSubjectName.hasFocus() } as NonThrowingPredicate<CharSequence>)
                .subscribe { subjectName ->
                    viewModel.onSubjectNameType(
                        subjectName
                    )
                }
        }
    }

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
