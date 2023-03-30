package com.denchic45.stuiversity.api.role.model

data class Capability(val resource: String) {

    override fun toString(): String = resource

    companion object {
        val ReadUserConfidentialData: Capability = Capability("user/confidential:read")
        val WriteUser: Capability = Capability("user:write")
        val DeleteUser: Capability = Capability("user:delete")

        val WriteAssignRoles: Capability = Capability("role/assignment:write")

        val ReadMembers: Capability = Capability("membership/members:read")
        val WriteMembers: Capability = Capability("membership/members:write")

        val WriteMembership: Capability = Capability("membership:write")

        val ReadStudyGroup: Capability = Capability("study_group:read")
        val WriteStudyGroup: Capability = Capability("study_group:write")
        val DeleteStudyGroup: Capability = Capability("study_group:delete")

        val WriteSpecialty: Capability = Capability("specialty:write")

        val BeStudent: Capability = Capability("be_student") // TODO: test capability for test

        val WriteTimetable: Capability = Capability("timetable:write")
        val WriteRoom: Capability = Capability("room:write")

        val ReadCourse: Capability = Capability("course:read")
        val WriteCourse: Capability = Capability("course:write")
        val DeleteCourse: Capability = Capability("course:delete")
        val WriteCourseTopics: Capability = Capability("course/topics:write")
        val WriteCourseStudyGroups: Capability = Capability("course/study_group:write")

        val ReadCourseElements: Capability = Capability("course/elements:read")
        val DeleteCourseElements: Capability = Capability("course/elements:delete")

        val WriteCourseElement: Capability = Capability("course/element:write")

        val ReadSubmissions: Capability = Capability("course/submissions:read")
        val SubmitSubmission: Capability = Capability("course/submissions:submit")
        val GradeSubmission: Capability = Capability("course/submissions:grade")

        val WriteSubject: Capability = Capability("subject:write")
        val ReadSubject: Capability = Capability("subject:read")
        val DeleteSubject: Capability = Capability("subject:delete")

        val WriteOtherAttachments: Capability = Capability("attachment/other:write")
    }
}