package com.denchic45.kts.ui.studygroupeditor

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.util.collectWhenStarted
import com.denchic45.stuiversity.util.toUUID
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupEditorFragment(
    private val appBarInteractor: AppBarInteractor,
    private val component: (studyGroupId: UUID?, () -> Unit, ComponentContext) -> StudyGroupEditorComponent,
) :
    Fragment() {
//     val binding: FragmentGroupEditorBinding by viewBinding(FragmentGroupEditorBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                val component = component(
                    arguments?.getString("studyGroupId")?.toUUID(),
                    { findNavController().navigateUp() },
                    defaultComponentContext(requireActivity().onBackPressedDispatcher)
                )
                component.appBarState.collectWhenStarted(viewLifecycleOwner) {
                    appBarInteractor.set(it)
                }
                StudyGroupEditorScreen(component)
            }
        }
    }

//     val viewModel: StudyGroupEditorViewModel by viewModels { viewModelFactory }
//    private var specialtyAdapter: ListPopupWindowAdapter? = null

//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        specialtyAdapter = ListPopupWindowAdapter(requireContext(), ArrayList())
//        viewBinding.apply {
//            etSpecialty.setAdapter(specialtyAdapter)
//            etSpecialty.setOnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
//                viewModel.onSpecialtySelect(position)
//            }
//
//            requireActivity().findViewById<FloatingActionButton>(R.id.fab_main).apply {
//                show()
//                setImageResource(R.drawable.ic_done)
//                setOnClickListener { viewModel.onFabClick() }
//            }
//
//            etSpecialty.textChanges()
//                .compose(EditTextTransformer())
//                .subscribe { specialtyName: String -> viewModel.onSpecialtyNameType(specialtyName) }
//            etGroupName.textChanges()
//                .compose(EditTextTransformer())
//                .subscribe { name: String -> viewModel.onGroupNameType(name) }
//
//            etStartYear.textChanges()
//                .compose(EditTextTransformer())
//                .subscribe { viewModel.onStartYearType(it) }
//
//            etEndYear.textChanges()
//                .compose(EditTextTransformer())
//                .subscribe { viewModel.onEndYearType(it) }
//
//            viewModel.enableSpecialtyField.observe(viewLifecycleOwner) { enable: Boolean ->
//                etSpecialty.isEnabled = enable
//            }
//
//            viewModel.openTeacherChooser.observe(
//                viewLifecycleOwner
//            ) { navController.navigate(R.id.teacherChooserFragment) }
//
//            viewModel.nameField.observe(
//                viewLifecycleOwner
//            ) { text: String -> if (etGroupName.text.toString() != text) etGroupName.setText(text) }
//            viewModel.specialtyField.observe(
//                viewLifecycleOwner
//            ) { specialty ->
//                if (etSpecialty.text.toString() != specialty?.name) etSpecialty.setText(
//                    specialty?.name
//                )
//            }
//            viewModel.startYearField.observe(viewLifecycleOwner) { start ->
//                start?.let {
//                    etStartYear.setText(it.toString())
//                }
//            }
//
//            viewModel.endYearField.observe(viewLifecycleOwner) { end ->
//                end?.let {
//                    etEndYear.setText(it.toString())
//                }
//            }
//
//            viewModel.fieldErrorMessage.observe(viewLifecycleOwner) { idWithMessagePair: Pair<Int, String?> ->
//                val textInputLayout: TextInputLayout = binding.root.findViewById(
//                    idWithMessagePair.first
//                )
//                if (idWithMessagePair.second != null) {
//                    textInputLayout.error = idWithMessagePair.second
//                } else {
//                    textInputLayout.error = null
//                }
//            }
//
//            viewModel.showSpecialties.observe(
//                viewLifecycleOwner
//            ) { listItems: List<ListItem> -> specialtyAdapter!!.updateList(listItems) }
//        }
//    }

    companion object {
        const val GROUP_ID = "GroupEditorFragment GROUP_ID"
    }
}