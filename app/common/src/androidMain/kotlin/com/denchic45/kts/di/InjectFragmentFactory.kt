package com.denchic45.kts.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorFragment
import com.denchic45.kts.ui.yourTimetables.YourTimetablesFragment
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class InjectFragmentFactory(
    private val yourTimetablesFragment: () -> YourTimetablesFragment,
    private val studyGroupEditorFragment:()-> StudyGroupEditorFragment
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            name<YourTimetablesFragment>() -> yourTimetablesFragment()
            name<StudyGroupEditorFragment>() -> studyGroupEditorFragment()
            else -> super.instantiate(classLoader, className)
        }
    }

    private inline fun <reified C> name() = C::class.qualifiedName
}