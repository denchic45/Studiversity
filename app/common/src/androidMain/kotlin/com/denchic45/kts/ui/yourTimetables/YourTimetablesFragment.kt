package com.denchic45.kts.ui.yourTimetables

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
import me.tatarka.inject.annotations.Inject

@Inject
class YourTimetablesFragment(
    private val appBarInteractor: AppBarInteractor,
    private val component: (ComponentContext) -> YourTimetablesComponent
) :
    Fragment() {


    private val yourTimetablesComponent by lazy {
        component(defaultComponentContext(requireActivity().onBackPressedDispatcher))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            YourTimetablesScreen(yourTimetablesComponent, appBarInteractor)
        }
    }
}