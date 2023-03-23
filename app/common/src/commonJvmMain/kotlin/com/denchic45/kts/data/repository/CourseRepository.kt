package com.denchic45.kts.data.repository

import com.denchic45.kts.data.db.local.source.*
import com.denchic45.kts.data.fetchResource
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.observeResource
import com.denchic45.kts.data.pref.AppPreferences
import com.denchic45.kts.data.pref.CoursePreferences
import com.denchic45.kts.data.pref.TimestampPreferences
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.ContentAttachmentStorage
import com.denchic45.kts.data.storage.SubmissionAttachmentStorage
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.onSuccess
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.course.model.CourseResponse
import com.denchic45.stuiversity.api.course.model.CreateCourseRequest
import com.denchic45.stuiversity.api.course.model.UpdateCourseRequest
import com.denchic45.stuiversity.api.course.topic.CourseTopicApi
import com.denchic45.stuiversity.api.course.topic.RelatedTopicElements
import com.denchic45.stuiversity.api.course.topic.model.CreateTopicRequest
import com.denchic45.stuiversity.api.course.topic.model.TopicResponse
import com.denchic45.stuiversity.api.course.topic.model.UpdateTopicRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import java.util.*
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class CourseRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    private val coursePreferences: CoursePreferences,
    override val networkService: NetworkService,
    private val contentAttachmentStorage: ContentAttachmentStorage,
    private val submissionAttachmentStorage: SubmissionAttachmentStorage,
    private val userPreferences: UserPreferences,
    private val timestampPreferences: TimestampPreferences,
    private val appPreferences: AppPreferences,
    private val courseContentLocalDataSource: CourseContentLocalDataSource,
    private val submissionLocalDataSource: SubmissionLocalDataSource,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource,
    override val sectionLocalDataSource: SectionLocalDataSource,
    override val groupCourseLocalDataSource: GroupCourseLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
    private val coursesApi: CoursesApi,
    private val courseTopicApi: CourseTopicApi,
) : Repository(), NetworkServiceOwner, SaveGroupOperation, SaveCourseRepository,
    FindByContainsNameRepository<CourseResponse> {

    override suspend fun findByContainsName(text: String): Resource<List<CourseResponse>> {
        return fetchResource { coursesApi.search(text) }
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

//    fun find(courseId: String): Flow<Course?> {
//        return flow {
//            coroutineScope {
//                launch {
//                    courseRemoteDataSource.observeById(courseId).collect { courseMap ->
//                        courseMap?.let {
//                            if (courseMap.groupIds.isNotEmpty()) {
//                                groupRemoteDataSource.findByIdIn(courseMap.groupIds)
//                                    ?.let { saveGroups(it) }
//                            }
//                            saveCourse(courseMap)
//                        } ?: courseLocalDataSource.deleteById(courseId)
//                    }
//                }
//
//
//                emitAll(courseLocalDataSource.observeById(courseId).map {
//                    if (it.isEmpty()) return@map null
//                    it.entityToCourseDomain()
//                }.distinctUntilChanged())
//            }
//        }
//    }


    // FIXME: maybe remove
//    suspend fun observeByYourGroup() {
//        combine(timestampPreferences.observeGroupCoursesUpdateTimestamp.filter { it != 0L }
//            .drop(if (appPreferences.coursesLoadedFirstTime) 1 else 0),
//            groupPreferences.observeGroupId.filter(String::isNotEmpty)) { timestamp, groupId -> timestamp to groupId }.collect { (timestamp, groupId) ->
//            appPreferences.coursesLoadedFirstTime = true
//            getAndSaveCoursesByGroupIdRemotely(groupId)
//        }
//    }

    // TODO: Implement in backend
    suspend fun findByMe() = fetchResource {
        coursesApi.getCoursesByMe()
    }

//    fun findByYourAsTeacher(): Flow<List<CourseHeader>> = flow {
//        coroutineScope {
//            launch { getAndSaveCoursesByTeacherRemotely(userPreferences.id) }
//            emitAll(courseLocalDataSource.getByTeacherId(userPreferences.id)
//                .map { it.entitiesToDomains() })
//        }
//    }


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
//        val submissionEntities = mutableListOf<SubmissionEntity>()
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

//    fun findByGroupId(groupId: String): Flow<List<CourseHeader>> = flow {
//        if (groupId != groupPreferences.groupId) coroutineScope {
//            getAndSaveCoursesByGroupIdRemotely(groupId)
//        }
//        emitAll(courseLocalDataSource.observeCoursesByStudyGroupId(groupId)
//            .map { it.entitiesToCourseHeaders() })
//    }

    private suspend fun getAndSaveCoursesByGroupIdRemotely(studyGroupId: UUID) {
        fetchResource { coursesApi.getList(studyGroupId = studyGroupId) }
            .onSuccess {
                groupCourseLocalDataSource.deleteByGroup(studyGroupId.toString())
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

    // FIXME: may be remove
//    fun findByYourGroup(): Flow<List<CourseHeader>> {
//        return groupPreferences.observeGroupId.filter(String::isNotEmpty).flatMapLatest {
//            courseLocalDataSource.observeCoursesByStudyGroupId(it)
//        }.map { it.entitiesToCourseHeaders() }
//    }

    suspend fun add(createCourseRequest: CreateCourseRequest) = fetchResource {
        coursesApi.create(createCourseRequest)
    }

    suspend fun removeStudyGroupFromCourses(courseId: UUID, studyGroupId: UUID) = fetchResource {
        coursesApi.deleteStudyGroup(courseId, studyGroupId)
    }


//    suspend fun addTask(task: Task, attachments: List<Attachment>) {
//        requireAllowWriteData()
//        courseRemoteDataSource.addTask(
//            courseContentMap = CourseContentMap(task.toMap(
//                attachments = contentAttachmentStorage.addContentAttachments(
//                    task.id,
//                    attachments
//                ), order = createLastContentOrder(task)
//            ).apply {
//                val studentIdsOfCourse =
//                    userLocalDataSource.getStudentIdsOfCourseByCourseId(task.courseId)
//                put("notSubmittedByStudentIds", studentIdsOfCourse)
//                put("submissions", studentIdsOfCourse.associateWith { studentId ->
//                    SubmissionMap.createNotSubmitted(
//                        id = UUIDS.createShort(),
//                        studentId = studentId,
//                        contentId = task.id,
//                        courseId = task.courseId
//                    )
//                })
//            })
//        )
//    }

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


    fun findTopicsByCourseId(courseId: UUID): Flow<Resource<List<TopicResponse>>> {
        return observeResource(
            query = sectionLocalDataSource.getByCourseId(courseId.toString())
                .map { it.toTopicResponses() },
            fetch = { courseTopicApi.getByCourseId(courseId) },
            saveFetch = { sectionLocalDataSource.upsert(it.toTopicEntities(courseId)) }
        )
    }

    suspend fun findTopic(courseId: UUID, topicId: UUID) = observeResource(
        query = sectionLocalDataSource.observe(topicId.toString()).map { it.toResponse() },
        fetch = { courseTopicApi.getById(courseId, topicId) },
        saveFetch = { sectionLocalDataSource.upsert(it.toEntity(courseId)) }
    )

//    fun findTaskSubmissionByContentIdAndStudentId(
//        taskId: String,
//        studentId: String,
//    ): Flow<Task.Submission> {
//        return submissionLocalDataSource.getByTaskIdAndUserId(taskId, studentId).map { entities ->
//            entities?.toDomain(
//                getSubmissionAttachments(entities)
//
//            ) ?: Task.Submission.createEmptyNotSubmitted(
//                contentId = taskId,
//                student = userLocalDataSource.get(studentId)!!.toUserDomain()
//            )
//        }.distinctUntilChanged()
//    }

//    suspend fun updateSubmissionFromStudent(submission: Task.Submission) {
//        requireNetworkAvailable()
//        val studentId = submission.student.id
//        val contentId = submission.contentId
//
//        val attachmentUrls =
//            submissionAttachmentStorage.update(contentId, studentId, submission.content.attachments)
//
//        val courseId = findCourseIdByContentId(contentId)
//        courseRemoteDataSource.updateSubmissionFromStudent(
//            submissionMap = SubmissionMap(
//                submission.toMap(
//                    courseId,
//                    attachmentUrls
//                )
//            ),
//            studentId = studentId,
//            attachmentUrls = attachmentUrls,
//            courseId = courseId,
//            contentId = contentId
//        )
//    }

    private suspend fun findCourseIdByContentId(contentId: String) =
        courseLocalDataSource.getCourseIdByContentId(contentId)


//    suspend fun rejectSubmission(
//        taskId: String,
//        studentId: String,
//        cause: String,
//        teacherId: String = userPreferences.id,
//    ) {
//        requireNetworkAvailable()
//        courseRemoteDataSource.rejectSubmission(
//            courseContentLocalDataSource.getCourseIdByTaskId(
//                taskId
//            ), taskId, studentId, cause, teacherId
//        )
//    }


//    suspend fun isCourseTeacher(userId: String, courseId: String): Boolean {
//        return courseLocalDataSource.isCourseTeacher(courseId, userId)
//    }

    suspend fun updateTopic(
        courseId: UUID,
        topicId: UUID,
        updateTopicRequest: UpdateTopicRequest,
    ) = fetchResource {
        courseTopicApi.update(courseId, topicId, updateTopicRequest)
    }

//    suspend fun updateCourseSections(sections: List<Section>) {
//        requireAllowWriteData()
//        courseRemoteDataSource.updateCourseSections(sections.domainsToMaps())
//    }

    suspend fun addTopic(courseId: UUID, createTopicRequest: CreateTopicRequest) = fetchResource {
        courseTopicApi.create(courseId, createTopicRequest)
    }

    suspend fun removeTopic(
        courseId: UUID,
        topicId: UUID,
        relatedTopicElements: RelatedTopicElements,
    ) = fetchResource {
        courseTopicApi.delete(courseId, topicId, relatedTopicElements)
    }

//    suspend fun updateContentOrder(contentId: String, order: Int) {
//        requireAllowWriteData()
//        courseRemoteDataSource.updateContentOrder(
//            courseContentLocalDataSource.getCourseIdByTaskId(
//                contentId
//            ), contentId, order
//        )
//    }

//    fun findUpcomingTasksForYourGroup(): Flow<List<Task>> = flow {
//        coroutineScope {
//            launch {
//                courseRemoteDataSource.findUpcomingTasksByGroupId(userPreferences.id).collect {
//                    saveCourseContentsLocal(it)
//                }
//            }
//            emitAll(courseContentLocalDataSource.getByGroupIdAndGreaterCompletionDate(
//                groupPreferences.groupId
//            ).map { it.entitiesToTaskDomains() })
//        }
//    }

//    fun findOverdueTasksForYourGroup(): Flow<List<Task>> = flow {
//        coroutineScope {
//            launch {
//                saveCourseContentsLocal(
//                    courseRemoteDataSource.findOverdueTasksByGroupId(
//                        userPreferences.id
//                    )
//                )
//            }
//
//            emitAll(courseContentLocalDataSource.getByGroupIdAndNotSubmittedUser(
//                groupPreferences.groupId,
//                userPreferences.id
//            ).map { it.entitiesToTaskDomains() })
//        }
//    }

//    fun findCompletedTasksForYourGroup(): Flow<List<Task>> {
//        return flow {
//            coroutineScope {
//                launch {
//                    saveCourseContentsLocal(
//                        courseRemoteDataSource.findCompletedTasksByStudentId(
//                            userPreferences.id
//                        )
//                    )
//                }
//                emitAll(courseContentLocalDataSource.getByGroupIdAndSubmittedUser(
//                    groupPreferences.groupId,
//                    userPreferences.id
//                ).map { it.entitiesToTaskDomains() })
//            }
//
//        }
//    }

    suspend fun updateCourse(
        courseId: UUID,
        updateCourseRequest: UpdateCourseRequest,
    ) = fetchResource { coursesApi.update(courseId, updateCourseRequest) }


    suspend fun archiveCourse(courseId: UUID) = fetchResource {
        coursesApi.setArchive(courseId)
    }

    suspend fun unarchiveCourse(courseId: UUID) = fetchResource {
        coursesApi.setArchive(courseId)
    }

    suspend fun removeCourse(courseId: UUID) = fetchResource {
        coursesApi.delete(courseId)
    }
}

class SameCoursesException : Exception()

interface SaveCourseRepository {

    val userLocalDataSource: UserLocalDataSource
    val groupLocalDataSource: GroupLocalDataSource
    val courseLocalDataSource: CourseLocalDataSource

    val subjectLocalDataSource: SubjectLocalDataSource

    val sectionLocalDataSource: SectionLocalDataSource
    val groupCourseLocalDataSource: GroupCourseLocalDataSource


    suspend fun saveCourse(courseResponse: CourseResponse) {
        courseLocalDataSource.saveCourse(
            subjectEntity = courseResponse.subject?.toSubjectEntity(),
            courseEntity = courseResponse.toCourseEntity()
        )

//        groupCourseLocalDataSource.deleteByCourseId(courseMap.id)

//        userLocalDataSource.upsert(UserMap(courseMap.teacher).mapToUserEntity())

//        subjectLocalDataSource.upsert(SubjectMap(courseMap.subject).mapToSubjectEntity())

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