package com.denchic45.kts.ui.timetablefinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.theme.AppTheme
import me.tatarka.inject.annotations.Inject

@Inject
class TimetableFinderFragment(
    component: (ComponentContext) -> TimetableFinderComponent,
    private val appBarInteractor: AppBarInteractor
) : Fragment() {

    private val component by lazy {
        component(defaultComponentContext(requireActivity().onBackPressedDispatcher))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnLifecycleDestroyed(viewLifecycleOwner)
        )
        setContent {
            AppTheme {
                TimetableFinderScreen(
                    component,
                    appBarInteractor
                )
            }
        }
    }
}