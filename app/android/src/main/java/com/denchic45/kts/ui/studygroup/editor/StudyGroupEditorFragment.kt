package com.denchic45.kts.ui.studygroup.editor

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import androidx.fragment.app.viewModels
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.kts.R
import com.denchic45.kts.customPopup.ListPopupWindowAdapter
import com.denchic45.kts.data.model.domain.ListItem
import com.denchic45.kts.databinding.FragmentGroupEditorBinding
import com.denchic45.kts.rx.EditTextTransformer
import com.denchic45.kts.ui.base.BaseFragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textfield.TextInputLayout
import com.jakewharton.rxbinding4.widget.textChanges

class StudyGroupEditorFragment :
    BaseFragment<StudyGroupEditorViewModel, FragmentGroupEditorBinding>(
        R.layout.fragment_group_editor,
        R.menu.options_group_editor
    ) {

    override val binding: FragmentGroupEditorBinding by viewBinding(FragmentGroupEditorBinding::bind)
    override val viewModel: StudyGroupEditorViewModel by viewModels { viewModelFactory }
    private val viewBinding: FragmentGroupEditorBinding by viewBinding(FragmentGroupEditorBinding::bind)
    private var specialtyAdapter: ListPopupWindowAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val curatorHeader = view.findViewById<TextView>(R.id.tv_header)
        specialtyAdapter = ListPopupWindowAdapter(requireContext(), ArrayList())
        viewBinding.apply {
            etSpecialty.setAdapter(specialtyAdapter)
//            etCourse.setAdapter(ListPopupWindowAdapter(requireContext(), viewModel.courseList))
//            etCourse.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
//                viewModel.onAcademicYearSelect(position)
//            }
            etSpecialty.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                viewModel.onSpecialtySelect(position)
            }

            requireActivity().findViewById<FloatingActionButton>(R.id.fab_main).apply {
                show()
                setImageResource(R.drawable.ic_done)
                setOnClickListener { viewModel.onFabClick() }
            }


//            curatorHeader.text = "Куратор"
            etSpecialty.textChanges()
                .compose(EditTextTransformer())
                .subscribe { specialtyName: String -> viewModel.onSpecialtyNameType(specialtyName) }
            etGroupName.textChanges()
                .compose(EditTextTransformer())
                .subscribe { name: String -> viewModel.onGroupNameType(name) }

            etStartYear.textChanges()
                .compose(EditTextTransformer())
                .subscribe { viewModel.onStartYearType(it) }

            etEndYear.textChanges()
                .compose(EditTextTransformer())
                .subscribe { viewModel.onEndYearType(it) }

            viewModel.enableSpecialtyField.observe(viewLifecycleOwner) { enable: Boolean ->
                etSpecialty.isEnabled = enable
            }

            viewModel.openTeacherChooser.observe(
                viewLifecycleOwner
            ) { navController.navigate(R.id.teacherChooserFragment) }


            viewModel.nameField.observe(
                viewLifecycleOwner
            ) { text: String -> if (etGroupName.text.toString() != text) etGroupName.setText(text) }
            viewModel.specialtyField.observe(
                viewLifecycleOwner
            ) { specialty ->
                if (etSpecialty.text.toString() != specialty?.name) etSpecialty.setText(
                    specialty?.name
                )
            }
            viewModel.startYearField.observe(viewLifecycleOwner) { start ->
                start?.let {
                    etStartYear.setText(it.toString())
                }
            }

            viewModel.endYearField.observe(viewLifecycleOwner) { end ->
                end?.let {
                    etEndYear.setText(it.toString())
                }
            }

            viewModel.fieldErrorMessage.observe(viewLifecycleOwner) { idWithMessagePair: Pair<Int, String?> ->
                val textInputLayout: TextInputLayout = binding.root.findViewById(
                    idWithMessagePair.first
                )
                if (idWithMessagePair.second != null) {
                    textInputLayout.error = idWithMessagePair.second
                } else {
                    textInputLayout.error = null
                }
            }

            viewModel.showSpecialties.observe(
                viewLifecycleOwner
            ) { listItems: List<ListItem> -> specialtyAdapter!!.updateList(listItems) }

        }
    }

    companion object {
        const val GROUP_ID = "GroupEditorFragment GROUP_ID"
    }
}