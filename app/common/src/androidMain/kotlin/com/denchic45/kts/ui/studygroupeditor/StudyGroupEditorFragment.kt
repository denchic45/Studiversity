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
    private val component: (
        onFinish: () -> Unit,
        studyGroupId: UUID?,
        ComponentContext,
    ) -> StudyGroupEditorComponent,
) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                val component = component(
                    findNavController()::navigateUp,
                    arguments?.getString("studyGroupId")?.toUUID(),
                    defaultComponentContext(requireActivity().onBackPressedDispatcher)
                )
//                component.appBarState.collectWhenStarted(viewLifecycleOwner) {
//                    appBarInteractor.set(it)
//                }
                StudyGroupEditorScreen(component)
            }
        }
    }

    companion object {
        const val GROUP_ID = "GroupEditorFragment GROUP_ID"
    }
}