package com.denchic45.studiversity.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.denchic45.studiversity.ui.adminPanel.finder.FinderFragment
import com.denchic45.studiversity.ui.course.CourseFragment
import com.denchic45.studiversity.ui.course.workEditor.CourseWorkEditorFragment
import com.denchic45.studiversity.ui.courseEditor.CourseEditorFragment
import com.denchic45.studiversity.ui.coursework.CourseWorkFragment
import com.denchic45.studiversity.ui.studygroup.StudyGroupFragment
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorFragment
import com.denchic45.studiversity.ui.timetableLoader.TimetableLoaderFragment
import com.denchic45.studiversity.ui.timetablefinder.TimetableFinderFragment
import com.denchic45.studiversity.ui.usereditor.UserEditorFragment
import com.denchic45.studiversity.ui.yourstudygroups.YourStudyGroupsFragment
import com.denchic45.studiversity.ui.yourtimetables.YourTimetablesFragment
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class InjectFragmentFactory(
    private val yourTimetablesFragment: () -> YourTimetablesFragment,
    private val yourStudyGroupsFragment: () -> YourStudyGroupsFragment,
    private val studyGroupFragment: () -> StudyGroupFragment,
    private val courseFragment: () -> CourseFragment,
    private val studyGroupEditorFragment: () -> StudyGroupEditorFragment,
    private val userEditorFragment: () -> UserEditorFragment,
    private val courseWorkFragment: () -> CourseWorkFragment,
    private val courseEditorFragment: () -> CourseEditorFragment,
    private val courseWorkEditorFragment: () -> CourseWorkEditorFragment,
    private val timetableFinderFragment: () -> TimetableFinderFragment,
    private val timetableLoaderFragment:()-> TimetableLoaderFragment,
    private val finderFragment: ()->FinderFragment
) : FragmentFactory() {
    override fun instantiate(classLoader: ClassLoader, className: String): Fragment {
        return when (className) {
            name<YourTimetablesFragment>() -> yourTimetablesFragment()
            name<YourStudyGroupsFragment>() -> yourStudyGroupsFragment()
            name<StudyGroupFragment>() -> studyGroupFragment()
            name<CourseFragment>() -> courseFragment()
            name<StudyGroupEditorFragment>() -> studyGroupEditorFragment()
            name<UserEditorFragment>() -> userEditorFragment()
            name<CourseWorkFragment>() -> courseWorkFragment()
            name<CourseEditorFragment>() -> courseEditorFragment()
            name<CourseWorkEditorFragment>() -> courseWorkEditorFragment()
            name<TimetableFinderFragment>() -> timetableFinderFragment()
            name<TimetableLoaderFragment>() -> timetableLoaderFragment()
            name<FinderFragment>() -> finderFragment()
            else -> super.instantiate(classLoader, className)
        }
    }

    private inline fun <reified C> name() = C::class.qualifiedName
}