package com.denchic45.studiversity.feature.course.repository

import com.denchic45.studiversity.database.exists
import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.feature.course.toResponse
import com.denchic45.studiversity.feature.studygroup.mapper.toResponse
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import io.github.jan.supabase.storage.BucketApi
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.Instant
import java.util.*

class CourseRepository(private val bucket: BucketApi) {

    fun add(request: CreateCourseRequest): CourseResponse {
        val dao = CourseDao.new {
            name = request.name
            request.subjectId?.apply {
                subject = SubjectDao.findById(this)
            }
        }

        Enrollments.insert {
            it[courseId] = dao.id
            it[type] = EnrollmentType.DEFAULT
        }
        return dao.toResponse()
    }

    fun findById(id: UUID): CourseResponse? {
        return CourseDao.findById(id)?.toResponse()
    }

    fun find(
        memberId: UUID?,
        studyGroupId: UUID?,
        subjectId: UUID?,
        archived: Boolean?,
        q: String?
    ): List<CourseResponse> {
        val query = Courses.selectAll()

        memberId?.let {
            query.adjustColumnSet {
                innerJoin(Enrollments, { Courses.id }, { courseId })
                    .innerJoin(UserEnrollments, { Enrollments.id }, { enrollmentId })
            }.andWhere { UserEnrollments.userId eq memberId }
        }
        studyGroupId?.let {
            query.adjustColumnSet {
                innerJoin(CoursesStudyGroups, { Courses.id }, { courseId })
            }.adjustWhere {
                CoursesStudyGroups.studyGroupId eq it
            }
        }
        subjectId?.let {
            query.andWhere { Courses.subjectId eq subjectId }
        }
        archived?.let {
            query.andWhere { Courses.archived eq archived }
        }
        q?.let {
            query.adjustColumnSet { leftJoin(Subjects, { Courses.subjectId }, { Subjects.id }) }
                .adjustSlice { slice(fields + Subjects.id) }
                .andWhere {
                    Courses.name.lowerCase() like "%$q%" or
                            (Subjects.name.lowerCase() like "%$q%") or
                            (Subjects.shortname.lowerCase() like "%$q%")
                }
        }
        return CourseDao.wrapRows(query).map(CourseDao::toResponse)
    }

    fun update(id: UUID, request: UpdateCourseRequest): CourseResponse? = CourseDao.findById(id)?.apply {
        updatedAt = Instant.now()
        request.name.ifPresent { name = it }
        request.subjectId.ifPresent { subject = it?.let { SubjectDao.findById(it) } }
    }?.toResponse()


    fun exist(id: UUID): Boolean {
        return Courses.exists { Courses.id eq id }
    }

    fun existStudyGroupByCourse(courseId: UUID, studyGroupId: UUID): Boolean {
        return CoursesStudyGroups.exists {
            CoursesStudyGroups.courseId eq courseId and
                    (CoursesStudyGroups.studyGroupId eq studyGroupId)
        }
//        return ExternalStudyGroupsMemberships
//            .innerJoin(Memberships, { membershipId }, { Memberships.id })
//            .slice(ExternalStudyGroupsMemberships.studyGroupId)
//            .exists { Memberships.scopeId eq courseId and (ExternalStudyGroupsMemberships.studyGroupId eq studyGroupId) }
    }


    fun findStudyGroupsByCourse(courseId: UUID): List<StudyGroupResponse> {
        return StudyGroupDao.wrapRows(
//            ExternalStudyGroupsMemberships
//                .innerJoin(Memberships, { membershipId }, { Memberships.id })
//                .innerJoin(StudyGroups, { ExternalStudyGroupsMemberships.studyGroupId }, { StudyGroups.id })
//                .select(Memberships.scopeId eq courseId)
            CoursesStudyGroups.innerJoin(StudyGroups, { studyGroupId }, { StudyGroups.id })
                .select(CoursesStudyGroups.courseId eq courseId)
        ).map(StudyGroupDao::toResponse)
    }

    // todo создавать enrollment для групп, если прикреплена только первая гурппа
    fun addStudyGroupToCourse(courseId: UUID, studyGroupId: UUID) {
        if (!Enrollments.exists {
                Enrollments.courseId eq courseId and
                        (Enrollments.type eq EnrollmentType.STUDY_GROUPS)
            }) {
            EnrollmentDao.new {
                this.course = CourseDao[courseId]
                this.type = EnrollmentType.STUDY_GROUPS
            }
        }


        CoursesStudyGroups.insert {
            it[CoursesStudyGroups.courseId] = courseId
            it[CoursesStudyGroups.studyGroupId] = studyGroupId
        }


//        val membershipId = Memberships
//            .select(Memberships.scopeId eq courseId and (Memberships.type eq "by_group"))
//            .firstOrNull()?.get(Memberships.id)
//            ?: Memberships.insertIgnore {
//                it[scopeId] = courseId
//                it[active] = true
//                it[type] = "by_group"
//            }[Memberships.id]
//        ExternalStudyGroupsMemberships.insertIgnore {
//            it[ExternalStudyGroupsMemberships.membershipId] = membershipId
//            it[ExternalStudyGroupsMemberships.studyGroupId] = studyGroupId
//        }
    }

    // todo удалять enrollment для групп, если откреплена последняя оставшаяся гурппа
    fun removeStudyGroupFromCourse(courseId: UUID, studyGroupId: UUID): Boolean {
        val studyGroupDetached = CoursesStudyGroups.deleteWhere {
            CoursesStudyGroups.courseId eq courseId and
                    (CoursesStudyGroups.studyGroupId eq studyGroupId)
        } > 0

//        val membershipOfCourseByGroupId = Memberships.slice(Memberships.id).select(
//            Memberships.scopeId eq courseId and (Memberships.type eq "by_group")
//        ).first()[Memberships.id]

//        val courseStudyGroupDeleted = ExternalStudyGroupsMemberships.deleteWhere {
//            ExternalStudyGroupsMemberships.studyGroupId eq studyGroupId and
//                    (membershipId eq membershipOfCourseByGroupId)
//        } > 0

        // Remove course enrollment for groups if no groups remain
        if (!CoursesStudyGroups.exists { CoursesStudyGroups.courseId eq courseId })
            Enrollments.deleteWhere {
                Enrollments.courseId eq courseId and (type eq EnrollmentType.STUDY_GROUPS)
            }
//        if (!ExternalStudyGroupsMemberships.exists {
//                ExternalStudyGroupsMemberships.membershipId eq membershipOfCourseByGroupId
//            })
//            Memberships.deleteWhere { Memberships.id eq membershipOfCourseByGroupId }
//        return courseStudyGroupDeleted

        return studyGroupDetached
    }

    fun addArchivedCourse(courseId: UUID) {
        CourseDao.findById(courseId)!!.archived = true
    }

    fun removeArchivedCourse(courseId: UUID) {
        CourseDao.findById(courseId)!!.archived = false
    }

    fun isArchivedCourse(courseId: UUID): Boolean {
        return CourseDao.findById(courseId)!!.archived
    }

    suspend fun removeCourse(courseId: UUID) {
        CourseDao.findById(courseId)!!.delete()
        // todo использовать свое хранилище
//        bucket.deleteRecursive("courses/$courseId")
    }
}