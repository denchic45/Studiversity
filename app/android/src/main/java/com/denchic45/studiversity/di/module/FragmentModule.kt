package com.denchic45.studiversity.di.module

import com.denchic45.studiversity.ui.adminPanel.AdminPanelFragment
import com.denchic45.studiversity.ui.adminPanel.finder.FinderFragment
import com.denchic45.studiversity.ui.adminPanel.timetableEditor.courseChooser.CourseChooserFragment
import com.denchic45.studiversity.ui.confirm.ConfirmDialog
import com.denchic45.studiversity.ui.course.CourseFragment
import com.denchic45.studiversity.ui.course.courseTopicChooser.CourseTopicChooserFragment
import com.denchic45.studiversity.ui.course.sections.CourseTopicsFragment
import com.denchic45.studiversity.ui.course.submission.SubmissionDialog
import com.denchic45.studiversity.ui.course.workEditor.CourseWorkEditorFragment
import com.denchic45.studiversity.ui.creator.CreatorDialog
import com.denchic45.studiversity.ui.iconPicker.IconPickerDialog
import com.denchic45.studiversity.ui.login.auth.AuthFragment
import com.denchic45.studiversity.ui.login.groupChooser.GroupChooserFragment
import com.denchic45.studiversity.ui.login.resetPassword.ResetPasswordFragment
import com.denchic45.studiversity.ui.settings.SettingsFragment
import com.denchic45.studiversity.ui.studygroup.StudyGroupFragment
import com.denchic45.studiversity.ui.studygroup.courses.GroupCoursesFragment
import com.denchic45.studiversity.ui.studygroupeditor.StudyGroupEditorFragment
import com.denchic45.studiversity.ui.studygroup.users.GroupMembersFragment
import com.denchic45.studiversity.ui.subjectEditor.SubjectEditorDialog
import com.denchic45.studiversity.ui.tasks.TasksFragment
import com.denchic45.studiversity.ui.tasks.completed.CompletedTasksFragment
import com.denchic45.studiversity.ui.tasks.overdue.OverdueTasksFragment
import com.denchic45.studiversity.ui.tasks.upcoming.UpcomingTasksFragment
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

//    @ContributesAndroidInjector(modules = [IntentModule::class])
//    fun contributeSpecialtyEditorDialog(): SpecialtyEditorDialog

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

//    @ContributesAndroidInjector(modules = [IntentModule::class])
//    fun contributeProfileFragment(): ProfileFragment

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