package com.denchic45.studiversity.ui.iconPicker

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.GridView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import by.kirich1409.viewbindingdelegate.viewBinding
import com.denchic45.studiversity.R
import com.denchic45.studiversity.databinding.FragmentIconPickerBinding
import com.denchic45.studiversity.ui.adapter.IconPickerAdapter
import com.denchic45.studiversity.ui.base.BaseDialogFragment
import com.denchic45.studiversity.util.collectWhenStarted
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class IconPickerDialog :
    BaseDialogFragment<IconPickerViewModel, FragmentIconPickerBinding>(R.layout.fragment_icon_picker) {
    override val viewModel: IconPickerViewModel by viewModels { viewModelFactory }
    override val binding: FragmentIconPickerBinding by viewBinding(FragmentIconPickerBinding::bind)
    private var gvIconPicker: GridView? = null
    private var adapter: IconPickerAdapter? = null

    override fun onBuildDialog(dialog: MaterialAlertDialogBuilder, savedInstanceState: Bundle?) {
        dialog.setTitle("Выбрать иконку предмета")
            .setNegativeButton("Отмена", null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        gvIconPicker = binding.root.findViewById(R.id.gv_icon_picker)
        adapter = IconPickerAdapter(requireActivity())
        gvIconPicker!!.adapter = adapter
        viewModel.showIcons.collectWhenStarted(viewLifecycleOwner, adapter!!::addAll)
        gvIconPicker!!.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position: Int, _ ->
                viewModel.onIconItemClick(position)
            }
    }
}