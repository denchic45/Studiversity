package com.denchic45.kts.ui.coursework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.stuiversity.util.toUUID
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkFragment(
    private val component: (
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
) : Fragment(), HasNavArgs<CourseWorkFragmentArgs> {

    override val navArgs: CourseWorkFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                CourseWorkScreen(
                    component = component(
                        navArgs.courseId.toUUID(),
                        navArgs.elementId.toUUID(),
                        defaultComponentContext(requireActivity().onBackPressedDispatcher)
                    )
                )
            }
        }
    }
}