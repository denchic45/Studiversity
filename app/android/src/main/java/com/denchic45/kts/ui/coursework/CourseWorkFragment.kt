package com.denchic45.kts.ui.coursework

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.defaultComponentContext
import com.denchic45.kts.ui.appbar.AppBarInteractor
import com.denchic45.kts.ui.base.HasNavArgs
import com.denchic45.kts.ui.theme.AppTheme
import com.denchic45.stuiversity.util.toUUID
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseWorkFragment(
    private val appBarInteractor: AppBarInteractor,
    private val component: (
        onEdit: (courseId: UUID, elementId: UUID?) -> Unit,
        courseId: UUID,
        elementId: UUID,
        ComponentContext,
    ) -> CourseWorkComponent,
) : Fragment(), HasNavArgs<CourseWorkFragmentArgs> {

    override val navArgs: CourseWorkFragmentArgs by navArgs()

    private val courseWorkComponent by lazy {
        component(
            { courseId, elementId ->
                findNavController().navigate(
                    CourseWorkFragmentDirections.actionGlobalCourseWorkEditorFragment(
                        courseId.toString(),
                        elementId?.toString(),
                        null
                    )
                )
            },
            navArgs.courseId.toUUID(),
            navArgs.elementId.toUUID(),
            defaultComponentContext(requireActivity().onBackPressedDispatcher)
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            AppTheme {
                CourseWorkScreen(
                    component = courseWorkComponent,
                    appBarInteractor = appBarInteractor
                )
            }
        }
    }
}