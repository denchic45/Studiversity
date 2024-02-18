package com.denchic45.stuiversity.api.role.model

data class Capability(val resource: String) {

    override fun toString(): String = resource

    companion object {
        val WriteUser: Capability = Capability("user:write")
        val DeleteUser: Capability = Capability("user:delete")
        val WriteUserConfidentialData: Capability = Capability("user/confidential:write")
        val ReadUserConfidentialData: Capability = Capability("user/confidential:read")
        val UpdateProfile: Capability = Capability("user/profile:write")

//        val WriteMembers: Capability = Capability("membership/members:write")
        val WriteAssignRoles: Capability = Capability("role/assignment:write")
        val WriteRoom: Capability = Capability("room:write")
        val WriteSpecialty: Capability = Capability("specialty:write")
        val WriteSubject: Capability = Capability("subject:write")
        val WriteTimetable: Capability = Capability("timestable:write")

        val ReadOtherStudyGroup: Capability = Capability("study_group:read")
        val WriteStudyGroup: Capability = Capability("study_group:write")
        val DeleteStudyGroup: Capability = Capability("study_group:delete")

        val WriteCourse: Capability = Capability("course:write")
        val ArchiveCourse: Capability = Capability("course:archive")
        val DeleteCourse: Capability = Capability("course:delete")
        val WriteCourseTopics: Capability = Capability("course/topics:write")
        val WriteCourseStudyGroups: Capability = Capability("course/study_group:write")

        val ReadCourseElements: Capability = Capability("course/elements:read")
        val WriteCourseElements: Capability = Capability("course/elements:write")
        val DeleteCourseElements: Capability = Capability("course/elements:delete")

        val ReadSubmissions: Capability = Capability("course/submissions:read")
        val GradeSubmission: Capability = Capability("course/submissions:grade")
        val SubmitSubmission: Capability = Capability("course/submissions:submit")

//        val ReadSubject: Capability = Capability("subject:read")
//        val WriteOtherAttachments: Capability = Capability("attachment/other:write")

        val BeStudent: Capability = Capability("be_student") // TODO: test capability for test
    }
}