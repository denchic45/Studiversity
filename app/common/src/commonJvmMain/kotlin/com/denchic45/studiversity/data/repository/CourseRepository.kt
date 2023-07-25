package com.denchic45.studiversity.data.repository

import com.denchic45.studiversity.data.db.local.source.CourseLocalDataSource
import com.denchic45.studiversity.data.db.local.source.CourseTopicLocalDataSource
import com.denchic45.studiversity.data.db.local.source.SpecialtyLocalDataSource
import com.denchic45.studiversity.data.db.local.source.StudyGroupCourseLocalDataSource
import com.denchic45.studiversity.data.db.local.source.StudyGroupLocalDataSource
import com.denchic45.studiversity.data.db.local.source.SubjectLocalDataSource
import com.denchic45.studiversity.data.db.local.source.UserLocalDataSource
import com.denchic45.studiversity.data.fetchResource
import com.denchic45.studiversity.data.fetchResourceFlow
import com.denchic45.studiversity.data.mapper.toCourseEntity
import com.denchic45.studiversity.data.mapper.toCourseResponse
import com.denchic45.studiversity.data.mapper.toSubject
import com.denchic45.studiversity.data.observeResource
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.resource.Resource
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.util.uuidOfMe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class CourseRepository(
    override val networkService: NetworkService,
    override val userLocalDataSource: UserLocalDataSource,
    override val studyGroupLocalDataSource: StudyGroupLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource,
    override val courseTopicLocalDataSource: CourseTopicLocalDataSource,
    override val studyGroupCourseLocalDataSource: StudyGroupCourseLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
    private val coursesApi: CoursesApi,
) : NetworkServiceOwner, SaveGroupOperation, SaveCourseRepository,
    FindByContainsNameRepository<CourseResponse> {

    override fun findByContainsName(text: String): Flow<Resource<List<CourseResponse>>> {
        return fetchResourceFlow { coursesApi.search(text) }
    }

    fun observeById(courseId: UUID) = observeResource(
        query = courseLocalDataSource.observeById(courseId.toString())
            .map { it.toCourseResponse() },
        observe = { flow { emit(coursesApi.getById(courseId)) } },
        saveObserved = { saveCourse(it) }
    )

    suspend fun findById(courseId: UUID) = fetchResource {
        coursesApi.getById(courseId)
    }

    suspend fun findByMe() = fetchResource {
        coursesApi.getList(memberId = uuidOfMe())
    }

//    fun findContentByCourseId(courseId: String): Flow<List<DomainModel>> = flow {
//        coroutineScope {
//            launch {
//                courseRemoteDataSource.findContentByCourseId(
//                    courseId,
//                    coursePreferences.getTimestampContentsOfCourse(courseId)
//                ).filterNotNull()
//                    .filter { it.isNotEmpty() }.collect { courseContents ->
//                        val timestamp = courseContents.maxOf { it.timestamp }.time
//                        saveCourseContentsLocal(courseContents)
//                        coursePreferences.setTimestampContentsOfCourse(courseId, timestamp)
//                    }
//            }
//            emitAll(
//                sectionLocalDataSource.getByCourseId(courseId)
//                    .combine(courseContentLocalDataSource.getByCourseId(courseId)) { sectionEntities, courseContentEntities ->
//                        CourseContents.sort(
//                            courseContentEntities.entitiesToDomains(),
//                            sectionEntities.entitiesToDomains()
//                        )
//                    }.distinctUntilChanged()
//            )
//        }
//    }

//    private suspend fun saveCourseContentsLocal(courseContentMaps: List<CourseContentMap>) {
//        val entities = courseContentMaps.filterNot(CourseContentMap::deleted)
//            .map(CourseContentMap::domainToEntity)
//
//        val removedCourseContentIds = courseContentMaps.filter { map -> map.deleted }.map { it.id }
//
//        removedCourseContentIds.forEach {
//            contentAttachmentStorage.deleteFromLocal(it)
//            submissionAttachmentStorage.deleteFromLocal(it)
//        }
//
//        val submissionEntities = mutableListOf<Submission>()
//        val contentCommentEntities = mutableListOf<ContentCommentEntity>()
//        val submissionCommentEntities = mutableListOf<SubmissionCommentEntity>()
//
//        courseContentMaps.forEach {
//            it.submissions.forEach { map ->
//                SubmissionMap(map.value).let { submissionMap ->
//                    submissionEntities.add(submissionMap.mapToEntity())
//                    submissionCommentEntities.addAll(
//                        it.comments.map(::SubmissionCommentMap)
//                            .docsToEntity()
//                    )
//                    it.comments.let { contentComments ->
//                        contentCommentEntities.addAll(
//                            contentComments.map(::ContentCommentMap)
//                                .docsToEntity()
//                        )
//                    }
//                }
//            }
//        }
//        courseContentLocalDataSource.saveContents(
//            removedCourseContentIds = removedCourseContentIds,
//            remainingCourseContent = entities,
//            contentIds = courseContentMaps.map(CourseContentMap::id),
//            submissionEntities = submissionEntities,
//            contentCommentEntities = contentCommentEntities,
//            submissionCommentEntities = submissionCommentEntities
//        )
//    }

    suspend fun findByStudyGroupId(studyGroupId: UUID) = fetchResource {
        coursesApi.getList(studyGroupId = studyGroupId)
    }

    private suspend fun getAndSaveCoursesByGroupIdRemotely(studyGroupId: UUID) {
        fetchResource { coursesApi.getList(studyGroupId = studyGroupId) }
            .onSuccess {
                studyGroupCourseLocalDataSource.deleteByGroup(studyGroupId.toString())
                saveCourses(it)
            }
    }

//    private suspend fun getCoursesByGroupIdRemotely(groupId: String) {
//        coursesByGroupIdQuery(groupId).get().await().let {
//            if (!it.isEmpty)
//                coroutineScope.launch {
//                    groupCourseLocalDataSource.deleteByGroup(groupId)
//                    saveCourses(it.toMaps(::CourseMap))
//                }
//        }
//    }

//    private suspend fun getAndSaveCoursesByTeacherRemotely(teacherId: String) {
//        courseRemoteDataSource.findCoursesByTeacherId(
//            teacherId,
//            timestampPreferences.teacherCoursesUpdateTimestamp
//        ).collect { courseMaps ->
//            saveCourses(courseMaps)
//            timestampPreferences.teacherCoursesUpdateTimestamp =
//                courseMaps.maxOf { it.timestamp.time }
//        }
//    }

    suspend fun add(request: CreateCourseRequest) = fetchResource {
        coursesApi.create(request)
    }

    suspend fun update(
        courseId: UUID,
        request: UpdateCourseRequest,
    ) = fetchResource { coursesApi.update(courseId, request) }

//    private suspend fun getLastContentOrderByCourseIdAndSectionId(
//        courseId: String,
//        sectionId: String,
//    ): Long = courseRemoteDataSource.findLastContentOrderByCourseIdAndSectionId(courseId, sectionId)


//    suspend fun updateTask(task: Task, attachments: List<Attachment>) {
//        requireAllowWriteData()
//
//        val attachmentUrls = contentAttachmentStorage.update(task.id, attachments)
//        val cacheTask = courseContentLocalDataSource.get(task.id)!!.run {
//            toTaskDomain().toMap(this.attachments, order)
//        }
//        val map = task.toMap(attachmentUrls, task.order)
//        val updatedFields = map.differenceOf(cacheTask).toMutableMap().apply {
//            if (containsKey("sectionId")) put("order", createLastContentOrder(task))
//
//        }
//        courseRemoteDataSource.updateTask(task.courseId, task.id, updatedFields)
//    }

//    private suspend fun createLastContentOrder(task: Task) =
//        getLastContentOrderByCourseIdAndSectionId(task.courseId, task.sectionId) + 1024


    suspend fun archiveCourse(courseId: UUID) = fetchResource {
        coursesApi.setArchive(courseId)
    }

    suspend fun unarchiveCourse(courseId: UUID) = fetchResource {
        coursesApi.unarchive(courseId)
    }

    suspend fun removeCourse(courseId: UUID) = fetchResource {
        coursesApi.delete(courseId)
    }
}

interface SaveCourseRepository {

    val userLocalDataSource: UserLocalDataSource
    val studyGroupLocalDataSource: StudyGroupLocalDataSource
    val courseLocalDataSource: CourseLocalDataSource

    val subjectLocalDataSource: SubjectLocalDataSource

    val courseTopicLocalDataSource: CourseTopicLocalDataSource
    val studyGroupCourseLocalDataSource: StudyGroupCourseLocalDataSource


    suspend fun saveCourse(courseResponse: CourseResponse) {
        courseLocalDataSource.saveCourse(
            subject = courseResponse.subject?.toSubject(),
            courseEntity = courseResponse.toCourseEntity()
        )

//        groupCourseLocalDataSource.deleteByCourseId(courseMap.id)

//        userLocalDataSource.upsert(UserMap(courseMap.teacher).mapToUser())

//        subjectLocalDataSource.upsert(SubjectMap(courseMap.subject).mapToSubject())

//        courseLocalDataSource.upsert(courseMap.mapToEntity())

//        sectionLocalDataSource.deleteByCourseId(courseMap.id)

//        sectionLocalDataSource.upsert(
//            courseMap.sections?.map { SectionMap(it).mapToEntity() } ?: emptyList()
//        )

//        groupCourseLocalDataSource.upsert(
//            courseMap.groupIds
//                .filter { groupLocalDataSource.isExist(it) }
//                .map { groupId -> GroupCourseEntity(groupId, courseMap.id) }
//        )
    }

    suspend fun saveCourses(courseDocs: List<CourseResponse>) {
        courseDocs.forEach { saveCourse(it) }
    }
}