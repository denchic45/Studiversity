package com.denchic45.kts.ui.timetableLoader

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
class TimetableLoaderFragment(
   private val appBarInteractor: AppBarInteractor,
    _timetableLoaderComponent: (ComponentContext) -> TimetableLoaderComponent,
) : Fragment() {
    private val timetableLoaderComponent by lazy {
        _timetableLoaderComponent(defaultComponentContext(requireActivity().onBackPressedDispatcher))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                TimetableLoaderScreen(timetableLoaderComponent,appBarInteractor)
            }
        }
    }
}