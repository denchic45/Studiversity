package com.studiversity.client.timetable

import com.github.michaelbull.result.unwrap
import com.studiversity.KtorClientTest
import com.studiversity.util.UNKNOWN_LONG_ID
import com.studiversity.util.assertResultIsOk
import com.studiversity.util.toUUID
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.room.RoomApi
import com.denchic45.stuiversity.api.room.model.CreateRoomRequest
import com.denchic45.stuiversity.api.room.model.RoomResponse
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.studygroup.model.AcademicYear
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.timetable.TimetableApi
import com.denchic45.stuiversity.api.timetable.model.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.koin.core.parameter.parametersOf
import org.koin.test.inject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TimetableTest : KtorClientTest() {

    private val studyGroupApi: StudyGroupApi by inject { parametersOf(client) }
    private val courseApi: CoursesApi by inject { parametersOf(client) }
    private val timetableApi: TimetableApi by inject { parametersOf(client) }
    private val roomApi: RoomApi by inject { parametersOf(client) }

    private val weekOfYear = LocalDate.of(2023, 2, 1).format(DateTimeFormatter.ofPattern("yyyy_ww"))

    private val teacher1Id = "02f00b3e-3a78-4431-87d4-34128ebbb04c".toUUID()
    private val teacher2Id = "96c42f53-2994-4845-9677-40bea853e56b".toUUID()

    private lateinit var studyGroup1: StudyGroupResponse
    private lateinit var studyGroup2: StudyGroupResponse
    private lateinit var mathCourse: CourseResponse
    private lateinit var engCourse: CourseResponse
    private lateinit var physicsCourse: CourseResponse
    private lateinit var room5: RoomResponse
    private lateinit var room10: RoomResponse
    private lateinit var room15: RoomResponse
    private lateinit var roomWorkshop: RoomResponse

    private fun timetableForStudyGroup1() = PutTimetableRequest(
        studyGroupId = studyGroup1.id,
        monday = listOf(
            LessonRequest(1, room5.id, listOf(teacher1Id), LessonDetails(mathCourse.id)),
            LessonRequest(2, room5.id, listOf(teacher1Id), LessonDetails(mathCourse.id)),
            LessonRequest(3, room10.id, listOf(teacher2Id), LessonDetails(engCourse.id))
        ),
        tuesday = listOf(
            LessonRequest(0, room5.id, listOf(teacher1Id), LessonDetails(mathCourse.id)),
            LessonRequest(1, room5.id, listOf(teacher1Id), LessonDetails(mathCourse.id)),
            LessonRequest(2, roomWorkshop.id, listOf(teacher2Id), LessonDetails(physicsCourse.id)),
            LessonRequest(3, room10.id, listOf(teacher2Id), LessonDetails(engCourse.id))
        ),
        wednesday = emptyList(),
        thursday = emptyList(),
        friday = emptyList()
    )

    private fun timetableForStudyGroup2(): PutTimetableRequest = PutTimetableRequest(
        studyGroupId = studyGroup2.id,
        monday = listOf(
            LessonRequest(1, room5.id, listOf(teacher2Id), LessonDetails(mathCourse.id)),
            LessonRequest(2, room10.id, listOf(teacher2Id), LessonDetails(engCourse.id)),
            LessonRequest(3, room5.id, listOf(teacher1Id), LessonDetails(mathCourse.id))
        ),
        tuesday = listOf(
            LessonRequest(0, room5.id, listOf(teacher2Id), LessonDetails(mathCourse.id)),
            LessonRequest(1, room5.id, listOf(teacher2Id), LessonDetails(mathCourse.id)),
            LessonRequest(2, room5.id, listOf(teacher1Id), LessonDetails(mathCourse.id)),
            LessonRequest(3, room10.id, listOf(teacher1Id), LessonDetails(engCourse.id))
        ),
        wednesday = emptyList(),
        thursday = emptyList(),
        friday = emptyList()
    )

    @BeforeEach
    fun init(): Unit = runBlocking {
        studyGroup1 = studyGroupApi.create(
            CreateStudyGroupRequest("Test group PKS 4.1", AcademicYear(2019, 2023), null, null)
        ).also(::assertResultIsOk).unwrap()

        studyGroup2 = studyGroupApi.create(
            CreateStudyGroupRequest("Test group PKS 4.2", AcademicYear(2023, 2027), null, null)
        ).also(::assertResultIsOk).unwrap()

        mathCourse = courseApi.create(CreateCourseRequest("Math")).also(::assertResultIsOk).unwrap()
        engCourse = courseApi.create(CreateCourseRequest("English")).also(::assertResultIsOk).unwrap()
        physicsCourse = courseApi.create(CreateCourseRequest("Physics")).also(::assertResultIsOk).unwrap()

        room5 = roomApi.create(CreateRoomRequest("Room 5")).also(::assertResultIsOk).unwrap()
        room10 = roomApi.create(CreateRoomRequest("Room 10")).also(::assertResultIsOk).unwrap()
        room15 = roomApi.create(CreateRoomRequest("Room 15")).also(::assertResultIsOk).unwrap()
        roomWorkshop = roomApi.create(CreateRoomRequest("Workshop")).also(::assertResultIsOk).unwrap()
    }

    @AfterEach
    fun tearDown(): Unit = runBlocking {
        studyGroupApi.delete(studyGroup1.id).also(::assertResultIsOk)
        studyGroupApi.delete(studyGroup2.id).also(::assertResultIsOk)

        courseApi.setArchive(mathCourse.id).also(::assertResultIsOk)
        courseApi.setArchive(engCourse.id).also(::assertResultIsOk)
        courseApi.setArchive(physicsCourse.id).also(::assertResultIsOk)

        courseApi.delete(mathCourse.id).also(::assertResultIsOk)
        courseApi.delete(engCourse.id).also(::assertResultIsOk)
        courseApi.delete(physicsCourse.id).also(::assertResultIsOk)

        roomApi.delete(room5.id).also(::assertResultIsOk)
        roomApi.delete(room10.id).also(::assertResultIsOk)
        roomApi.delete(room15.id).also(::assertResultIsOk)
        roomApi.delete(roomWorkshop.id).also(::assertResultIsOk)
    }

    @Test
    fun testPutAndDeleteTimetable(): Unit = runBlocking {
        val request = timetableForStudyGroup1()
        val response = putTimetable(request)

        assertIterableEquals(
            request.toFlatPeriods().map(PeriodRequest::details),
            response.toFlatPeriods().map(PeriodResponse::details)
        )

        timetableApi.deleteTimetable(weekOfYear, studyGroup1.id).also(::assertResultIsOk)
        assertTrue(timetableApi.getTimetable(weekOfYear, listOf(studyGroup1.id)).unwrap().toFlatPeriods().isEmpty())
    }

    private suspend fun putTimetable(request: PutTimetableRequest): TimetableResponse {
        return timetableApi.putTimetable(weekOfYear, request).also(::assertResultIsOk).unwrap()
    }

    @Test
    fun testGetTimetableByStudyGroup(): Unit = runBlocking {
        val request = timetableForStudyGroup1()
        putTimetable(request)

        val response = timetableApi.getTimetableByStudyGroupId(weekOfYear, studyGroup1.id)
            .also(::assertResultIsOk).unwrap()

        assertIterableEquals(
            request.toFlatPeriods().map(PeriodRequest::details),
            response.toFlatPeriods().map(PeriodResponse::details)
        )
    }

    @Test
    fun testGetTimetableByMember(): Unit = runBlocking {
        val request1 = timetableForStudyGroup1()
        putTimetable(request1)
        val request2 = timetableForStudyGroup2()
        putTimetable(request2)

        val response = timetableApi.getTimetable(weekOfYear, memberIds = listOf(teacher1Id))
            .also(::assertResultIsOk).unwrap()

        val expectedTimetableResponse = TimetableResponse(
            monday = listOf(
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    1,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    2,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    3,
                    room5.id,
                    studyGroup2.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                )
            ),
            tuesday = listOf(
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    0,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    1,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    2,
                    room5.id,
                    studyGroup2.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    3,
                    room10.id,
                    studyGroup2.id,
                    listOf(teacher1Id),
                    LessonDetails(engCourse.id)
                )
            ),
            wednesday = emptyList(),
            thursday = emptyList(),
            friday = emptyList(),
            saturday = listOf()
        )

        expectedTimetableResponse.toFlatPeriods().zip(response.toFlatPeriods())
            .forEach { (expected, actual) ->
                assertEquals(expected.date, actual.date)
                assertEquals(expected.order, actual.order)
                assertEquals(expected.studyGroupId, actual.studyGroupId)
                assertEquals(expected.details, actual.details) { expected.toString() + actual.toString() }
                assertEquals(expected.roomId, actual.roomId)
            }
    }

    @Test
    fun testGetTimetableByCourseId(): Unit = runBlocking {
        val request1 = timetableForStudyGroup1()
        putTimetable(request1)
        val request2 = timetableForStudyGroup2()
        putTimetable(request2)

        val response = timetableApi.getTimetable(
            weekOfYear = weekOfYear,
            courseIds = listOf(mathCourse.id),
            sorting = listOf(SortingPeriods.StudyGroup())
        ).also(::assertResultIsOk).unwrap()

        val expectedTimetableResponse = TimetableResponse(
            monday = listOf(
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    1,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    2,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    1,
                    room5.id,
                    studyGroup2.id,
                    listOf(teacher2Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    3,
                    room5.id,
                    studyGroup2.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                )
            ),
            tuesday = listOf(
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    0,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher2Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    1,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher2Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    0,
                    room5.id,
                    studyGroup2.id,
                    listOf(teacher2Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    1,
                    room5.id,
                    studyGroup2.id,
                    listOf(teacher2Id),
                    LessonDetails(mathCourse.id)
                ),

                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    2,
                    room5.id,
                    studyGroup2.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
            ),
            wednesday = emptyList(),
            thursday = emptyList(),
            friday = emptyList(),
            saturday = emptyList()
        )

        expectedTimetableResponse.toFlatPeriods().zip(response.toFlatPeriods())
            .forEach { (expected, actual) ->
                assertEquals(expected.studyGroupId, actual.studyGroupId)
                assertEquals(expected.date, actual.date)
                assertEquals(expected.details, actual.details) { expected.toString() + actual.toString() }
                assertEquals(expected.order, actual.order)
                assertEquals(expected.roomId, actual.roomId)
            }
    }

    @Test
    fun testGetTimetableByRoomWithSortingByOrderAndStudyGroup(): Unit = runBlocking {
        val request1 = timetableForStudyGroup1()
        putTimetable(request1)
        val request2 = timetableForStudyGroup2()
        putTimetable(request2)

        val expectedResponse = TimetableResponse(
            monday = listOf(
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    2,
                    room10.id,
                    studyGroup2.id,
                    listOf(teacher2Id),
                    LessonDetails(engCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 30),
                    3,
                    room10.id,
                    studyGroup1.id,
                    listOf(teacher2Id),
                    LessonDetails(engCourse.id)
                ),
            ),
            tuesday = listOf(
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    3,
                    room10.id,
                    studyGroup1.id,
                    listOf(teacher2Id),
                    LessonDetails(engCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    3,
                    room10.id,
                    studyGroup2.id,
                    listOf(teacher1Id),
                    LessonDetails(engCourse.id)
                )
            ),
            wednesday = listOf(),
            thursday = listOf(),
            friday = listOf(),
            saturday = listOf()
        )

        val response = timetableApi.getTimetable(
            weekOfYear,
            roomIds = listOf(room10.id),
            sorting = listOf(SortingPeriods.Order(), SortingPeriods.StudyGroup())
        ).also(::assertResultIsOk).unwrap()

        expectedResponse.toFlatPeriods().zip(response.toFlatPeriods())
            .forEach { (expected, actual) ->
                assertEquals(expected.studyGroupId, actual.studyGroupId)
                assertEquals(expected.date, actual.date)
                assertEquals(expected.details, actual.details) { expected.toString() + actual.toString() }
                assertEquals(expected.order, actual.order)
                assertEquals(expected.roomId, actual.roomId)
            }
    }

    @Test
    fun testGetTimetableOfDateByStudyGroup(): Unit = runBlocking {
        val request = timetableForStudyGroup1()
        putTimetable(request)

        val expectedResponse = TimetableOfDayResponse(
            periods = listOf(
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    0,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    1,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    2,
                    roomWorkshop.id,
                    studyGroup1.id,
                    listOf(teacher2Id),
                    LessonDetails(physicsCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 1, 31),
                    3,
                    room10.id,
                    studyGroup1.id,
                    listOf(teacher2Id),
                    LessonDetails(engCourse.id)
                )
            )
        )

        val response = timetableApi.getTimetableOfDayByStudyGroupId(weekOfYear, 2, studyGroup1.id)
            .also(::assertResultIsOk).unwrap()

        expectedResponse.periods.zip(response.periods).forEach { (expected, actual) ->
            assertEquals(expected.studyGroupId, actual.studyGroupId)
            assertEquals(expected.date, actual.date)
            assertEquals(expected.details, actual.details)
            assertEquals(expected.order, actual.order)
            assertEquals(expected.roomId, actual.roomId)
        }
    }

    @Test
    fun testPutAndDeleteTimetableOfDay(): Unit = runBlocking {
        val expectedResponse = TimetableOfDayResponse(
            listOf(
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 2, 1),
                    1,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 2, 1),
                    2,
                    roomWorkshop.id,
                    studyGroup1.id,
                    listOf(teacher2Id),
                    LessonDetails(physicsCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 2, 1),
                    3,
                    roomWorkshop.id,
                    studyGroup1.id,
                    listOf(teacher2Id),
                    LessonDetails(physicsCourse.id)
                ),
                LessonResponse(
                    UNKNOWN_LONG_ID,
                    LocalDate.of(2023, 2, 1),
                    4,
                    room5.id,
                    studyGroup1.id,
                    listOf(teacher1Id),
                    LessonDetails(mathCourse.id)
                ),
            )
        )
        val response: TimetableOfDayResponse = timetableApi.putTimetableOfDay(
            weekOfYear = weekOfYear,
            dayOfWeek = 3,
            putTimetableOfDayRequest = PutTimetableOfDayRequest(
                studyGroup1.id, listOf(
                    LessonRequest(1, room5.id, listOf(teacher1Id), LessonDetails(mathCourse.id)),
                    LessonRequest(2, roomWorkshop.id, listOf(teacher2Id), LessonDetails(physicsCourse.id)),
                    LessonRequest(3, roomWorkshop.id, listOf(teacher2Id), LessonDetails(physicsCourse.id)),
                    LessonRequest(4, room5.id, listOf(teacher1Id), LessonDetails(mathCourse.id)),
                )
            )
        ).also(::assertResultIsOk).unwrap()
        expectedResponse.periods.zip(response.periods).forEach { (expected, actual) ->
            assertEquals(expected.studyGroupId, actual.studyGroupId)
            assertEquals(expected.date, actual.date)
            assertEquals(expected.details, actual.details)
            assertEquals(expected.order, actual.order)
            assertEquals(expected.roomId, actual.roomId)
        }

        timetableApi.deleteTimetable(weekOfYear, 3, studyGroup1.id).also(::assertResultIsOk)
        assertTrue(timetableApi.getTimetableOfDay(weekOfYear, 3, listOf(studyGroup1.id)).unwrap().periods.isEmpty())
    }

    private fun PutTimetableRequest.toFlatPeriods() = run {
        monday + tuesday + wednesday + thursday + friday + saturday
    }

    private fun TimetableResponse.toFlatPeriods() = run {
        monday + tuesday + wednesday + thursday + friday + saturday
    }
}