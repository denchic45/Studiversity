package com.denchic45.kts.data.repository

import androidx.room.withTransaction
import com.denchic45.kts.*
import com.denchic45.kts.data.database.DataBase
import com.denchic45.kts.data.local.db.*
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.model.mapper.CourseContentMapper
import com.denchic45.kts.data.model.mapper.CourseMapper
import com.denchic45.kts.data.model.mapper.SubmissionMapper
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.prefs.AppPreference
import com.denchic45.kts.data.prefs.CoursePreference
import com.denchic45.kts.data.prefs.TimestampPreference
import com.denchic45.kts.data.prefs.TimestampPreference.Companion.TIMESTAMP_LAST_UPDATE_GROUP_COURSES
import com.denchic45.kts.data.remote.model.*
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.data.storage.ContentAttachmentStorage
import com.denchic45.kts.data.storage.SubmissionAttachmentStorage
import com.denchic45.kts.di.modules.IoDispatcher
import com.denchic45.kts.domain.DomainModel
import com.denchic45.kts.domain.model.*
import com.denchic45.kts.util.*
import com.google.firebase.firestore.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class CourseRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    override val coroutineScope: CoroutineScope,
    @IoDispatcher override val dispatcher: CoroutineDispatcher,
    override val courseMapper: CourseMapper,
    private val submissionMapper: SubmissionMapper,
    private val coursePreference: CoursePreference,
    private val groupPreferences: GroupPreferences,
    override val networkService: NetworkService,
    private val contentAttachmentStorage: ContentAttachmentStorage,
    private val submissionAttachmentStorage: SubmissionAttachmentStorage,
    override val firestore: FirebaseFirestore,
    override val dataBase: DataBase,
    private val appDatabase: AppDatabase,
    private val userPreferences: UserPreferences,
    private val timestampPreference: TimestampPreference,
    private val appPreference: AppPreference,
    private val courseContentMapper: CourseContentMapper,
//    private val courseContentLocalDataSource: courseContentLocalDataSource,
    private val courseContentLocalDataSource: CourseContentLocalDataSource,
    private val submissionLocalDataSource: SubmissionLocalDataSource,
    private val contentCommentLocalDataSource: ContentCommentLocalDataSource,
    private val submissionCommentLocalDataSource: SubmissionCommentLocalDataSource,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource,
    override val sectionLocalDataSource: SectionLocalDataSource,
    override val groupCourseLocalDataSource: GroupCourseLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
) : Repository(), SaveGroupOperation, SaveCourseRepository, RemoveCourseOperation,
    FindByContainsNameRepository<CourseHeader>, UpdateCourseOperation {
    override val groupsRef: CollectionReference = firestore.collection("Groups")
    override val coursesRef: CollectionReference = firestore.collection("Courses")
    private val contentsRef: Query = firestore.collectionGroup("Contents")

    override fun findByContainsName(text: String): Flow<List<CourseHeader>> {
        return coursesRef
            .whereArrayContains("searchKeys", text.lowercase())
            .getDataFlow { it.toMaps(::CourseMap) }
            .map { courseMaps ->
                dataBase.withTransaction {
                    saveCourses(courseMaps)
                }
                courseMaps.mapsToCourseHeaderDomains()
            }
    }

    fun find(courseId: String): Flow<Course?> = courseLocalDataSource.observe(courseId)
        .withSnapshotListener(
            documentReference = coursesRef.document(courseId),
            onDocumentSnapshot = { documentSnapshot: DocumentSnapshot ->
                if (!documentSnapshot.exists()) {
                    coroutineScope.launch {
                        courseLocalDataSource.deleteById(courseId)
                    }
                    return@withSnapshotListener
                }
                if (documentSnapshot.timestampIsNull())
                    return@withSnapshotListener
                val courseMap = documentSnapshot.toMap(::CourseMap)
                coroutineScope.launch {
                    dataBase.withTransaction {
                        if (courseMap.groupIds.isNotEmpty()) {
                            val querySnapshot = groupsRef.whereIn("id", courseMap.groupIds)
                                .get()
                                .await()
                            if (querySnapshot.timestampsNotNull())
                                saveGroups(querySnapshot.toMaps(::GroupMap))
                        }
                        saveCourse(courseMap)
                    }
                }
            }
        )
        .map {
            if (it.isEmpty()) return@map null
            it.entityToCourseDomain()
        }
        .distinctUntilChanged()

    private fun coursesByGroupIdQuery(groupId: String): Query {
        return coursesRef.whereArrayContains("groupIds", groupId)
    }

    private fun coursesByTeacherIdQuery(teacherId: String): Query {
        return coursesRef.whereEqualTo("teacher.id", teacherId)
            .whereGreaterThan(
                "timestamp",
                Date(timestampPreference.updateTeacherCoursesTimestamp)
            )
    }

    suspend fun observeByYourGroup() {
        combine(
            timestampPreference.observeValue(TIMESTAMP_LAST_UPDATE_GROUP_COURSES, 0L)
                .filter { it != 0L }
                .drop(if (appPreference.coursesLoadedFirstTime) 1 else 0),
            groupPreferences.observeGroupId.filter(String::isNotEmpty)
        ) { timestamp, groupId -> timestamp to groupId }
            .collect { (timestamp, groupId) ->
                appPreference.coursesLoadedFirstTime = true
                getCoursesByGroupIdRemotely(groupId)
            }
    }


    fun findByYourAsTeacher(): Flow<List<CourseHeader>> {
        getCoursesByTeacherRemotely(userPreferences.id)
        return courseLocalDataSource.getByTeacherId(userPreferences.id)
            .map { it.entitiesToDomains() }
    }

    fun findContentByCourseId(courseId: String): Flow<List<DomainModel>> {
        val query = coursesRef.document(courseId).collection("Contents").run {
            val timestampContentsOfCourse = coursePreference.getTimestampContentsOfCourse(courseId)
            if (timestampContentsOfCourse == 0L)
                whereEqualTo("deleted", false)
            else
                whereGreaterThan("timestamp", Date(timestampContentsOfCourse))
        }

        addListenerRegistrationIfNotExist("findContentByCourseId: $courseId") {
            query.addSnapshotListener { value, error ->
                if (error != null) {
                    return@addSnapshotListener
                }
                if (value != null && !value.isEmpty) {
                    if (value.timestampsNotNull()) {
                        appDatabase.transaction {
                            coroutineScope.launch(dispatcher) {
                                val courseContents = courseContentMapper.docToEntity(value)
                                saveCourseContentsLocal(value.toMaps(::CourseContentMap))
                                coursePreference.setTimestampContentsOfCourse(
                                    courseId,
                                    courseContents.first.maxOf { it.timestamp }.time
                                )
                            }
                        }
                    }
                }
            }
        }

        return sectionLocalDataSource.getByCourseId(courseId)
            .combine(courseContentLocalDataSource.getByCourseId(courseId)) { sectionEntities, courseContentEntities ->
                CourseContents.sort(
                    courseContentEntities.entitiesToDomains(),
                    sectionEntities.entitiesToDomains()
//                    sectionMapper.entityToDomain(sectionEntities)
                )
            }
            .distinctUntilChanged()
    }

    private suspend fun saveCourseContentsLocal(
        courseContents: List<CourseContentMap>
    ) {

        val entities = courseContents.map { it.domainToEntity() }

        val removedCourseContents = entities.filter { entity ->
            entities.first { it.content_id == entity.course_id }.deleted
        }
        val remainingCourseContent = entities - removedCourseContents.toSet()

        removedCourseContents.forEach {
            contentAttachmentStorage.deleteFromLocal(it.content_id)
            submissionAttachmentStorage.deleteFromLocal(it.content_id)
        }

        appDatabase.transaction {
            coroutineScope.launch {
                courseContentLocalDataSource.delete(removedCourseContents)
                courseContentLocalDataSource.upsert(remainingCourseContent)

                val submissionEntities = mutableListOf<SubmissionEntity>()
                val contentCommentEntities = mutableListOf<ContentCommentEntity>()
                val submissionCommentEntities = mutableListOf<SubmissionCommentEntity>()

                courseContents.forEach {
                    submissionLocalDataSource.deleteByContentId(it.id)
                    contentCommentLocalDataSource.deleteByContentId(it.id)

                    it.submissions.forEach { submissionDoc ->
                        submissionEntities.add(
                            submissionDoc.value.domainToEntity()
                        )
                        submissionCommentEntities.addAll(submissionDoc.value.comments.docsToEntity())
                        it.comments.let { contentComments ->
                            contentCommentEntities.addAll(contentComments.docsToEntity())
                        }
                    }
                }

                submissionLocalDataSource.upsert(submissionEntities)
                contentCommentLocalDataSource.upsert(contentCommentEntities)
                submissionCommentLocalDataSource.upsert(submissionCommentEntities)
            }
        }
    }

    fun findByGroupId(groupId: String): Flow<List<CourseHeader>> {
        if (groupId != groupPreferences.groupId)
            coroutineScope.launch {
                dataBase.withTransaction {
                    getCoursesByGroupIdRemotely(groupId)
                }
            }
        return courseLocalDataSource.observeCoursesByGroupId(groupId)
            .map { it.entitiesToCourseHeaders() }
    }

    private suspend fun getCoursesByGroupIdRemotely(groupId: String) {
        coursesByGroupIdQuery(groupId).get().await().let {
            if (!it.isEmpty)
                dataBase.withTransaction {
                    groupCourseLocalDataSource.deleteByGroup(groupId)
                    saveCourses(it.toMaps(::CourseMap))
                }
        }
    }

    private fun getCoursesByTeacherRemotely(teacherId: String) {
        coursesByTeacherIdQuery(teacherId).addSnapshotListener { value, error ->
            coroutineScope.launch {
                value?.let { value ->
                    if (!value.isEmpty) {
                        value.toMaps(::CourseMap).apply {
                            dataBase.withTransaction {
                                saveCourses(this)
                            }
                            timestampPreference.updateTeacherCoursesTimestamp =
                                this.maxOf { it.timestamp.time }
                        }
                    }
                } ?: error?.printStackTrace()
            }
        }
    }

    fun findByYourGroup(): Flow<List<CourseHeader>> {
        return groupPreferences.observeGroupId
            .filter(String::isNotEmpty)
            .flatMapLatest { courseLocalDataSource.observeCoursesByGroupId(it) }
            .map { it.entitiesToCourseHeaders() }
    }

    suspend fun add(course: Course) {
        requireAllowWriteData()
        val courseMap = CourseMap(course.domainToCourseMap())
        val batch = firestore.batch()
        (courseMap.groupIds).forEach { groupId ->
            batch.update(
                groupsRef.document(groupId),
                "timestamp",
                FieldValue.serverTimestamp()
            )

            batch.update(
                groupsRef.document(groupId),
                "timestampCourses",
                FieldValue.serverTimestamp()
            )
        }
        batch.set(coursesRef.document(courseMap.id), courseMap)
        batch.commit().await()
    }

//    suspend fun update(course: Course) {
//        requireAllowWriteData()
//        val batch = firestore.batch()
//        val courseDoc = courseMapper.domainToDoc(course)
//
//        val oldCourse = courseMapper.entityToDomain2(courseDao.get(course.id))
//        val oldCourseDoc = courseMapper.domainToDoc(oldCourse)
//
//        (oldCourseDoc.groupIds + courseDoc.groupIds).forEach { groupId ->
//            batch.update(
//                groupsRef.document(groupId),
//                "timestamp",
//                FieldValue.serverTimestamp()
//            )
//
//            batch.update(
//                groupsRef.document(groupId),
//                "timestampCourses",
//                FieldValue.serverTimestamp()
//            )
//        }
//
//        batch.update(
//            coursesRef.document(courseDoc.id),
//            FieldsComparator.mapOfDifference(oldCourseDoc, courseDoc)
//        )
//        batch.commit().await()
//    }


    private fun timestampFiledPair() = "timestamp" to FieldValue.serverTimestamp()

    suspend fun removeGroupFromCourses(groupId: String) {
        requireAllowWriteData()
        val courseDocs = coursesByGroupIdQuery(groupId)
            .get()
            .await()
            .toMaps(::CourseMap)
        val batch = firestore.batch()
        courseDocs.forEach { courseDoc ->
            batch.update(
                coursesRef.document(courseDoc.id),
                mapOf(
                    timestampFiledPair(),
                    "groupIds" to FieldValue.arrayRemove(groupId)
                )
            )
        }
        batch.commit().await()
    }

    fun findTask(id: String): Flow<Task?> {
        return courseContentLocalDataSource.observe(id)
            .map { taskEntity -> taskEntity?.toTaskDomain() }
    }

    fun findAttachmentsByContentId(contentId: String): Flow<List<Attachment>> {
        return courseContentLocalDataSource.getAttachmentsById(contentId)
            .filterNotNull()
            .mapLatest { attachments -> contentAttachmentStorage.get(contentId, attachments) }
    }

    suspend fun addTask(task: Task) {
        requireAllowWriteData()
        val attachments = contentAttachmentStorage.addContentAttachments(task.id, task.attachments)
        val order = getLastContentOrderByCourseIdAndSectionId(task.courseId, task.sectionId) + 1024
        coursesRef.document(task.courseId)
            .collection("Contents")
            .document(task.id)
            .set(courseContentMapper.domainToTaskDoc(task).apply {
                this.attachments = attachments
                this.order = order
            }.asMutableMap().apply {
                val studentIdsOfCourseByCourseId =
                    userLocalDataSource.getStudentIdsOfCourseByCourseId(task.courseId)
                val mapOfEmptySubmissions =
                    studentIdsOfCourseByCourseId
                        .associateWith { studentId ->
                            SubmissionDoc.createNotSubmitted(
                                studentId,
                                task.id,
                                task.courseId
                            )
                        }
                put("submissions", mapOfEmptySubmissions)
                put("notSubmittedByStudentIds", studentIdsOfCourseByCourseId)
            }
            )
            .await()
    }

    private suspend fun getLastContentOrderByCourseIdAndSectionId(
        courseId: String,
        sectionId: String
    ): Long {
        val snapshot = coursesRef.document(courseId).collection("Contents")
            .whereEqualTo("sectionId", sectionId)
            .orderBy("order", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .await()

        return if (snapshot.isEmpty) 0L
        else snapshot.documents[0].getLong("order")!!
    }

    suspend fun updateTask(task: Task) {
        requireAllowWriteData()
        val attachments = contentAttachmentStorage.update(task.id, task.attachments)
        val cacheTask =
            courseContentLocalDataSource.get(task.id)!!.entityToTaskDoc()
        val updatedFields =
            FieldsComparator.mapOfDifference(cacheTask, courseContentMapper.domainToDoc(task))
        updatedFields["attachments"] = attachments
        updatedFields["timestamp"] = Date()
        if (updatedFields.containsKey("sectionId"))
            updatedFields["order"] =
                getLastContentOrderByCourseIdAndSectionId(task.courseId, task.sectionId) + 1024
        coursesRef.document(task.courseId)
            .collection("Contents")
            .document(task.id)
            .update(updatedFields)
            .await()
    }

    suspend fun removeCourseContent(taskId: String) {
        requireAllowWriteData()
        contentAttachmentStorage.deleteFilesByContentId(taskId)
        submissionAttachmentStorage.deleteFilesByContentId(taskId)
        getContentDocument(taskId)
            .set(
                mapOf(
                    "id" to taskId,
                    "timestamp" to Date(),
                    "deleted" to true
                )
            )
            .await()
    }

    private suspend fun getContentDocument(contentId: String) =
        coursesRef.document(courseContentLocalDataSource.getCourseIdByTaskId(contentId))
            .collection("Contents")
            .document(contentId)

    fun findSectionsByCourseId(courseId: String) =
        sectionLocalDataSource.getByCourseId(courseId)
            .map { it.entitiesToDomains() }

    suspend fun findSection(sectionId: String): Section? {
        return sectionLocalDataSource.get(sectionId)?.entityToUserDomain()
    }

    fun findTaskSubmissionByContentIdAndStudentId(
        taskId: String,
        studentId: String
    ): Flow<Task.Submission> {
        return submissionLocalDataSource.getByTaskIdAndUserId(taskId, studentId)
            .map {
                it?.entityToUserDomain(
                    submissionAttachmentStorage.get(
                        it.submissionEntity.content_id,
                        it.submissionEntity.student_id,
                        it.submissionEntity.attachments
                    )
                ) ?: Task.Submission.createEmpty(
                    contentId = taskId,
                    student = userLocalDataSource.get(studentId)!!.toUserDomain()
                )
            }
            .distinctUntilChanged()
    }

    suspend fun updateSubmissionFromStudent(submission: Task.Submission) {
        requireNetworkAvailable()
        val studentId = submission.student.id
        val contentId = submission.contentId

        val attachmentUrls = submissionAttachmentStorage.update(
            contentId,
            studentId,
            attachments = submission.content.attachments
        )

        val submittedDate = (submission.status as Task.SubmissionStatus.Submitted)
            .submittedDate.toDate()

        val updatedFields = mapOf(
            "submissions.$studentId.text" to submission.content.text,
            "submissions.$studentId.attachments" to attachmentUrls,
            "submissions.$studentId.status" to submissionMapper.domainToStatus(submission),
            "submissions.$studentId.contentUpdateDate" to submission.contentUpdateDate.toDate(),
            "submissions.$studentId.submittedDate" to submittedDate,
            "submittedByStudentIds" to
                    if (submission.submitted)
                        FieldValue.arrayUnion(studentId)
                    else
                        FieldValue.arrayRemove(studentId),
            "notSubmittedByStudentIds" to
                    if (submission.submitted)
                        FieldValue.arrayRemove(studentId)
                    else
                        FieldValue.arrayUnion(studentId)
        )
        coursesRef.document(courseContentLocalDataSource.getCourseIdByTaskId(contentId))
            .collection("Contents")
            .document(contentId)
            .update(mapOfSubmissionFields(contentId, studentId) + updatedFields)
            .await()
    }

    suspend fun gradeSubmission(
        taskId: String,
        studentId: String,
        grade: Int,
        teacherId: String = userPreferences.id
    ) {
        requireNetworkAvailable()
        getContentDocument(taskId).update(
            mapOfSubmissionFields(taskId, studentId)
                    +
                    mapOf(
                        "submissions.$studentId.status" to Task.Submission.Status.GRADED,
                        "submissions.$studentId.gradedDate" to Date(),
                        "submissions.$studentId.grade" to grade,
                        "submissions.$studentId.teacherId" to teacherId,

                        "submittedByStudentIds" to FieldValue.arrayUnion(studentId),
                        "notSubmittedByStudentIds" to FieldValue.arrayRemove(studentId),
                    )
        ).await()
    }

    suspend fun rejectSubmission(
        taskId: String,
        studentId: String,
        cause: String,
        teacherId: String = userPreferences.id
    ) {
        requireNetworkAvailable()
        getContentDocument(taskId).update(
            mapOfSubmissionFields(taskId, studentId)
                    +
                    mapOf(
                        "timestamp" to FieldValue.serverTimestamp(),
                        "submissions.$studentId.status" to Task.Submission.Status.REJECTED,
                        "submissions.$studentId.cause" to cause,
                        "submissions.$studentId.rejectedDate" to Date(),
                        "submissions.$studentId.teacherId" to teacherId,

                        "submittedByStudentIds" to FieldValue.arrayRemove(studentId),
                        "notSubmittedByStudentIds" to FieldValue.arrayUnion(studentId),
                    )
        ).await()
    }

    private suspend fun mapOfSubmissionFields(
        taskId: String,
        studentId: String,
    ) = mapOf(
        "timestamp" to FieldValue.serverTimestamp(),
        "submissions.$studentId.studentId" to studentId,
        "submissions.$studentId.contentId" to taskId,
        "submissions.$studentId.courseId" to courseContentLocalDataSource.getCourseIdByTaskId(taskId),
    )

    suspend fun isCourseTeacher(userId: String, courseId: String): Boolean {
        return courseLocalDataSource.isCourseTeacher(courseId, userId)
    }

    fun findTaskSubmissions(taskId: String): Flow<List<Task.Submission>> {
        return submissionLocalDataSource.getByTaskId(taskId).map { list ->
            list.map {
                it.entityToUserDomain(
                    submissionAttachmentStorage.get(
                        it.submissionEntity.content_id,
                        it.submissionEntity.student_id,
                        it.submissionEntity.attachments
                    )
                )
//                submissionMapper.entityToDomain(it, attachments)
            }
        }
            .mapLatest {
                it + submissionLocalDataSource.getStudentsWithoutSubmission(taskId)
                    .map { userEntity ->
                        Task.Submission.createEmpty(taskId, userEntity.toUserDomain())
                    }
            }
            .distinctUntilChanged()
    }

    suspend fun updateCourseSections(sections: List<Section>) {
        requireAllowWriteData()
        coursesRef.document(sections[0].courseId)
            .update("sections", sections)
            .await()
    }

    suspend fun addSection(section: Section) {
        requireAllowWriteData()
        coursesRef.document(section.courseId)
            .update("sections", FieldValue.arrayUnion(section))
            .await()
    }

    suspend fun removeSection(section: Section) {
        requireAllowWriteData()
        val courseRef = coursesRef.document(section.courseId)

        val contentsWithThisSection = courseRef.collection("Contents")
            .whereEqualTo("sectionId", section.id)
            .get()
            .await()

        val batch = firestore.batch()

        contentsWithThisSection.forEach {
            batch.update(
                courseRef.collection("Contents")
                    .document(it.getString("id")!!), "sectionId", ""
            )
        }

        batch.update(courseRef, "sections", FieldValue.arrayRemove(section))
        batch.commit().await()
    }

    suspend fun updateContentOrder(contentId: String, order: Int) {
        requireAllowWriteData()
        getContentDocument(contentId)
            .update(
                mapOf(
                    "order" to order,
                    timestampFiledPair()
                )
            )
            .await()
    }

    fun findUpcomingTasksForYourGroup(): Flow<List<Task>> {
        contentsRef.whereGreaterThanOrEqualTo(
            "completionDate",
            Date()
        )
            .whereArrayContains(
                "notSubmittedByStudentIds",
                userPreferences.id
            )
            .limit(10)
            .addSnapshotListener { value, error ->
                appDatabase.transaction {
                    coroutineScope.launch {
                        value?.let {
                            val courseContentMaps: List<CourseContentMap> =
                                it.toMaps(::CourseContentMap)
                            saveCourseContentsLocal(courseContentMaps)
                        }
                    }
                }

            }
        return courseContentLocalDataSource.getByGroupIdAndGreaterCompletionDate(
            groupPreferences.groupId
        ).map { it.entitiesToTaskDomains() }
    }

    fun findOverdueTasksForYourGroup(): Flow<List<Task>> {
        return flow {
            val contentsQuerySnapshot =
                contentsRef
                    .whereLessThanOrEqualTo(
                        "completionDate",
                        Date()
                    )
                    .whereArrayContains(
                        "notSubmittedByStudentIds",
                        userPreferences.id
                    )
                    .limit(10)
                    .get()
                    .await()
            saveCourseContentsLocal(
                contentsQuerySnapshot.documents.map { it.toMap(::CourseContentMap) }
            )

            emitAll(courseContentLocalDataSource.getByGroupIdAndNotSubmittedUser(
                groupPreferences.groupId,
                userPreferences.id
            ).map { it.entitiesToTaskDomains() })
        }
    }

    fun findCompletedTasksForYourGroup(): Flow<List<Task>> {
        return flow {
            contentsRef
                .whereArrayContains(
                    "submittedByStudentIds",
                    userPreferences.id
                )
                .limit(10)
                .get()
                .await().apply {
                    saveCourseContentsLocal(
                        documents.map { it.toMap(::CourseContentMap) }
                    )
                }
            emitAll(courseContentLocalDataSource.getByGroupIdAndSubmittedUser(
                groupPreferences.groupId,
                userPreferences.id
            ).map { it.entitiesToTaskDomains() })

        }
    }
}


class SameCoursesException : Exception()

interface RemoveCourseOperation : PreconditionsRepository {

    val firestore: FirebaseFirestore
    val courseMapper: CourseMapper
    val coursesRef: CollectionReference
    val groupsRef: CollectionReference
    val coroutineScope: CoroutineScope
    val dispatcher: CoroutineDispatcher

    suspend fun removeCourse(courseId: String, groupIds: List<String>) {
        requireAllowWriteData()
        val batch = firestore.batch()
        batch.delete(coursesRef.document(courseId))
        groupIds.forEach { groupId ->
            batch.update(
                groupsRef.document(groupId),
                "timestamp",
                FieldValue.serverTimestamp()
            )

            batch.update(
                groupsRef.document(groupId),
                "timestampCourses",
                FieldValue.serverTimestamp()
            )
        }
        coursesRef.document(courseId).collection("Contents").deleteCollection(10)
        batch.commit().await()
    }
}

interface UpdateCourseOperation : PreconditionsRepository {

    val firestore: FirebaseFirestore
    val courseMapper: CourseMapper
    val courseLocalDataSource: CourseLocalDataSource
    val groupsRef: CollectionReference
    val coursesRef: CollectionReference

    suspend fun updateCourse(course: Course) {
        requireAllowWriteData()
        val batch = firestore.batch()
        val courseMap = CourseMap(course.domainToCourseMap())

        val oldCourse = courseLocalDataSource.get(course.id).entityToCourseDomain()
        val oldCourseMap = CourseMap(oldCourse.domainToCourseMap())

        updateGroupsOfCourse(batch, (oldCourseMap.groupIds + courseMap.groupIds))

        batch.update(
            coursesRef.document(courseMap.id),
            FieldsComparator.mapOfDifference(oldCourseMap, courseMap)
        )
        batch.commit().await()
    }

    fun updateGroupsOfCourse(batch: WriteBatch, groupIds: List<String>) {
        groupIds.forEach { groupId ->
            batch.update(
                groupsRef.document(groupId),
                "timestamp",
                FieldValue.serverTimestamp()
            )

            batch.update(
                groupsRef.document(groupId),
                "timestampCourses",
                FieldValue.serverTimestamp()
            )
        }
    }
}

interface SaveCourseRepository {

    val userLocalDataSource: UserLocalDataSource
    val groupLocalDataSource: GroupLocalDataSource
    val courseLocalDataSource: CourseLocalDataSource

    val subjectLocalDataSource: SubjectLocalDataSource

    val sectionLocalDataSource: SectionLocalDataSource
    val groupCourseLocalDataSource: GroupCourseLocalDataSource


    suspend fun saveCourse(courseMap: CourseMap) {

        groupCourseLocalDataSource.deleteByCourse(courseMap.id)

        userLocalDataSource.upsert(UserMap(courseMap.teacher).mapToUserEntity())

        subjectLocalDataSource.upsert(SubjectMap(courseMap.subject).mapToSubjectEntity())

        courseLocalDataSource.upsert(courseMap.mapToEntity())

        sectionLocalDataSource.deleteByCourseId(courseMap.id)

        sectionLocalDataSource.upsert(
            courseMap.sections?.map { SectionMap(it).mapToEntity() } ?: emptyList()
        )

        groupCourseLocalDataSource.upsert(
            courseMap.groupIds
                .filter { groupLocalDataSource.isExist(it) }
                .map { groupId -> GroupCourseEntity(groupId, courseMap.id) }
        )
    }

    suspend fun saveCourses(courseDocs: List<CourseMap>) {
        courseDocs.forEach { saveCourse(it) }
    }
}