package com.denchic45.kts.ui.timetablefinder

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.kts.ui.timetable.DayTimetableContent
import me.tatarka.inject.annotations.Inject

@Inject
class TimetableFinderFragment(
    component: (ComponentContext) -> DayTimetableFinderComponent
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
                val selectedDate by component.selectedDate.collectAsState()
                val viewState by component.dayViewState.collectAsState()
                DayTimetableContent(
                    selectedDate = selectedDate,
                    timetableResource = viewState,
                    onDateSelect = component::onDateSelect
                )
            }
        }
    }
}