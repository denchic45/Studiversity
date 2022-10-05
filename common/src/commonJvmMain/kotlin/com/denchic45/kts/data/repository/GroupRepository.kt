package com.denchic45.kts.data.repository

import com.denchic45.kts.data.db.local.source.*
import com.denchic45.kts.data.db.remote.model.GroupMap
import com.denchic45.kts.data.db.remote.source.CourseRemoteDataSource
import com.denchic45.kts.data.db.remote.source.GroupRemoteDataSource
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.pref.GroupPreferences
import com.denchic45.kts.data.pref.TimestampPreferences
import com.denchic45.kts.data.pref.UserPreferences
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.domain.model.*
import com.denchic45.kts.util.timestampNotNull
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@me.tatarka.inject.annotations.Inject
class GroupRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val specialtyLocalDataSource: SpecialtyLocalDataSource,
    private val timestampPreferences: TimestampPreferences,
    private val groupPreferences: GroupPreferences,
    private val userPreferences: UserPreferences,
    override val networkService: NetworkService,
    override val groupCourseLocalDataSource: GroupCourseLocalDataSource,
    override val userLocalDataSource: UserLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val sectionLocalDataSource: SectionLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
    private val groupRemoteDataSource: GroupRemoteDataSource,
    private val courseRemoteDataSource: CourseRemoteDataSource,
) : Repository(), SaveGroupOperation, SaveCourseRepository,
    FindByContainsNameRepository<GroupHeader> {

    override fun findByContainsName(text: String): Flow<List<GroupHeader>> {
        return groupRemoteDataSource.findByContainsName(text)
            .filter { groupMaps -> groupMaps.all { it.map.timestampNotNull() } }.map { groupDocs ->
                for (groupDoc in groupDocs) {
                    saveGroup(groupDoc)
                }
                groupDocs.mapsToGroupHeaders()
            }
    }

    private suspend fun saveYourGroup(group: GroupMap) {
        saveGroup(group)
        groupPreferences.saveGroupInfo(group.mapToGroupEntity())
        timestampPreferences.groupCoursesUpdateTimestamp = group.timestampCourses.time
    }

    suspend fun listenGroupsWhereThisUserIsTeacher(teacher: User) {
        val timestampGroups = timestampPreferences.groupsUpdateTimestamp
        val teacherId = teacher.id
        return groupRemoteDataSource.findByTeacherIdAndTimestamp(teacherId, timestampGroups)
            .collect {
                saveGroups(it)
                timestampPreferences.groupsUpdateTimestamp = System.currentTimeMillis()
            }
    }

    fun findGroupByCuratorId(userId: String): Flow<Group?> {
        groupRemoteDataSource.observeByCuratorId(userPreferences.id).onEach {
            it?.let {
                if (it.map.timestampNotNull()) {
                    saveYourGroup(GroupMap(it.map))
                }
            }
        }
        return groupLocalDataSource.observeByCuratorId(userId).map { it.toGroup() }
    }

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

    suspend fun listenYourGroupById() {
        when (UserRole.valueOf(userPreferences.role)) {
            UserRole.STUDENT -> groupRemoteDataSource.observeById(userPreferences.groupId)
            UserRole.TEACHER -> groupRemoteDataSource.observeByCuratorId(userPreferences.id)
            else -> emptyFlow()
        }.filterNotNull().collect { groupMap ->
            if (groupMap.map.timestampNotNull()) {
                saveYourGroup(groupMap)
            }
        }
    }

    suspend fun findBySpecialtyId(specialtyId: String): List<GroupHeader> {
        return groupRemoteDataSource.findBySpecialtyId(specialtyId).mapsToGroupHeaders()
    }

    val yourGroupId: String
        get() = groupPreferences.groupId

    val yourGroupName: String
        get() = groupPreferences.groupName

//    fun observeById(groupId: String): Flow<Group?> = callbackFlow {
//        launch { observeAndSaveGroupFlow(groupId).collect() }
//        groupLocalDataSource.observe(groupId)
//            .distinctUntilChanged()
//            .map { it?.toGroup() }
//            .collect { send(it) }
//    }

    fun observeById(groupId: String): Flow<Group?> = flow {
        coroutineScope {
            launch { observeAndSaveGroupFlow(groupId).collect() }
            emitAll(groupLocalDataSource.observe(groupId).distinctUntilChanged()
                .map { it?.toGroup() })
        }
    }

    suspend fun find(groupId: String): Group? {
        groupLocalDataSource.upsert(groupRemoteDataSource.findById(groupId).mapToGroupEntity())
        return groupLocalDataSource.get(groupId)?.toGroup()
    }

    private suspend fun observeAndSaveGroupFlow(groupId: String): Flow<GroupMap?> {
        return groupRemoteDataSource.observeById(groupId).onEach { groupMap ->
            if (groupMap != null) {
                if (groupMap.map.timestampNotNull()) {
                    saveGroup(groupMap)
                }
            } else {
                groupLocalDataSource.deleteById(groupId)
            }
        }
    }

    suspend fun add(group: Group) {
        requireAllowWriteData()
        groupRemoteDataSource.add(group.toMap().toMutableMap())
    }

    suspend fun update(group: Group) {
        requireAllowWriteData()
        groupRemoteDataSource.update(group.toMap().toMutableMap())
    }

    suspend fun remove(groupId: String) {
        requireAllowWriteData()
        val studentIds: Set<String> = groupRemoteDataSource.findById(groupId).students.keys

        val groupCourseIds: List<String> = groupRemoteDataSource.findCoursesByGroupId(groupId)

        groupRemoteDataSource.removeGroupAndRemoveStudentsAndRemoveGroupIdInCourses(groupId,
            studentIds,
            groupCourseIds)
    }

    suspend fun updateGroupCurator(groupId: String, teacher: User) {
        requireAllowWriteData()
        groupRemoteDataSource.updateGroupCurator(groupId, teacher.toMap())
    }

    fun getNameByGroupId(groupId: String): Flow<String> = flow {
        coroutineScope {
            launch {
                if (!groupLocalDataSource.isExist(groupId)) {
                    observeAndSaveGroupFlow(groupId).collect()
                }
            }
            emitAll(groupLocalDataSource.getNameById(groupId).filterNotNull()
                .distinctUntilChanged())
        }
    }

    fun observeGroupNameByCuratorId(curatorId: String): Flow<String> {
        return groupLocalDataSource.observeGroupIdByCuratorId(curatorId)
            .flatMapLatest(::getNameByGroupId)
    }


    fun isExistGroup(groupId: String): Flow<Boolean> {
        return groupLocalDataSource.observeIsExist(groupId)
    }

    suspend fun findGroupsWithCoursesByCourse(course: Int): List<GroupCourses> {
        val groupMaps = groupRemoteDataSource.findByCourse(course)
        if (groupMaps.isEmpty()) return emptyList()
        val courseMaps = courseRemoteDataSource.findByGroupIds(groupMaps.map(GroupMap::id))

        saveGroups(groupMaps)
        saveCourses(courseMaps)

        return groupMaps.map { groupMap ->
            GroupCourses(groupMap.toGroupHeader(),
                courseMaps.filter { it.groupIds.contains(groupMap.id) }.mapsToCourseHeaderDomains())
        }
    }

    fun observeHasGroup(): Flow<Boolean> {
        return groupPreferences.observeGroupId.map(String::isNotEmpty).distinctUntilChanged()
    }

    fun findGroupByStudent(user: User): Flow<Group> = flow {
        if (!userLocalDataSource.isExistByIdAndGroupId(user.id, user.groupId!!)) find(user.groupId)

        emitAll(groupLocalDataSource.getByStudentId(user.id).map { it.toGroup() })
    }

    fun findGroupMembersByGroupId(groupId: String): Flow<GroupMembers> {
        return userLocalDataSource.observeStudentsWithCuratorByGroupId(groupId)
            .filter { it.isNotEmpty() }.map { it.toGroupMembers() }
    }

    suspend fun setHeadman(studentId: String, groupId: String) {
        groupRemoteDataSource.setHeadman(studentId, groupId)
    }

    suspend fun removeHeadman(groupId: String) {
        groupRemoteDataSource.removeHeadman(groupId)
    }
}