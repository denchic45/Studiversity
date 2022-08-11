package com.denchic45.kts.data.repository

import com.denchic45.kts.ContentCommentEntity
import com.denchic45.kts.GroupCourseEntity
import com.denchic45.kts.SubmissionCommentEntity
import com.denchic45.kts.SubmissionEntity
import com.denchic45.kts.data.db.local.model.SubmissionWithStudentEntities
import com.denchic45.kts.data.db.local.source.*
import com.denchic45.kts.data.db.remote.model.*
import com.denchic45.kts.data.db.remote.source.CourseRemoteDataSource
import com.denchic45.kts.data.db.remote.source.GroupRemoteDataSource
import com.denchic45.kts.data.domain.model.Attachment
import com.denchic45.kts.data.domain.model.DomainModel
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.pref.*
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.ContentAttachmentStorage
import com.denchic45.kts.data.storage.SubmissionAttachmentStorage
import com.denchic45.kts.domain.model.Course
import com.denchic45.kts.domain.model.CourseHeader
import com.denchic45.kts.domain.model.Section
import com.denchic45.kts.domain.model.Task
import com.denchic45.kts.util.CourseContents
import com.denchic45.kts.util.UUIDS
import com.denchic45.kts.util.differenceOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class CourseRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    private val coroutineScope: CoroutineScope,
    private val coursePreferences: CoursePreferences,
    private val groupPreferences: GroupPreferences,
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
    private val courseRemoteDataSource: CourseRemoteDataSource,
    private val groupRemoteDataSource: GroupRemoteDataSource,
) : Repository(), SaveGroupOperation, SaveCourseRepository,
    FindByContainsNameRepository<CourseHeader> {

    override fun findByContainsName(text: String): Flow<List<CourseHeader>> {
        return courseRemoteDataSource.findByContainsName(text)
            .map { courseMaps ->
                coroutineScope.launch {
                    saveCourses(courseMaps)

                }
                courseMaps.mapsToCourseHeaderDomains()
            }
    }

    fun find(courseId: String): Flow<Course?> {
        return flow {
            coroutineScope {
                launch {
                    courseRemoteDataSource.observeById(courseId).collect { courseMap ->
                        courseMap?.let {
                            if (courseMap.groupIds.isNotEmpty()) {
                                groupRemoteDataSource.findByIdIn(courseMap.groupIds)
                                    ?.let { saveGroups(it) }
                            }
                            saveCourse(courseMap)
                        } ?: courseLocalDataSource.deleteById(courseId)
                    }
                }


                emitAll(
                    courseLocalDataSource.observe(courseId)
                        .map {
                            if (it.isEmpty()) return@map null
                            it.entityToCourseDomain()
                        }
                        .distinctUntilChanged()
                )
            }
        }
    }

//    private fun coursesByTeacherIdQuery(teacherId: String): Query {
//        return coursesRef.whereEqualTo("teacher.id", teacherId)
//            .whereGreaterThan(
//                "timestamp",
//                Date(timestampPreferences.teacherCoursesUpdateTimestamp)
//            )
//    }

    suspend fun observeByYourGroup() {
        combine(
            timestampPreferences.observeGroupCoursesUpdateTimestamp
                .filter { it != 0L }
                .drop(if (appPreferences.coursesLoadedFirstTime) 1 else 0),
            groupPreferences.observeGroupId.filter(String::isNotEmpty)
        ) { timestamp, groupId -> timestamp to groupId }
            .collect { (timestamp, groupId) ->
                appPreferences.coursesLoadedFirstTime = true
                getAndSaveCoursesByGroupIdRemotely(groupId)
            }
    }


    fun findByYourAsTeacher(): Flow<List<CourseHeader>> = flow {
        coroutineScope {
            launch { getAndSaveCoursesByTeacherRemotely(userPreferences.id) }
            emitAll(
                courseLocalDataSource.getByTeacherId(userPreferences.id)
                    .map { it.entitiesToDomains() }
            )
        }
    }

    fun findContentByCourseId(courseId: String): Flow<List<DomainModel>> = flow {
        coroutineScope {
            launch {
                courseRemoteDataSource.findContentByCourseId(
                    courseId,
                    coursePreferences.getTimestampContentsOfCourse(courseId)
                )
                    .filterNotNull()
                    .filter { it.isNotEmpty() }
                    .collect { courseContents ->
                        val timestamp = courseContents.maxOf { it.timestamp }.time
                        saveCourseContentsLocal(courseContents)
                        coursePreferences.setTimestampContentsOfCourse(
                            courseId,
                            timestamp
                        )
                    }
            }
            emitAll(
                sectionLocalDataSource.getByCourseId(courseId)
                    .combine(courseContentLocalDataSource.getByCourseId(courseId)) { sectionEntities, courseContentEntities ->
                        CourseContents.sort(
                            courseContentEntities.entitiesToDomains(),
                            sectionEntities.entitiesToDomains()
                        )
                    }
                    .distinctUntilChanged()
            )
        }
    }

    private suspend fun saveCourseContentsLocal(courseContentMaps: List<CourseContentMap>) {
        val entities = courseContentMaps.filterNot(CourseContentMap::deleted)
            .map(CourseContentMap::domainToEntity)

        val removedCourseContentIds = courseContentMaps.filter { map -> map.deleted }.map { it.id }

        removedCourseContentIds.forEach {
            contentAttachmentStorage.deleteFromLocal(it)
            submissionAttachmentStorage.deleteFromLocal(it)
        }

        val submissionEntities = mutableListOf<SubmissionEntity>()
        val contentCommentEntities = mutableListOf<ContentCommentEntity>()
        val submissionCommentEntities = mutableListOf<SubmissionCommentEntity>()

        courseContentMaps.forEach {
            it.submissions.forEach { map ->
                SubmissionMap(map.value).let { submissionMap ->
                    submissionEntities.add(submissionMap.mapToEntity())
                    submissionCommentEntities.addAll(
                        it.comments.map(::SubmissionCommentMap).docsToEntity()
                    )
                    it.comments.let { contentComments ->
                        contentCommentEntities.addAll(
                            contentComments.map(::ContentCommentMap).docsToEntity()
                        )
                    }
                }
            }
        }
        courseContentLocalDataSource.saveContents(
            removedCourseContentIds = removedCourseContentIds,
            remainingCourseContent = entities,
            contentIds = courseContentMaps.map(CourseContentMap::id),
            submissionEntities = submissionEntities,
            contentCommentEntities = contentCommentEntities,
            submissionCommentEntities = submissionCommentEntities
        )
    }

    fun findByGroupId(groupId: String): Flow<List<CourseHeader>> = flow {
        coroutineScope {
            launch {
                if (groupId != groupPreferences.groupId)
                    getAndSaveCoursesByGroupIdRemotely(groupId)
                emitAll(courseLocalDataSource.observeCoursesByGroupId(groupId)
                    .map { it.entitiesToCourseHeaders() })
            }
        }
    }

    private suspend fun getAndSaveCoursesByGroupIdRemotely(groupId: String) {
        courseRemoteDataSource.findByGroupId(groupId).let {
            groupCourseLocalDataSource.deleteByGroup(groupId)
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

    private suspend fun getAndSaveCoursesByTeacherRemotely(teacherId: String) {
        courseRemoteDataSource.findCoursesByTeacherId(teacherId,
            timestampPreferences.teacherCoursesUpdateTimestamp)
            .collect { courseMaps ->
                saveCourses(courseMaps)
                timestampPreferences.teacherCoursesUpdateTimestamp =
                    courseMaps.maxOf { it.timestamp.time }
            }
    }

    fun findByYourGroup(): Flow<List<CourseHeader>> {
        return groupPreferences.observeGroupId
            .filter(String::isNotEmpty)
            .flatMapLatest {
                courseLocalDataSource.observeCoursesByGroupId(it)
            }
            .map { it.entitiesToCourseHeaders() }
    }

    suspend fun add(course: Course) {
        requireAllowWriteData()
        courseRemoteDataSource.addCourse(CourseMap(course.toCourseMap()))
    }

    suspend fun removeGroupFromCourses(groupId: String) {
        requireAllowWriteData()
        courseRemoteDataSource.removeGroupFromCourses(groupId)
    }

    fun findTask(id: String): Flow<Task?> = courseContentLocalDataSource.observe(id)
        .map { taskEntity -> taskEntity?.toTaskDomain() }

    fun findAttachmentsByContentId(contentId: String): Flow<List<Attachment>> {
        return courseContentLocalDataSource.getAttachmentsById(contentId)
            .map { attachments -> contentAttachmentStorage.get(contentId, attachments) }
    }

    suspend fun addTask(task: Task) {
        requireAllowWriteData()
        courseRemoteDataSource.addTask(
            task = CourseContentMap(task.toMap(
                attachments = contentAttachmentStorage.addContentAttachments(task.id,
                    task.attachments),
                order = createLastContentOrder(task)
            ).apply {
                val studentIdsOfCourse =
                    userLocalDataSource.getStudentIdsOfCourseByCourseId(task.courseId)
                put("notSubmittedByStudentIds", studentIdsOfCourse)
                put("submissions",
                    studentIdsOfCourse.associateWith { studentId ->
                        SubmissionMap.createNotSubmitted(
                            id = UUIDS.createShort(),
                            studentId = studentId,
                            contentId = task.id,
                            courseId = task.courseId)
                    })
            })
        )
    }

    private suspend fun getLastContentOrderByCourseIdAndSectionId(
        courseId: String,
        sectionId: String,
    ): Long = courseRemoteDataSource.findLastContentOrderByCourseIdAndSectionId(
        courseId,
        sectionId
    )


    suspend fun updateTask(task: Task) {
        requireAllowWriteData()

        val attachments = contentAttachmentStorage.update(task.id, task.attachments)
        val cacheTask = courseContentLocalDataSource.get(task.id)!!.run {
            toTaskDomain().toMap(attachments, order)
        }
        val map = task.toMap(attachments, task.order)
        val updatedFields = map.differenceOf(cacheTask).toMutableMap()
            .apply {
                if (containsKey("sectionId")) {
                    put(
                        "order",
                        createLastContentOrder(task))
                }
            }
        courseRemoteDataSource.updateTask(task.courseId, task.id, updatedFields)
    }

    private suspend fun createLastContentOrder(task: Task) =
        getLastContentOrderByCourseIdAndSectionId(task.courseId,
            task.sectionId) + 1024

    suspend fun removeCourseContent(contentId: String) {
        requireAllowWriteData()
        contentAttachmentStorage.deleteFilesByContentId(contentId)
        submissionAttachmentStorage.deleteFilesByContentId(contentId)
        courseRemoteDataSource.removeCourseContent(
            courseContentLocalDataSource.getCourseIdByTaskId(contentId),
            contentId
        )
    }


    fun findSectionsByCourseId(courseId: String) =
        sectionLocalDataSource.getByCourseId(courseId)
            .map { it.entitiesToDomains() }

    suspend fun findSection(sectionId: String): Section? {
        return sectionLocalDataSource.get(sectionId)?.entityToUserDomain()
    }

    fun findTaskSubmissionByContentIdAndStudentId(
        taskId: String,
        studentId: String,
    ): Flow<Task.Submission> {
        return submissionLocalDataSource.getByTaskIdAndUserId(taskId, studentId)
            .map { entities ->
                entities?.toDomain(
                    getSubmissionAttachments(entities)

                ) ?: Task.Submission.createEmptyNotSubmitted(
                    contentId = taskId,
                    student = userLocalDataSource.get(studentId)!!.toUserDomain()
                )
            }
            .distinctUntilChanged()
    }

    private suspend fun getSubmissionAttachments(entities: SubmissionWithStudentEntities) =
        entities.submissionEntity.attachments?.let {
            submissionAttachmentStorage.get(
                entities.submissionEntity.content_id,
                entities.submissionEntity.student_id,
                it
            )
        } ?: emptyList()

    suspend fun updateSubmissionFromStudent(submission: Task.Submission) {
        requireNetworkAvailable()
        val studentId = submission.student.id
        val contentId = submission.contentId

        val attachmentUrls = submissionAttachmentStorage.update(
            contentId,
            studentId,
            submission.content.attachments
        )

        val courseId = findCourseIdByContentId(contentId)
        courseRemoteDataSource.updateSubmissionFromStudent(
            submissionMap = SubmissionMap(submission.toMap(courseId, attachmentUrls)),
            studentId = studentId,
            attachmentUrls = attachmentUrls,
            courseId = courseId,
            contentId = contentId
        )
    }

    private suspend fun findCourseIdByContentId(contentId: String) =
        courseLocalDataSource.getCourseIdByContentId(contentId)

    suspend fun gradeSubmission(
        taskId: String,
        studentId: String,
        grade: Int,
        teacherId: String = userPreferences.id,
    ) {
        requireNetworkAvailable()
        courseRemoteDataSource.gradeSubmission(
            courseContentLocalDataSource.getCourseIdByTaskId(taskId),
            taskId,
            studentId,
            grade,
            teacherId
        )
    }

    suspend fun rejectSubmission(
        taskId: String,
        studentId: String,
        cause: String,
        teacherId: String = userPreferences.id,
    ) {
        requireNetworkAvailable()
        courseRemoteDataSource.rejectSubmission(
            courseContentLocalDataSource.getCourseIdByTaskId(taskId),
            taskId,
            studentId,
            cause,
            teacherId
        )
    }


    suspend fun isCourseTeacher(userId: String, courseId: String): Boolean {
        return courseLocalDataSource.isCourseTeacher(courseId, userId)
    }

    fun findTaskSubmissions(taskId: String): Flow<List<Task.Submission>> {
        return submissionLocalDataSource.getByTaskId(taskId).map { list ->
            list.map { it.toDomain(getSubmissionAttachments(it)) }
        }.mapLatest {
            it + submissionLocalDataSource.getStudentsWithoutSubmission(taskId)
                .map { userEntity ->
                    Task.Submission.createEmptyNotSubmitted(taskId, userEntity.toUserDomain())
                }
        }
            .distinctUntilChanged()
    }

    suspend fun updateCourseSections(sections: List<Section>) {
        requireAllowWriteData()
        courseRemoteDataSource.updateCourseSections(sections.domainsToMaps())
    }

    suspend fun addSection(section: Section) {
        requireAllowWriteData()
        courseRemoteDataSource.addSection(section.toMap())
    }

    suspend fun removeSection(section: Section) {
        requireAllowWriteData()
        courseRemoteDataSource.removeSection(SectionMap(section.toMap()))
    }

    suspend fun updateContentOrder(contentId: String, order: Int) {
        requireAllowWriteData()
        courseRemoteDataSource.updateContentOrder(
            courseContentLocalDataSource.getCourseIdByTaskId(contentId),
            contentId,
            order
        )
    }

    fun findUpcomingTasksForYourGroup(): Flow<List<Task>> = flow {
        coroutineScope {
            launch {
                courseRemoteDataSource.findUpcomingTasksByGroupId(userPreferences.id).collect {
                    saveCourseContentsLocal(it)
                }
            }
            emitAll(
                courseContentLocalDataSource.getByGroupIdAndGreaterCompletionDate(
                    groupPreferences.groupId
                ).map { it.entitiesToTaskDomains() }
            )
        }
    }

    fun findOverdueTasksForYourGroup(): Flow<List<Task>> = flow {
        coroutineScope {
            launch {
                saveCourseContentsLocal(
                    courseRemoteDataSource.findOverdueTasksByGroupId(userPreferences.id)
                )
            }

            emitAll(courseContentLocalDataSource.getByGroupIdAndNotSubmittedUser(
                groupPreferences.groupId,
                userPreferences.id
            ).map { it.entitiesToTaskDomains() })
        }
    }

    fun findCompletedTasksForYourGroup(): Flow<List<Task>> {
        return flow {
            coroutineScope {
                launch {
                    saveCourseContentsLocal(
                        courseRemoteDataSource.findCompletedTasksByStudentId(userPreferences.id)
                    )
                }
                emitAll(courseContentLocalDataSource.getByGroupIdAndSubmittedUser(
                    groupPreferences.groupId,
                    userPreferences.id
                ).map { it.entitiesToTaskDomains() })
            }

        }
    }

    suspend fun updateCourse(course: Course) {
        requireAllowWriteData()
        courseRemoteDataSource.updateCourse(
            CourseMap(courseLocalDataSource.get(course.id).entityToCourseDomain().toCourseMap()),
            CourseMap(course.toCourseMap()))
    }

    suspend fun removeCourse(courseId: String, groupIds: List<String>) {
        courseRemoteDataSource.removeCourse(courseId, groupIds)
    }
}


class SameCoursesException : Exception()

//interface UpdateCourseOperation : PreconditionsRepository, UpdateGroupsOfCourse {
//
//    val courseLocalDataSource: CourseLocalDataSource
//    val courseRemoteDataSource: CourseRemoteDataSource
//
//    suspend fun updateCourse(course: Course) {
//        requireAllowWriteData()
//        courseRemoteDataSource.updateCourse(
//            CourseMap(courseLocalDataSource.get(course.id).entityToCourseDomain().toCourseMap()),
//            CourseMap(course.toCourseMap()))
//    }
//}

interface SaveCourseRepository {

    val userLocalDataSource: UserLocalDataSource
    val groupLocalDataSource: GroupLocalDataSource
    val courseLocalDataSource: CourseLocalDataSource

    val subjectLocalDataSource: SubjectLocalDataSource

    val sectionLocalDataSource: SectionLocalDataSource
    val groupCourseLocalDataSource: GroupCourseLocalDataSource


    suspend fun saveCourse(courseMap: CourseMap) {
        courseLocalDataSource.saveCourse(
            courseId = courseMap.id,
            teacherEntity = UserMap(courseMap.teacher).mapToUserEntity(),
            subjectEntity = SubjectMap(courseMap.subject).mapToSubjectEntity(),
            courseEntity = courseMap.mapToCourseEntity(),
            sectionEntities = courseMap.sections.map { SectionMap(it).mapToEntity() },
            groupCourseEntities = courseMap.groupIds
                .filter(groupLocalDataSource::isExist)
                .map { groupId -> GroupCourseEntity(groupId, courseMap.id) }
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

    suspend fun saveCourses(courseDocs: List<CourseMap>) {
        courseDocs.forEach { saveCourse(it) }
    }
}