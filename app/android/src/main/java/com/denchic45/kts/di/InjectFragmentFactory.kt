package com.denchic45.kts.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.denchic45.kts.ui.course.CourseFragment
import com.denchic45.kts.ui.profile.ProfileFragment
import com.denchic45.kts.ui.studygroup.StudyGroupFragment
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorFragment
import com.denchic45.kts.ui.yourStudyGroups.YourStudyGroupsFragment
import com.denchic45.kts.ui.yourTimetables.YourTimetablesFragment
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class InjectFragmentFactory(
    private val yourTimetablesFragment: () -> YourTimetablesFragment,
    private val yourStudyGroupsFragment: () -> YourStudyGroupsFragment,
    private val studyGroupFragment: () -> StudyGroupFragment,
    private val courseFragment: () -> CourseFragment,
    private val profileFragment: () -> ProfileFragment,
    private val studyGroupEditorFragment: () -> StudyGroupEditorFragment
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            name<YourTimetablesFragment>() -> yourTimetablesFragment()
            name<YourStudyGroupsFragment>() -> yourStudyGroupsFragment()
            name<StudyGroupFragment>() -> studyGroupFragment()
            name<CourseFragment>() -> courseFragment()
            name<ProfileFragment>() -> profileFragment()
            name<StudyGroupEditorFragment>() -> studyGroupEditorFragment()
            else -> super.instantiate(classLoader, className)
        }
    }

    private inline fun <reified C> name() = C::class.qualifiedName
}