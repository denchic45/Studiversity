package com.denchic45.kts.domain.timetable

import com.denchic45.kts.domain.model.*
import com.denchic45.kts.domain.model.Event.Companion.createEmpty
import com.denchic45.kts.domain.model.SimpleEventDetails.Companion.dinner
import com.denchic45.kts.domain.model.SimpleEventDetails.Companion.practice
import com.denchic45.kts.util.UUIDS
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.timetable.model.TimetableResponse
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.DatePatterns
import com.denchic45.stuiversity.util.Dates
import com.denchic45.stuiversity.util.toLocalDate
import org.apache.poi.xwpf.usermodel.XWPFDocument
import org.apache.poi.xwpf.usermodel.XWPFTable
import org.apache.poi.xwpf.usermodel.XWPFTableCell
import java.io.File
import java.time.LocalDate
import java.util.*

class TimetableParser(
    private val studyGroupApi: StudyGroupApi,
    private val userApi: UserApi,
    private val courseApi: UserApi
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
    private lateinit var currentGroupCourse: GroupCourses

    private val cachedCourses = mutableMapOf<String, CourseResponse>()
    private val cachedUsers = mutableMapOf<String, UserResponse>()

    private fun findUserBySurname(surname: String) {
        return cachedCourses.get(surname) ?:
    }

    suspend fun parseDoc(
        docFile: File,
//        callbackGroupInfo: suspend (Int) -> List<GroupCourses>,
    ): Map<UUID, TimetableResponse> {

        val timetables: MutableMap<UUID,TimetableResponse> = mutableMapOf()

        try {
            val wordDoc = XWPFDocument(docFile.inputStream())
            table = wordDoc.tables[0]

            val cellsInGroups: MutableList<XWPFTableCell> = ArrayList()
            cellsInGroups.addAll(table.getRow(1).tableCells)
            cellsInGroups.addAll(table.getRow(2).tableCells)

            val studyGroupsByAcademicYear = studyGroupApi.getByAcademicYear()

            val cellsCount = table.getRow(1).tableCells.size

            for (groupCoursesItem in studyGroupsByAcademicYear) {
                currentGroupCourse = groupCoursesItem
                cellOfGroupPos = cellsInGroups.indexOf(
                    getCellByGroupName(
                        cellsInGroups,
                        groupCoursesItem.groupHeader.name
                    )
                )
                if (cellOfGroupPos == -1) continue
                cellOfGroupPos =
                    if (cellOfGroupPos > cellsCount) cellOfGroupPos - cellsCount else cellOfGroupPos
                timetables.put(
                    gr
                    GroupTimetable(
                        groupCoursesItem.groupHeader,
                        getLessonsOfGroup()
                    )
                )
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

    private fun getLessonsOfGroup(): List<EventsOfDay> {
        val weekLessons: MutableList<EventsOfDay> = ArrayList()
        currentRow = 3
        currentDayOfWeek = 0
        while (!table.getRow(currentRow).getCell(0).text.contains("ПОНЕДЕЛЬНИК")) currentRow++
        for (i in 0 until STUDY_DAY_COUNT) {
            val dateInCell = table.getRow(currentRow).getCell(0).text
            currentRow++
            val date = dateInCell.substring(dateInCell.lastIndexOf(" ") + 1)
            if (Dates.isValidDate(date, DatePatterns.DD_MM_yy)) {
                weekLessons.add(getEventsOfTheDay(date))
                currentDayOfWeek++
            } else {
                throw  TimetableInvalidDateException("Некоректная дата: $dateInCell")
            }
        }
        return weekLessons
    }

    private fun getEventsOfTheDay(dateText: String): EventsOfDay {
        val date = dateText.toLocalDate(DatePatterns.DD_MM_yy)
        val events: MutableList<Event> = mutableListOf()
        if (currentRow == table.rows.size) return EventsOfDay.createEmpty(date)
        var cells = table.getRow(currentRow).tableCells

        val startAtZero = cells[0].text == "0" && cells[cellOfGroupPos].text.isNotEmpty()

        while (hasRowsOfCurrentDate()) {
            val orderText = cells[0].text
            val subjectAndRoomText: String = cells[cellOfGroupPos].text
                .replace("\\(.*\\)".toRegex(), "")
                .trim { it <= ' ' }
            currentRow++
            if (currentRow < table.rows.size) {
                cells = table.getRow(currentRow).tableCells
            }
            if (orderText.isNotEmpty()) {
                if (orderText == "0" && subjectAndRoomText.isEmpty()) {
                    continue
                }
                events.add(createPeriod(orderText.toInt(), subjectAndRoomText, date))
            } else if (subjectAndRoomText.isNotEmpty()) {
                throw TimetableOrderLessonException(
                    """
    У урока нет порядкового номера! 
    Дата: $dateText
    Поле урока: $subjectAndRoomText
    Группа: ${currentGroupCourse.groupHeader.name}
    """.trimIndent()
                )
            }
        }

        return EventsOfDay(date, events, startAtZero, UUIDS.createShort())
    }

    private fun hasRowsOfCurrentDate(): Boolean {
        if (currentRow == table.rows.size - 1) return false
        return if (currentDayOfWeek == 5) true
        else !table.getRow(currentRow)
            .getCell(0).text.contains(
                days[currentDayOfWeek + 1]
            )
    }

    private fun createPeriod(order: Int, subjectAndRoom: String, date: LocalDate): Event {
        var subjectAndRoom = subjectAndRoom
        if (subjectAndRoom.isEmpty()) {
            return createEmpty(
                groupHeader = currentGroupCourse.groupHeader,
//                order = order
            )
        }
        val separatorSubjectRoomPos = subjectAndRoom.indexOf('\\')
        val eventName: String
        val room: String
        subjectAndRoom = subjectAndRoom.replace("\\(.*\\)".toRegex(), "")
        if (separatorSubjectRoomPos != -1) {
            eventName = subjectAndRoom.substring(0, separatorSubjectRoomPos)
            room = subjectAndRoom.substring(separatorSubjectRoomPos + 1)
        } else {
            eventName = subjectAndRoom
            room = ""
        }
        val subject = findSubjectByName(eventName)
        return if (subject != null) {
            Event(
                groupHeader = currentGroupCourse.groupHeader,
                room = room,
                details = Lesson(subject, teachers = findTeachersBySubject(subject))
            )
        } else {
            Event(
                groupHeader = currentGroupCourse.groupHeader,
                room = room,
                details = createEventDetails(eventName)
            )
        }
    }

    private fun findSubjectByName(subjectName: String): SubjectResponse? {
        return currentGroupCourse.courses
            .firstOrNull { course ->
                course.subject.name.resetFormatting().contains(
                    subjectName.resetFormatting()
                )
            }?.subject
    }

    private fun createEventDetails(eventName: String): EventDetails {
        return when (eventName.lowercase(Locale.getDefault())) {
            "обед" -> dinner()
            "практика" -> practice()
            else -> SimpleEventDetails(eventName)
        }
    }

    private fun findTeachersBySubject(subject: SubjectResponse): List<UserResponse> {
        return currentGroupCourse.courses
            .filter { it.subject == subject }
            .map { it.teacher }
    }

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