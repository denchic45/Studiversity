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
import com.denchic45.studiversity.data.pref.AppPreferences
import com.denchic45.studiversity.data.pref.TimestampPreferences
import com.denchic45.studiversity.data.pref.UserPreferences
import com.denchic45.studiversity.data.service.NetworkService
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.onSuccess
import com.denchic45.stuiversity.api.course.CoursesApi
import com.denchic45.stuiversity.api.member.MembersApi
import com.denchic45.stuiversity.api.role.RoleApi
import com.denchic45.stuiversity.api.role.model.Role
import com.denchic45.stuiversity.api.studygroup.StudyGroupApi
import com.denchic45.stuiversity.api.studygroup.model.CreateStudyGroupRequest
import com.denchic45.stuiversity.api.studygroup.model.StudyGroupResponse
import com.denchic45.stuiversity.api.studygroup.model.UpdateStudyGroupRequest
import com.denchic45.stuiversity.util.UUIDWrapper
import com.denchic45.stuiversity.util.toUUID
import com.denchic45.stuiversity.util.uuidOf
import com.denchic45.stuiversity.util.uuidOfMe
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.tatarka.inject.annotations.Inject
import java.util.UUID

@Inject
class StudyGroupRepository constructor(
    override val studyGroupLocalDataSource: StudyGroupLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource,
    private val appPreferences: AppPreferences,
    private val timestampPreferences: TimestampPreferences,
    private val userPreferences: UserPreferences,
    override val networkService: NetworkService,
    override val studyGroupCourseLocalDataSource: StudyGroupCourseLocalDataSource,
    override val userLocalDataSource: UserLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val courseTopicLocalDataSource: CourseTopicLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
    private val studyGroupApi: StudyGroupApi,
    private val courseApi: CoursesApi,
    private val membersApi: MembersApi,
    private val roleApi: RoleApi,
) : NetworkServiceOwner, SaveGroupOperation, SaveCourseRepository,
    FindByContainsNameRepository<StudyGroupResponse> {

    override fun findByContainsName(text: String): Flow<Resource<List<StudyGroupResponse>>> {
        return fetchResourceFlow {
            studyGroupApi.search(text)
        }
    }


//    private suspend fun saveYourGroup(group: GroupMap) {
//        saveGroup(group)
//        groupPreferences.saveGroupInfo(group.mapToGroupEntity())
//        timestampPreferences.groupCoursesUpdateTimestamp = group.timestampCourses.time
//    }

//    suspend fun listenGroupsWhereThisUserIsTeacher(teacher: User) {
//        val timestampGroups = timestampPreferences.groupsUpdateTimestamp
//        val teacherId = teacher.id
//        return groupRemoteDataSource.findByTeacherIdAndTimestamp(teacherId, timestampGroups)
//            .collect {
//                saveGroups(it)
//                timestampPreferences.groupsUpdateTimestamp = System.currentTimeMillis()
//            }
//    }

    suspend fun findByCurator(userId: UUID) = fetchResource {
        studyGroupApi.getList(memberId = uuidOf(userId), Role.Curator.id)
    }

//    fun findGroupByCuratorId(userId: String): Flow<Group?> {
//        groupRemoteDataSource.observeByCuratorId(userPreferences.id).onEach {
//            it?.let {
//                if (it.timestampNotNull()) {
//                    saveYourGroup(GroupMap(it))
//                }
//            }
//        }
//        return groupLocalDataSource.observeByCuratorId(userId).map { it.toGroup() }
//    }

//    fun findGroupByCurator(user: User): Flow<Group> {
//        getQueryOfGroupByCurator(user.id)
//            .addSnapshotListener { value: QuerySnapshot?, error: FirebaseFirestoreException? ->
//                if (error != null) {
//                    throw error
//                }
//                if (!value!!.isEmpty && value.timestampsNotNull())
//                    coroutineScope.launch(dispatcher) {
//                        saveGroup(GroupMap(value.documents[0].toMap()))
//                    }
//            }
//        return groupLocalDataSource.getByCuratorId(user.id)
//            .map { it.toGroup() }
//    }

//    suspend fun listenYourGroupById() {
//        when (UserRole.valueOf(userPreferences.role)) {
//            UserRole.STUDENT -> groupRemoteDataSource.observeById(userPreferences.groupId)
//            UserRole.TEACHER -> groupRemoteDataSource.observeByCuratorId(userPreferences.id)
//            else -> emptyFlow()
//        }.filterNotNull().collect { groupMap ->
//            if (groupMap.timestampNotNull()) {
//                saveYourGroup(groupMap)
//            }
//        }
//    }

    suspend fun findBySpecialtyId(specialtyId: UUID): Resource<List<StudyGroupResponse>> =
        fetchResource {
            studyGroupApi.getList(specialtyId = specialtyId)
        }

    fun findBy(
        memberId: UUIDWrapper? = null,
        roleId: Long? = null,
        specialtyId: UUID? = null,
        academicYear: Int? = null,
        query: String? = null,
    ): Flow<Resource<List<StudyGroupResponse>>> = fetchResourceFlow {
        studyGroupApi.getList(memberId, roleId, specialtyId, academicYear, query = query)
    }

//    suspend fun findBySpecialtyId(specialtyId: String): List<GroupHeader> {
//        return groupRemoteDataSource.findBySpecialtyId(specialtyId).mapsToGroupHeaders()
//    }

//    fun observeById(groupId: String): Flow<Group?> = callbackFlow {
//        launch { observeAndSaveGroupFlow(groupId).collect() }
//        groupLocalDataSource.observe(groupId)
//            .distinctUntilChanged()
//            .map { it?.toGroup() }
//            .collect { send(it) }
//    }

    fun findById(studyGroupId: UUID) = fetchResourceFlow {
        studyGroupApi.getById(studyGroupId)
    }

//    private suspend fun observeAndSaveGroupFlow(groupId: String): Flow<GroupMap?> {
//        return groupRemoteDataSource.observeById(groupId).onEach { groupMap ->
//            if (groupMap != null) {
//                if (groupMap.timestampNotNull()) {
//                    saveGroup(groupMap)
//                }
//            } else {
//                groupLocalDataSource.deleteById(groupId)
//            }
//        }
//    }

    suspend fun add(createStudyGroupRequest: CreateStudyGroupRequest) = fetchResource {
        studyGroupApi.create(createStudyGroupRequest)
    }

    suspend fun update(studyGroupId: UUID, updateStudyGroupRequest: UpdateStudyGroupRequest) =
        fetchResource {
            studyGroupApi.update(studyGroupId, updateStudyGroupRequest)
        }

    suspend fun remove(studyGroupId: UUID) = fetchResource {
        studyGroupApi.delete(studyGroupId)
    }


//    fun observeGroupInfoById(groupId: String): Flow<GroupHeader> = flow {
//        coroutineScope {
//            launch {
//                if (!groupLocalDataSource.isExist(groupId)) {
//                    observeAndSaveGroupFlow(groupId).collect()
//                }
//            }
//            emitAll(
//                groupLocalDataSource.getNameById(groupId).filterNotNull()
//                    .map { it.toGroupHeader() }.distinctUntilChanged()
//            )
//        }
//    }

//    fun observeGroupInfoByCuratorId(curatorId: String): Flow<GroupHeader> {
//        return groupLocalDataSource.observeGroupIdByCuratorId(curatorId)
//            .flatMapLatest(::observeGroupInfoById)
//    }


    fun isExistGroup(groupId: String): Flow<Boolean> {
        return studyGroupLocalDataSource.observeIsExist(groupId)
    }

//    suspend fun findGroupsWithCoursesByCourse(course: Int): List<GroupCourses> {
//        val groupMaps = groupRemoteDataSource.findByCourse(course)
//        if (groupMaps.isEmpty()) return emptyList()
//        val courseMaps = courseRemoteDataSource.findByGroupIds(groupMaps.map(GroupMap::id))
//
//        saveGroups(groupMaps)
//        saveCourses(courseMaps)
//
//        return groupMaps.map { groupMap ->
//            GroupCourses(
//                groupMap.toGroupHeader(),
//                courseMaps.filter { it.groupIds.contains(groupMap.id) }.mapsToCourseHeaderDomains()
//            )
//        }
//    }

//    fun findGroupByStudent(user: User): Flow<Group> = flow {
//        if (!userLocalDataSource.isExistByIdAndGroupId(user.id, user.groupId!!)) findById(user.groupId)
//        emitAll(groupLocalDataSource.getByStudentId(user.id).map { it.toGroup() })
//    }

    suspend fun findGroupMembersByGroupId(studyGroupId: UUID) = fetchResource {
        membersApi.getByScope(studyGroupId)
    }

    fun observeIdsByMe() = appPreferences.observeBellSchedule
        .map { Json.decodeFromString<List<String>>(it).map(String::toUUID) }

    // TODO: Make observable
    fun findByMe(): Flow<Resource<List<StudyGroupResponse>>> {
        return fetchResourceFlow { studyGroupApi.getList(memberId = uuidOfMe()) }
            .onEach { resource ->
                resource.onSuccess { responses ->
                    appPreferences.yourStudyGroups =
                        Json.encodeToString(responses.map { it.id.toString() })
                }
            }

    }

    fun findByCourseId(courseId: UUID) = fetchResourceFlow {
        courseApi.getStudyGroups(courseId)
    }

    fun addToCourse(courseId: UUID, studyGroupId: UUID) = fetchResourceFlow {
        courseApi.putStudyGroup(courseId, studyGroupId)
    }

    fun removeFromCourse(studyGroupId: UUID, courseId: UUID) = fetchResourceFlow {
        courseApi.deleteStudyGroup(courseId, studyGroupId)
    }
}