package com.denchic45.kts.data.repository

import android.net.Uri
import androidx.room.withTransaction
import com.denchic45.kts.SubjectEntity
import com.denchic45.kts.data.database.DataBase
import com.denchic45.kts.data.local.db.*
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.model.mapper.CourseMapper
import com.denchic45.kts.data.model.mapper.SubjectMapper
import com.denchic45.kts.data.remote.model.CourseMap
import com.denchic45.kts.data.remote.model.SubjectMap
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.di.modules.IoDispatcher
import com.denchic45.kts.domain.model.Subject
import com.denchic45.kts.util.getDataFlow
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.*
import javax.inject.Inject

class SubjectRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    override val coroutineScope: CoroutineScope,
    override val networkService: NetworkService,
    override val firestore: FirebaseFirestore,
    override val courseMapper: CourseMapper,
    val subjectMapper: SubjectMapper,
    private val dataBase: DataBase,
    @IoDispatcher override val dispatcher: CoroutineDispatcher,
    override val userLocalDataSource: UserLocalDataSource,
    override val groupLocalDataSource: GroupLocalDataSource,
    override val courseLocalDataSource: CourseLocalDataSource,
    override val sectionLocalDataSource: SectionLocalDataSource,
    override val groupCourseLocalDataSource: GroupCourseLocalDataSource,
    override val subjectLocalDataSource: SubjectLocalDataSource,
) : Repository(), SaveCourseRepository, RemoveCourseOperation,
    FindByContainsNameRepository<Subject>, UpdateCourseOperation {

    private val subjectsRef: CollectionReference = firestore.collection("Subjects")
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    override val groupsRef: CollectionReference = firestore.collection("Groups")
    override val coursesRef: CollectionReference = firestore.collection("Courses")

    suspend fun add(subject: Subject) {
        requireAllowWriteData()
        isExistWithSameIconAndColor(subject)
        subjectsRef.document(subject.id)
            .set(subjectMapper.domainToDoc(subject))
            .await()
    }

    suspend fun update(subject: Subject) {
        requireAllowWriteData()
        isExistWithSameIconAndColor(subject)
        val subjectDoc = subjectMapper.domainToDoc(subject)
        val batch = firestore.batch()

        batch[subjectsRef.document(subject.id)] = subjectDoc
        coursesRef.whereEqualTo("subject.id", subject.id).get().await().forEach { docSnapshot ->
            @Suppress("UNCHECKED_CAST")
            updateGroupsOfCourse(batch, (docSnapshot.get("groupIds") as List<String>))
            batch.update(
                coursesRef.document(docSnapshot.id),
                "subject",
                subjectDoc,
                "timestamp",
                FieldValue.serverTimestamp()
            )
        }
        batch.commit().await()
    }

    private suspend fun isExistWithSameIconAndColor(subject: Subject) {
        val snapshot = subjectsRef
            .whereNotEqualTo("id", subject.id)
            .whereEqualTo("iconUrl", subject.iconUrl)
            .whereEqualTo("colorName", subject.colorName)
            .get()
            .await()
        if (!snapshot.isEmpty) {
            throw SameSubjectIconException()
        }
    }

    suspend fun remove(subject: Subject) {
        requireAllowWriteData()
        subjectsRef.document(subject.id).delete().await()
        coursesRef.whereEqualTo("subject.id", subject.id)
            .get()
            .await()
            .forEach {
                val courseMap = it.toObject(CourseMap::class.java)
                removeCourse(courseMap.id, courseMap.groupIds)
            }

    }

    fun find(id: String): Flow<Subject?> {
        subjectsRef.document(id)
            .addSnapshotListener { value, error ->
                coroutineScope.launch(dispatcher) {
                    value?.let {
                        if (value.exists())
                            saveSubject(SubjectMap(value.data!!).mapToSubjectEntity())
                    }
                }
            }

        return subjectLocalDataSource.observe(id).map { it?.entityToSubjectDomain() }
    }

    private suspend fun saveSubject(subjectEntity: SubjectEntity) {
        subjectLocalDataSource.upsert(subjectEntity)
    }

//    fun findByTypedName(subjectName: String): Flow<List<Subject>> = callbackFlow {
//        val addSnapshotListener =
//            subjectsRef.whereArrayContains("searchKeys", subjectName.lowercase(Locale.getDefault()))
//                .addSnapshotListener { value: QuerySnapshot?, _ ->
//                    val subjects = value!!.toObjects(Subject::class.java)
//                    trySend(subjects)
//                    launch(dispatcher) {
//                        subjectLocalDataSource.upsert(subjectMapper.domainToEntity(subjects))
//                    }
//                }
//        awaitClose { addSnapshotListener.remove() }
//    }

    override fun findByContainsName(text: String): Flow<List<Subject>> {
        return subjectsRef.whereArrayContains("searchKeys", text.lowercase(Locale.getDefault()))
            .getDataFlow { snapshot -> snapshot.documents.map { SubjectMap(it.data!!) } }
            .map { subjectMaps ->
                coroutineScope.launch {
                    subjectLocalDataSource.upsert(subjectMaps.mapsToSubjectEntities())
                }
                subjectMaps.mapsToSubjectDomains()
            }
    }

    suspend fun findAllRefsOfSubjectIcons(): List<Uri> {
        return storage.getReference("subjects")
            .listAll()
            .await()
            .run {
                items.map { it.downloadUrl.await() }
            }
    }

    fun findByGroup(groupId: String): Flow<List<Subject>> {
        coroutineScope.launch {
            requireAllowWriteData()
            coursesRef.whereArrayContains("groupIds", groupId)
                .get()
                .await().apply {
                    dataBase.withTransaction {
                        saveCourses(toObjects(CourseMap::class.java))
                    }
                }
        }
        return subjectLocalDataSource.observeByGroupId(groupId)
            .map { it.entitiesToSubjectDomains() }
    }

}