package com.denchic45.kts.di.module

import com.denchic45.kts.ui.adminPanel.AdminPanelFragment
import com.denchic45.kts.ui.adminPanel.finder.FinderFragment
import com.denchic45.kts.ui.adminPanel.timetableEditor.courseChooser.CourseChooserFragment
import com.denchic45.kts.ui.confirm.ConfirmDialog
import com.denchic45.kts.ui.course.CourseFragment
import com.denchic45.kts.ui.course.courseTopicChooser.CourseTopicChooserFragment
import com.denchic45.kts.ui.course.sections.CourseTopicsFragment
import com.denchic45.kts.ui.course.submission.SubmissionDialog
import com.denchic45.kts.ui.course.workEditor.CourseWorkEditorFragment
import com.denchic45.kts.ui.creator.CreatorDialog
import com.denchic45.kts.ui.iconPicker.IconPickerDialog
import com.denchic45.kts.ui.login.auth.AuthFragment
import com.denchic45.kts.ui.login.groupChooser.GroupChooserFragment
import com.denchic45.kts.ui.login.resetPassword.ResetPasswordFragment
import com.denchic45.kts.ui.profile.ProfileFragment
import com.denchic45.kts.ui.settings.SettingsFragment
import com.denchic45.kts.ui.specialtyEditor.SpecialtyEditorDialog
import com.denchic45.kts.ui.studygroup.StudyGroupFragment
import com.denchic45.kts.ui.studygroup.courses.GroupCoursesFragment
import com.denchic45.kts.ui.studygroupeditor.StudyGroupEditorFragment
import com.denchic45.kts.ui.studygroup.users.GroupMembersFragment
import com.denchic45.kts.ui.subjectEditor.SubjectEditorDialog
import com.denchic45.kts.ui.tasks.TasksFragment
import com.denchic45.kts.ui.tasks.completed.CompletedTasksFragment
import com.denchic45.kts.ui.tasks.overdue.OverdueTasksFragment
import com.denchic45.kts.ui.tasks.upcoming.UpcomingTasksFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
interface FragmentModule {

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeSubjectEditorDialog(): SubjectEditorDialog

//    @ContributesAndroidInjector(modules = [IntentModule::class])
//    fun contributeTimetableFragment(): TimetableFragment

//    @ContributesAndroidInjector(modules = [IntentModule::class])
//    fun contributeCourseEditorFragment(): CourseEditorFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeGroupFragment(): StudyGroupFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeGroupUsersFragment(): GroupMembersFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeGroupCoursesFragment(): GroupCoursesFragment

//    @ContributesAndroidInjector
//    fun contributeLessonEditorFragment(): LessonEditorFragment

//    @ContributesAndroidInjector
//    fun contributeSimpleEventEditorFragment(): SimpleEventEditorFragment

//    @ContributesAndroidInjector
//    fun contributeGroupTimetableEditorFragment(): TimetableEditorFragment

//    @ContributesAndroidInjector
//    fun contributeGroupTimetableLoaderFragment(): TimetableLoaderFragment

//    @ContributesAndroidInjector
//    fun contributeGroupTimetableFinderFragment(): TimetableFinderFragment

//    @ContributesAndroidInjector
//    fun contributeEventEditorFragment(): EventEditorFragment

    @ContributesAndroidInjector(modules = [IntentModule::class, RawModule::class])
    fun contributeGroupEditorFragment(): StudyGroupEditorFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeSpecialtyEditorDialog(): SpecialtyEditorDialog

    @ContributesAndroidInjector(modules = [IntentModule::class, RawModule::class])
    fun contributeFinderFragment(): FinderFragment

    @ContributesAndroidInjector
    fun contributeSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    fun contributeChoiceOfGroupSubjectFragment(): CourseChooserFragment

    @ContributesAndroidInjector
    fun contributeGroupChooserFragment(): GroupChooserFragment

    @ContributesAndroidInjector
    fun contributeAdminPanelFragment(): AdminPanelFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    fun contributeResetPasswordFragment(): ResetPasswordFragment

    @ContributesAndroidInjector
    fun contributeAuthFragment(): AuthFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeCourseFragment(): CourseFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeTaskEditorFragment(): CourseWorkEditorFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeSectionPickerFragment(): CourseTopicChooserFragment

    @ContributesAndroidInjector
    fun contributeCreatorDialog(): CreatorDialog

//    @ContributesAndroidInjector(modules = [IntentModule::class])
//    fun contributeTaskFragment(): CourseWorkFragment

//    @ContributesAndroidInjector(modules = [IntentModule::class])
//    fun contributeSubmissionsFragment(): SubmissionsFragment

    @ContributesAndroidInjector
    fun contributeConfirmFragment(): ConfirmDialog

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeSubmissionFragment(): SubmissionDialog

    @ContributesAndroidInjector
    fun contributeTasksFragment(): TasksFragment

    @ContributesAndroidInjector
    fun contributeUpcomingTasksFragment(): UpcomingTasksFragment

    @ContributesAndroidInjector
    fun contributeOverdueTasksFragment(): OverdueTasksFragment

    @ContributesAndroidInjector
    fun contributeCompletedTasksFragment(): CompletedTasksFragment

    @ContributesAndroidInjector(modules = [IntentModule::class])
    fun contributeCourseSectionsFragment(): CourseTopicsFragment

    @ContributesAndroidInjector
    fun contributeIconPickerDialog(): IconPickerDialog
}