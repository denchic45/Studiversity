package com.denchic45.kts

import com.denchic45.kts.domain.timetable.TimetableParser
import com.denchic45.stuiversity.api.common.EmptyResponseResult
import com.denchic45.stuiversity.api.common.ResponseResult
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.api.course.subject.SubjectApi
import com.denchic45.stuiversity.api.course.subject.model.CreateSubjectRequest
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.course.subject.model.UpdateSubjectRequest
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.studygroup.model.AcademicYear
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.denchic45.stuiversity.api.user.UserApi
import com.denchic45.stuiversity.api.user.model.Account
import com.denchic45.stuiversity.api.user.model.CreateUserRequest
import com.denchic45.stuiversity.api.user.model.Gender
import com.denchic45.stuiversity.api.user.model.UserResponse
import com.denchic45.stuiversity.util.UUIDWrapper
import com.github.michaelbull.result.Ok
import kotlinx.coroutines.runBlocking
import org.sk.PrettyTable
import java.io.File
import java.net.URISyntaxException
import java.net.URL
import java.util.UUID
import kotlin.test.Test


class TimetableParserTest {

    private val inputStream = ClassLoader.getSystemResourceAsStream("test-timetable.docx")!!

    init {
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLInputFactory",
            "com.fasterxml.aalto.stax.InputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLOutputFactory",
            "com.fasterxml.aalto.stax.OutputFactoryImpl"
        )
        System.setProperty(
            "org.apache.poi.javax.xml.stream.XMLEventFactory",
            "com.fasterxml.aalto.stax.EventFactoryImpl"
        )
    }

    @Test
    fun test(): Unit = runBlocking {
        val parseDoc = TimetableParser(
            studyGroupApi = studyGroupApi,
            userApi = userApi,
            courseApi = courseApi,
            subjectApi = subjectApi
        ).parseDoc(inputStream)
        val table = PrettyTable(*parseDoc.map { it.first.name }.toTypedArray())
        parseDoc.forEach {
            it.second.days.forEach {

            }
        }
    }

    private val studyGroupApi = object : StudyGroupApi {
        override suspend fun create(createStudyGroupRequest: CreateStudyGroupRequest): ResponseResult<StudyGroupResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getById(studyGroupId: UUID): ResponseResult<StudyGroupResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getList(
            memberId: UUIDWrapper?,
            roleId: Long?,
            specialtyId: UUID?,
            academicYear: Int?,
            query: String?,
        ): ResponseResult<List<StudyGroupResponse>> {
            return if (query == "ПКС – 4.2") Ok(
                listOf(
                    StudyGroupResponse(
                        id = UUID.randomUUID(),
                        name = "ПКС – 4.2",
                        academicYear = AcademicYear(2019, 2023),
                        specialty = null
                    )
                )
            ) else Ok(emptyList())
        }

        override suspend fun update(
            studyGroupId: UUID,
            updateStudyGroupRequest: UpdateStudyGroupRequest,
        ): ResponseResult<StudyGroupResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun delete(studyGroupId: UUID): EmptyResponseResult {
            TODO("Not yet implemented")
        }

        override suspend fun getByAcademicYear(academicYear: Int): ResponseResult<List<StudyGroupResponse>> {
            TODO("Not yet implemented")
        }
    }

    private val userApi = object : UserApi {
        override suspend fun create(createUserRequest: CreateUserRequest): ResponseResult<UserResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getMe(): ResponseResult<UserResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getById(userId: UUID): ResponseResult<UserResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getBySurname(surname: String): ResponseResult<List<UserResponse>> {
            return Ok(
                if (surname == "Иван")
                    listOf(
                        UserResponse(
                            id = UUID.randomUUID(),
                            firstName = "Иван",
                            surname = "Иванов",
                            patronymic = "Иванович",
                            account = Account("ivan@gmail.com"),
                            avatarUrl = "",
                            gender = Gender.MALE
                        )
                    )
                else emptyList()
            )

        }

        override suspend fun getList(query: String): ResponseResult<List<UserResponse>> {
            TODO("Not yet implemented")
        }

        override suspend fun delete(userId: UUID): EmptyResponseResult {
            TODO("Not yet implemented")
        }
    }

    private val courseApi = object : CoursesApi {
        override suspend fun create(createCourseRequest: CreateCourseRequest): ResponseResult<CourseResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getById(courseId: UUID): ResponseResult<CourseResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getList(
            memberId: UUIDWrapper,
            studyGroupId: UUID?,
            subjectId: UUID?,
            query: String?,
        ): ResponseResult<List<CourseResponse>> {
            return Ok(
                if (subjectId == subjectResponse.id)
                    listOf(courseResponse)
                else emptyList()
            )
        }

        override suspend fun update(
            courseId: UUID,
            updateCourseRequest: UpdateCourseRequest,
        ): ResponseResult<CourseResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getStudyGroupIds(courseId: UUID): ResponseResult<List<UUID>> {
            TODO("Not yet implemented")
        }

        override suspend fun putStudyGroup(
            courseId: UUID,
            studyGroupId: UUID,
        ): EmptyResponseResult {
            TODO("Not yet implemented")
        }

        override suspend fun deleteStudyGroup(
            courseId: UUID,
            studyGroupId: UUID,
        ): EmptyResponseResult {
            TODO("Not yet implemented")
        }

        override suspend fun setArchive(courseId: UUID): EmptyResponseResult {
            TODO("Not yet implemented")
        }

        override suspend fun unarchive(courseId: UUID): EmptyResponseResult {
            TODO("Not yet implemented")
        }

        override suspend fun delete(courseId: UUID): EmptyResponseResult {
            TODO("Not yet implemented")
        }
    }

    private val subjectResponse = SubjectResponse(
        id = UUID.randomUUID(),
        name = "МДК",
        shortname = "",
        iconUrl = ""
    )

    private val courseResponse = CourseResponse(
        id = UUID.randomUUID(),
        name = "Курс мдк",
        subject = subjectResponse,
        archived = false
    )

    private val subjectApi = object : SubjectApi {
        override suspend fun create(createSubjectRequest: CreateSubjectRequest): ResponseResult<SubjectResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun getById(subjectId: UUID): ResponseResult<SubjectResponse> {
            TODO("Not yet implemented")
        }


        override suspend fun getList(name: String?): ResponseResult<List<SubjectResponse>> {
            return Ok(
                if (name == subjectResponse.name)
                    listOf(subjectResponse)
                else emptyList()
            )
        }

        override suspend fun update(
            subjectId: UUID,
            updateSubjectRequest: UpdateSubjectRequest,
        ): ResponseResult<SubjectResponse> {
            TODO("Not yet implemented")
        }

        override suspend fun search(query: String): ResponseResult<List<SubjectResponse>> {
            TODO("Not yet implemented")
        }

        override suspend fun delete(subjectId: UUID): EmptyResponseResult {
            TODO("Not yet implemented")
        }
    }
}

@Throws(URISyntaxException::class)
fun getFileFromResource(fileName: String): File {
    val resource: URL? = ClassLoader.getSystemResource(fileName)
    return if (resource == null) {
        throw IllegalArgumentException("file not found! $fileName")
    } else {
        // failed if files have whitespaces or special characters
        //return new File(resource.getFile());
        File(resource.toURI())
    }
}