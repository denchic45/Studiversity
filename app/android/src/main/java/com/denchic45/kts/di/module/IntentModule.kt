package com.denchic45.kts.di.module

import com.denchic45.kts.ui.avatar.FullImageActivity
import com.denchic45.kts.ui.course.CourseFragment
import com.denchic45.kts.ui.course.content.ContentFragment
import com.denchic45.kts.ui.course.sections.CourseTopicEditorFragment
import com.denchic45.kts.ui.course.submission.SubmissionDialog
import com.denchic45.kts.ui.course.submissions.SubmissionsFragment
import com.denchic45.kts.ui.course.taskEditor.CourseWorkEditorFragment
import com.denchic45.kts.ui.course.taskInfo.CourseWorkFragment
import com.denchic45.kts.ui.courseEditor.CourseEditorFragment
import com.denchic45.kts.ui.studygroup.StudyGroupFragment
import com.denchic45.kts.ui.studygroup.courses.GroupCoursesFragment
import com.denchic45.kts.ui.studygroup.editor.StudyGroupEditorFragment
import com.denchic45.kts.ui.studygroup.users.GroupMembersFragment
import com.denchic45.kts.ui.profile.ProfileFragment
import com.denchic45.kts.ui.profile.fullAvatar.FullAvatarActivity
import com.denchic45.kts.ui.specialtyEditor.SpecialtyEditorDialog
import com.denchic45.kts.ui.subjectEditor.SubjectEditorDialog
import com.denchic45.kts.ui.timetable.TimetableFragment
import com.denchic45.kts.ui.userEditor.UserEditorFragment
import dagger.Module
import dagger.Provides
import javax.inject.Named

@Module
object IntentModule {

    @Named(CourseEditorFragment.COURSE_ID)
    @Provides
    fun provideCourseIdToCourseEditor(fragment: CourseEditorFragment): String? {
        return fragment.requireArguments().getString(CourseEditorFragment.COURSE_ID)
    }

    @Named(GroupCoursesFragment.GROUP_ID)
    @Provides
    fun provideGroupIdFromGroupCourses(groupCoursesFragment: GroupCoursesFragment): String? {
        return groupCoursesFragment.requireArguments().getString(GroupCoursesFragment.GROUP_ID)
    }

    @Named(StudyGroupEditorFragment.GROUP_ID)
    @Provides
    fun provideGroupIdFromGroupEditor(studyGroupEditorFragment: StudyGroupEditorFragment): String? {
        return studyGroupEditorFragment.requireArguments().getString(StudyGroupEditorFragment.GROUP_ID)
    }

    @Named(SubjectEditorDialog.SUBJECT_ID)
    @Provides
    fun provideSubjectId(dialog: SubjectEditorDialog): String? {
        return dialog.requireArguments().getString(SubjectEditorDialog.SUBJECT_ID)
    }

    @Named(TimetableFragment.GROUP_ID)
    @Provides
    fun provideAffiliation(timetableFragment: TimetableFragment): String? {
        return timetableFragment.arguments?.getString(TimetableFragment.GROUP_ID)
    }

    @Named(FullImageActivity.IMAGE_URL)
    @Provides
    fun providePhotoUrlFromFullAvatarActivity(fullAvatarActivity: FullAvatarActivity): String {
        return fullAvatarActivity.intent.getStringExtra(FullImageActivity.IMAGE_URL)!!
    }

    @Named(ProfileFragment.USER_ID)
    @Provides
    fun provideUserIdFromProfileFragment(profileFragment: ProfileFragment): String {
        return profileFragment.navArgs.userId
    }

    @Named(StudyGroupFragment.GROUP_ID)
    @Provides
    fun provideGroupIdFromGroup(studyGroupFragment: StudyGroupFragment): String? {
        return studyGroupFragment.navArgs.groupId
    }

    @Named(GroupMembersFragment.GROUP_ID)
    @Provides
    fun provideGroupIdFromGroupUsers(groupMembersFragment: GroupMembersFragment): String? {
        return (groupMembersFragment.requireParentFragment() as StudyGroupFragment).navArgs.groupId
    }

    @Named("UserEditor ${UserEditorFragment.USER_ID}")
    @Provides
    fun provideUserIdFromUserEditor(userEditorFragment: UserEditorFragment): String? {
        return userEditorFragment.navArgs.userId
    }

    @Named(UserEditorFragment.USER_ROLE)
    @Provides
    fun provideUserRole(userEditorFragment: UserEditorFragment): String {
        return userEditorFragment.navArgs.role
    }

    @Named(UserEditorFragment.USER_GROUP_ID)
    @Provides
    fun provideUserGroupId(userEditorFragment: UserEditorFragment): String? {
        return userEditorFragment.navArgs.groupId
    }

    @Named(SpecialtyEditorDialog.SPECIALTY_ID)
    @Provides
    fun provideSpecialtyId(specialtyEditorDialog: SpecialtyEditorDialog): String? {
        return specialtyEditorDialog.requireArguments()
            .getString(SpecialtyEditorDialog.SPECIALTY_ID)
    }

    @Named(CourseFragment.COURSE_ID)
    @Provides
    fun provideCourseIdFromCourse(courseFragment: CourseFragment): String {
        return courseFragment.navArgs.courseId
    }

    @Named(CourseWorkEditorFragment.WORK_ID)
    @Provides
    fun provideTaskIdToTaskEditor(courseWorkEditorFragment: CourseWorkEditorFragment): String? {
        return courseWorkEditorFragment.requireArguments().getString(CourseWorkEditorFragment.WORK_ID)
    }

    @Named(CourseWorkEditorFragment.COURSE_ID)
    @Provides
    fun provideCourseIdToTaskEditor(courseWorkEditorFragment: CourseWorkEditorFragment): String {
        return courseWorkEditorFragment.requireArguments()
            .getString(CourseWorkEditorFragment.COURSE_ID)!!
    }

    @Named(CourseWorkEditorFragment.SECTION_ID)
    @Provides
    fun provideSectionId(courseWorkEditorFragment: CourseWorkEditorFragment): String? {
        return courseWorkEditorFragment.requireArguments().getString(CourseWorkEditorFragment.SECTION_ID)
    }

    @Named(ContentFragment.TASK_ID)
    @Provides
    fun provideTaskId(contentFragment: ContentFragment): String {
        return contentFragment.requireArguments().getString(ContentFragment.TASK_ID)!!
    }

    @Named(ContentFragment.COURSE_ID)
    @Provides
    fun provideCourseId(contentFragment: ContentFragment): String {
        return contentFragment.requireArguments()
            .getString(ContentFragment.COURSE_ID)!!
    }

    @Named(CourseWorkFragment.TASK_ID)
    @Provides
    fun provideTaskIdToTaskInfo(courseWorkFragment: CourseWorkFragment): String {
        return courseWorkFragment.requireParentFragment().requireArguments()
            .getString(ContentFragment.TASK_ID)!!
    }

    @Named(CourseWorkFragment.COURSE_ID)
    @Provides
    fun provideCourseIdToTaskInfo(courseWorkFragment: CourseWorkFragment): String {
        return courseWorkFragment.requireParentFragment().requireArguments()
            .getString(ContentFragment.COURSE_ID)!!
    }

    @Named(SubmissionsFragment.TASK_ID)
    @Provides
    fun provideTaskIdToSubmissions(submissionsFragment: SubmissionsFragment): String {
        return submissionsFragment.requireParentFragment().requireArguments()
            .getString(ContentFragment.TASK_ID)!!
    }

    @Named(SubmissionDialog.TASK_ID)
    @Provides
    fun provideTaskIdToSubmission(submissionDialog: SubmissionDialog): String {
        return submissionDialog
            .requireArguments()
            .getString(SubmissionDialog.TASK_ID)!!
    }

    @Named(SubmissionDialog.STUDENT_ID)
    @Provides
    fun provideStudentIdToSubmission(submissionDialog: SubmissionDialog): String {
        return submissionDialog.requireArguments().getString(SubmissionDialog.STUDENT_ID)!!
    }

    @Named(CourseTopicEditorFragment.COURSE_ID)
    @Provides
    fun provideCourseIdToCourseSectionEditor(courseTopicEditorFragment: CourseTopicEditorFragment): String {
        return courseTopicEditorFragment.requireArguments()
            .getString(CourseTopicEditorFragment.COURSE_ID)!!
    }
}