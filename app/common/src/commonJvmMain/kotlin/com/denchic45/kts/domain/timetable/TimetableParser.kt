package com.denchic45.kts.domain.timetable

import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.SubjectApi
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.model.*
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.DateTimePatterns
import com.denchic45.stuiversity.util.Dates
import com.denchic45.stuiversity.util.toLocalDate
import com.github.michaelbull.result.unwrap
import me.tatarka.inject.annotations.Inject
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

@Inject
class TimetableParser(
    private val studyGroupApi: StudyGroupApi,
    private val userApi: UserApi,
    private val courseApi: CoursesApi,
    private val subjectApi: SubjectApi
) {
    private val days = listOf(
        "ПОНЕДЕЛЬНИК ",
        "ВТОРНИК",
        "СРЕДА",
        "ЧЕТВЕРГ",
        "ПЯТНИЦА",
        "СУББОТА"
    )
    private var cellOfGroupPos = 0
    private lateinit var table: XWPFTable
    private var currentRow = 0
    private var currentDayOfWeek = 0
    private lateinit var currentStudyGroup: StudyGroupResponse

    private val cachedCourses = mutableMapOf<String, CourseResponse>()
    private val cachedCoursesByStudyGroup = mutableMapOf<Pair<String, UUID>, CourseResponse>()
    private val cachedSubjects = mutableListOf<SubjectResponse>()
    private val cachedUsers = mutableMapOf<String, UserResponse>()

    private suspend fun findUserBySurname(surname: String): UserResponse {
        return cachedUsers[surname] ?: userApi.getBySurname(surname).unwrap()
            .apply { cachedUsers[surname] = this }
    }

    suspend fun parseDoc(docFile: File): List<Pair<StudyGroupResponse, TimetableResponse>> {
        val timetables: MutableList<Pair<StudyGroupResponse, TimetableResponse>> = mutableListOf()
        try {
            val wordDoc = XWPFDocument(docFile.inputStream())
            table = wordDoc.tables[0]

            val cellsInGroups: MutableList<XWPFTableCell> = ArrayList()
            cellsInGroups.addAll(table.getRow(1).tableCells)
            cellsInGroups.addAll(table.getRow(2).tableCells)

            val studyGroupsByAcademicYear = studyGroupApi.getByAcademicYear(findCourseNumber())
                .unwrap()

            val cellsCount = table.getRow(1).tableCells.size

            for (studyGroup in studyGroupsByAcademicYear) {
                currentStudyGroup = studyGroup
                cellOfGroupPos = cellsInGroups.indexOf(
                    getCellByGroupName(cellsInGroups, studyGroup.name)
                )
                if (cellOfGroupPos == -1) continue
                cellOfGroupPos = if (cellOfGroupPos > cellsCount) cellOfGroupPos - cellsCount
                else cellOfGroupPos
                timetables.add(studyGroup to createTimetable())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw e
        }
        return timetables
    }

    private fun findCourseNumber(): Int {
        val text = table.getRow(0).getCell(0).text
        return text.replace(Regex("[^0-9]+"), "").toInt()
    }

    private fun getCellByGroupName(
        tableCells: List<XWPFTableCell>,
        groupName: String,
    ): XWPFTableCell? {
        return tableCells
            .find { cell: XWPFTableCell ->
                val text = cell.text
                text.resetFormatting().contains(groupName.resetFormatting())
            }
    }

    private suspend fun createTimetable(): TimetableResponse {
        val weekLessons = mutableListOf<List<PeriodResponse>>()
        currentRow = 3
        currentDayOfWeek = 0
        while (!table.getRow(currentRow).getCell(0).text.contains("ПОНЕДЕЛЬНИК")) currentRow++

        val weekOfYear = table.getRow(currentRow).getCell(0).text
            .toLocalDate(DateTimePatterns.DD_MM_yy)
            .format(DateTimeFormatter.ofPattern("YYYY_ww"))

        for (i in 0 until STUDY_DAY_COUNT) {
            val dateInCell = table.getRow(currentRow).getCell(0).text
            currentRow++
            val date = dateInCell.substring(dateInCell.lastIndexOf(" ") + 1)
            if (Dates.isValidDate(date, DateTimePatterns.DD_MM_yy)) {
                weekLessons.add(getPeriodsOfDay(date))
                currentDayOfWeek++
            } else {
                throw TimetableInvalidDateException("Некоректная дата: $dateInCell")
            }
        }
        return TimetableResponse(
            weekOfYear,
            weekLessons[0],
            weekLessons[1],
            weekLessons[2],
            weekLessons[3],
            weekLessons[4],
            weekLessons[5],
        )
    }

    private suspend fun getPeriodsOfDay(dateText: String): List<PeriodResponse> {
        val date = dateText.toLocalDate(DateTimePatterns.DD_MM_yy)
        val periods = mutableListOf<PeriodResponse>()
        if (currentRow == table.rows.size) return emptyList()
        var cells = table.getRow(currentRow).tableCells

        while (hasRowsOfCurrentDate()) {
            val orderText = cells[0].text
            val cellContent: String = cells[cellOfGroupPos].text
                .replace("\\(.*\\)".toRegex(), "")
                .trim { it <= ' ' }
            currentRow++
            if (currentRow < table.rows.size) {
                cells = table.getRow(currentRow).tableCells
            }
            if (orderText.isNotEmpty()) {
                if (orderText == "0" && cellContent.isEmpty()) {
                    continue
                }
                createPeriod(orderText.toInt(), date, cellContent)?.let { periods.add(it) }
            } else if (cellContent.isNotEmpty()) {
                throw TimetableOrderLessonException(
                    """
    У урока нет порядкового номера! 
    Дата: $dateText
    Поле урока: $cellContent
    Группа: ${currentStudyGroup.name}
    """.trimIndent()
                )
            }
        }
        return periods
    }

    private fun hasRowsOfCurrentDate(): Boolean {
        if (currentRow == table.rows.size - 1) return false
        return if (currentDayOfWeek == 5) true
        else !table.getRow(currentRow)
            .getCell(0).text.contains(days[currentDayOfWeek + 1])
    }

    private suspend fun createPeriod(
        order: Int,
        date: LocalDate,
        content: String
    ): PeriodResponse? {
        if (content.isEmpty()) {
            return null
        }
        val separatedContent = content.lines()
        val subjectName = separatedContent[0].substringBefore('\\')
        val roomName = separatedContent[0].substringAfter('\\')

        val subject = findSubjectByName(subjectName)
        val course = subject?.let { findCourseBySubjectAndStudyGroupId(it, currentStudyGroup.id) }
        return if (course != null) {
            LessonResponse(
                id = -1,
                date = date,
                order = order,
                room = null,
                studyGroup = StudyGroupName(currentStudyGroup.id, currentStudyGroup.name),
                members = findTeacherByContent(separatedContent),
                details = LessonDetails(course.id, subject)
            )
        } else {
            EventResponse(
                id = -1,
                date = date,
                order = order,
                room = null,
                studyGroup = StudyGroupName(currentStudyGroup.id, currentStudyGroup.name),
                members = findTeacherByContent(separatedContent),
                details = EventDetails(subjectName, "blue", "")
            )
        }
    }

    private suspend fun findTeacherByContent(separatedContent: List<String>): List<PeriodMember> {
        return if (separatedContent.size == 1) {
            emptyList()
        } else separatedContent.subList(1, separatedContent.size).map { line ->
            findUserBySurname(line.split(" ")[0]).let {
                PeriodMember(
                    it.id,
                    it.firstName,
                    it.surname,
                    it.avatarUrl
                )
            }
        }
    }

    private suspend fun findCourseBySubjectAndStudyGroupId(
        subject: SubjectResponse,
        studyGroupId: UUID
    ): CourseResponse? {
        val subjectName = subject.name

        // try to get cached course by group
        val cachedByGroup = cachedCoursesByStudyGroup[subjectName to studyGroupId]
        if (cachedByGroup != null) {
            return cachedByGroup
        }

        // try to get just cached course
        val cachedCourse = cachedCourses[subjectName]
        if (cachedCourse != null) {
            cachedCoursesByStudyGroup[subjectName to studyGroupId] = cachedCourse
            return cachedCourse
        }

        // get course by subject name and group id or subject name only
        val subjectResponse = subjectApi.getList(name = subjectName).unwrap().firstOrNull()
        val courseResponse = subjectResponse?.let {
            courseApi.getList(studyGroupId = studyGroupId, subjectId = subjectResponse.id)
                .unwrap().firstOrNull()
                ?: courseApi.getList(subjectId = subjectResponse.id)
                    .unwrap().first()
        }

        return courseResponse?.also {
            cachedCourses[subjectName] = it
            cachedCoursesByStudyGroup[subjectName to studyGroupId] = it
        }
    }

    private suspend fun findSubjectByName(subjectName: String): SubjectResponse? {
        return cachedSubjects.find { it.name.resetFormatting() == subjectName.resetFormatting() }
            ?: run {
                subjectApi.getList(name = subjectName).unwrap().firstOrNull()
            }?.also(cachedSubjects::add)
    }

//    private fun createEventDetails(eventName: String): EventDetails {
//        return when (eventName.lowercase(Locale.getDefault())) {
//            "обед" -> dinner()
//            "практика" -> practice()
//            else -> SimpleEventDetails(eventName)
//        }
//    }

//    private fun findTeachersBySubject(subject: SubjectResponse): List<UserResponse> {
//        return currentStudyGroup.courses
//            .filter { it.subject == subject }
//            .map { it.teacher }
//    }

    private fun String.resetFormatting(): String {
        return replace(" ", "")
            .replace("-", "")
            .replace("–", "")
            .lowercase(Locale.getDefault())
    }


    open class TimetableParserException(message: String?) : Exception(message)
    class TimetableInvalidDateException(message: String?) : TimetableParserException(message)
    class TimetableOrderLessonException(message: String?) : TimetableParserException(message)

    companion object {
        const val STUDY_DAY_COUNT = 6
    }
}