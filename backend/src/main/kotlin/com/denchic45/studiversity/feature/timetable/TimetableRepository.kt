package com.denchic45.studiversity.feature.timetable

import com.denchic45.studiversity.database.table.*
import com.denchic45.studiversity.util.toSqlSortOrder
import com.denchic45.stuiversity.api.timetable.model.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.between
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

class TimetableRepository {

    fun putPeriodsOfDay(studyGroupId: UUID, date: LocalDate, periods: List<PeriodRequest>) {
        removeByStudyGroupIdAndDate(studyGroupId, date)
        periods.forEach { period: PeriodRequest ->
            val periodDao = PeriodDao.new {
                this.date = date
                order = period.order
                room = period.roomId?.let { RoomDao.findById(it) }
                this.studyGroup = StudyGroupDao.findById(studyGroupId)!!
                type = period.type
            }
            val periodId = periodDao.id.value

            when (period) {
                is LessonRequest -> LessonDao.new(periodId) {
                    this.period = PeriodDao.findById(periodId)!!
                    this.course = CourseDao.findById(period.courseId)!!
                }

                is EventRequest -> EventDao.new(periodId) {
                    name = period.name
                    color = period.color
                    icon = period.iconUrl
                }
            }
            period.memberIds.forEach {
                PeriodMemberDao.new {
                    this.period = periodDao
                    this.member = UserDao.findById(it)!!
                }
            }
        }
    }

    fun findByDateRange(
        startDate: LocalDate,
        endDate: LocalDate,
        studyGroupId: List<UUID>? = null,
        memberIds: List<UUID>? = null,
        courseIds: List<UUID>? = null,
        roomIds: List<UUID>? = null,
        sorting: List<PeriodsSorting>? = null
    ): TimetableResponse {
        val query = getPeriodsQuery(
            startDate = startDate,
            endDate = endDate,
            studyGroupId = studyGroupId,
            courseIds = courseIds,
            memberIds = memberIds,
            roomIds = roomIds,
            sorting = sorting
        )

        return PeriodDao.wrapRows(query)
            .orderBy(Periods.date to SortOrder.ASC, Periods.order to SortOrder.ASC)
            .map(PeriodDao::toResponse)
            .groupBy { it.date.dayOfWeek }
            .withDefault { emptyList() }
            .let { map ->
                TimetableResponse(
                    weekOfYear = startDate.format(DateTimeFormatter.ofPattern("YYYY_ww")),
                    monday = map.getValue(DayOfWeek.MONDAY),
                    tuesday = map.getValue(DayOfWeek.TUESDAY),
                    wednesday = map.getValue(DayOfWeek.WEDNESDAY),
                    thursday = map.getValue(DayOfWeek.THURSDAY),
                    friday = map.getValue(DayOfWeek.FRIDAY),
                    saturday = map.getValue(DayOfWeek.SATURDAY),
                )
            }
    }

    fun findByDate(
        date: LocalDate,
        studyGroupId: List<UUID>? = null,
        memberIds: List<UUID>? = null,
        courseIds: List<UUID>? = null,
        roomIds: List<UUID>? = null,
        sorting: List<PeriodsSorting>? = null
    ): TimetableOfDayResponse {
        val query = getPeriodsQuery(
            startDate = date,
            endDate = null,
            studyGroupId = studyGroupId,
            courseIds = courseIds,
            memberIds = memberIds,
            roomIds = roomIds,
            sorting = sorting
        )
        return PeriodDao.wrapRows(query)
            .orderBy(Periods.date to SortOrder.ASC, Periods.order to SortOrder.ASC)
            .map(PeriodDao::toResponse)
            .let(::TimetableOfDayResponse)
    }

    private fun getPeriodsQuery(
        startDate: LocalDate,
        endDate: LocalDate?,
        studyGroupId: List<UUID>?,
        courseIds: List<UUID>?,
        memberIds: List<UUID>?,
        roomIds: List<UUID>?,
        sorting: List<PeriodsSorting>?
    ): Query {
        val query = Periods.leftJoin(Lessons, { Periods.id }, { id })
            .leftJoin(Events, { Periods.id }, { id })
            .run {
                endDate?.let {
                    select(Periods.date.between(startDate, endDate))
                } ?: select(Periods.date eq startDate)
            }

        studyGroupId?.let { query.andWhere { Periods.studyGroupId inList it } }

        courseIds?.let { query.andWhere { Lessons.courseId inList courseIds } }

        memberIds?.let {
            query.adjustColumnSet { innerJoin(PeriodsMembers, { Periods.id }, { periodId }) }
                .andWhere { PeriodsMembers.memberId inList memberIds }
        }

        roomIds?.let { query.andWhere { Periods.roomId inList roomIds } }

        sorting?.forEach {
            query.orderBy(
                column = when (it) {
                    is PeriodsSorting.Course -> {
                        query.adjustColumnSet {
                            innerJoin(
                                Courses,
                                { Lessons.courseId },
                                { id })
                        }
                            .adjustSelect { select(this.fields + Courses.name) }
                        Courses.name
                    }

                    is PeriodsSorting.Member -> {
                        query.adjustColumnSet {
                            innerJoin(Users, { PeriodsMembers.memberId }, { id })
                        }.adjustSelect { select(fields + Users.surname) }
                        Users.surname
                    }

                    is PeriodsSorting.Order -> Periods.order
                    is PeriodsSorting.Room -> {
                        query.adjustColumnSet { innerJoin(Rooms, { Periods.roomId }, { id }) }
                            .adjustSelect { select(this.fields + Rooms.name) }
                        Rooms.name
                    }

                    is PeriodsSorting.StudyGroup -> {
                        query.adjustColumnSet {
                            innerJoin(
                                StudyGroups,
                                { Periods.studyGroupId },
                                { id })
                        }
                            .adjustSlice { slice(fields + StudyGroups.name) }
                        StudyGroups.name
                    }
                },
                order = it.order.toSqlSortOrder()
            )
        }
        return query
    }

    fun removeByStudyGroupIdAndDate(studyGroupId: UUID, date: LocalDate) {
        Periods.deleteWhere { Periods.studyGroupId eq studyGroupId and (Periods.date eq date) }
    }

    fun removeByStudyGroupIdAndDateRange(
        studyGroupId: UUID,
        startDate: LocalDate,
        endDate: LocalDate
    ) {
        Periods.deleteWhere {
            Periods.studyGroupId eq studyGroupId and (date.between(
                startDate,
                endDate
            ))
        }
    }
}