package com.denchic45.studiversity.ui.subjectEditor

import android.graphics.Color
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.denchic45.studiversity.R
import com.denchic45.studiversity.SvgColorListener
import com.denchic45.studiversity.databinding.DialogSubjectEditorBinding
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.studiversity.glideSvg.GlideApp
import com.denchic45.studiversity.rx.EditTextTransformer
import com.denchic45.studiversity.ui.adapter.ColorPickerAdapter
import com.denchic45.studiversity.ui.base.BaseDialogFragment
import com.denchic45.studiversity.ui.iconPicker.IconPickerDialog
import com.denchic45.studiversity.util.collectWhenStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jakewharton.rxbinding4.widget.textChanges

class SubjectEditorDialog : BaseDialogFragment<SubjectEditorViewModel, DialogSubjectEditorBinding>(
    R.layout.dialog_subject_editor
) {

    override val binding: DialogSubjectEditorBinding by viewBinding(DialogSubjectEditorBinding::bind)
    override val viewModel: SubjectEditorViewModel by viewModels { viewModelFactory }
    private lateinit var adapter: ColorPickerAdapter

    override fun onBuildDialog(dialog: MaterialAlertDialogBuilder, savedInstanceState: Bundle?) {
        dialog.setTitle("Редактор предметов")
            .setPositiveButton("Ок", null)
            .setNegativeButton("Отмена", null)
            .setNeutralButton("Удалить", null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        alertDialog.setOnShowListener {
            val btnDelete = alertDialog.getButton(AlertDialog.BUTTON_NEUTRAL)
            val btnPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            btnDelete.setTextColor(Color.RED)
            btnDelete.setOnClickListener { viewModel.onDeleteClick() }
            btnPositive.setOnClickListener { viewModel.onPositiveClick() }
        }

        with(binding) {
            viewModel.showColors.observe(
                viewLifecycleOwner
            ) { listWithCurrentPair ->
                adapter = ColorPickerAdapter()
                binding.rvColorPicker.adapter = adapter
                adapter.list = listWithCurrentPair.first
                adapter.setItemClickListener { position: Int ->
                    viewModel.onColorSelect(position)
                    adapter.notifyItemRangeChanged(0, adapter.itemCount)
                }
                viewModel.currentSelectedColorPosition.observe(viewLifecycleOwner) { current: Int ->
                    adapter.setCurrent(current)
                }
            }
            viewModel.deleteBtnVisibility.observe(viewLifecycleOwner) { visibility: Boolean ->
                alertDialog.getButton(
                    AlertDialog.BUTTON_NEUTRAL
                ).visibility = if (visibility) View.VISIBLE else View.GONE
            }

            viewModel.title.observe(
                viewLifecycleOwner
            ) { title: String -> dialog!!.setTitle(title) }
            viewModel.openIconPicker.observe(viewLifecycleOwner) {
                IconPickerDialog().show(
                    requireActivity().supportFragmentManager, null
                )
            }
            viewModel.enablePositiveBtn.observe(viewLifecycleOwner) { enabled: Boolean ->
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).isEnabled = enabled
            }

            viewModel.uiState.collectWhenStarted(viewLifecycleOwner) {
                it.onSuccess { state ->
                    if (!etSubjectName.text.contentEquals(state.name))
                        etSubjectName.setText(state.name)

                    com.denchic45.studiversity.glideSvg.GlideApp.with(requireActivity())
                        .`as`(PictureDrawable::class.java)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .listener(
                            SvgColorListener(ivSubjectIc, R.color.dark_blue, requireContext())
                        )
                        .load(state.iconUrl)
                        .into(ivSubjectIc)
                }
            }

//            viewModel.colorIcon.observe(viewLifecycleOwner) { color: Int ->
//                ivSubjectIc.post { ViewUtils.paintImageView(ivSubjectIc, color, requireContext()) }
//            }

            etSubjectName.textChanges()
                .skipInitialValue()
                .compose(EditTextTransformer())
                .subscribe { name: String -> viewModel.onNameType(name) }

            ivSubjectIc.setOnClickListener { viewModel.onIconClick() }
        }
    }

    companion object {
        const val SUBJECT_ID = "SUBJECT_ID"
        fun newInstance(subjectId: String?): SubjectEditorDialog {
            val fragment = SubjectEditorDialog()
            val args = Bundle()
            args.putString(SUBJECT_ID, subjectId)
            fragment.arguments = args
            return fragment
        }
    }
}