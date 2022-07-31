package com.denchic45.kts.data.repository

import com.denchic45.kts.data.local.db.SpecialtyLocalDataSource
import com.denchic45.kts.data.mapper.*
import com.denchic45.kts.data.remote.model.GroupMap
import com.denchic45.kts.data.remote.model.SpecialtyMap
import com.denchic45.kts.data.service.AppVersionService
import com.denchic45.kts.data.service.NetworkService
import com.denchic45.kts.di.modules.IoDispatcher
import com.denchic45.kts.domain.model.Specialty
import com.denchic45.kts.util.SearchKeysGenerator
import com.denchic45.kts.util.getDataFlow
import com.denchic45.kts.util.toMaps
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SpecialtyRepository @Inject constructor(
    override val appVersionService: AppVersionService,
    private val coroutineScope: CoroutineScope,
    @IoDispatcher private val dispatcher: CoroutineDispatcher,
    private val specialtyLocalDataSource: SpecialtyLocalDataSource,
    private val firestore: FirebaseFirestore,
    override val networkService: NetworkService,
) : Repository(), FindByContainsNameRepository<Specialty> {

    override fun findByContainsName(text: String): Flow<List<Specialty>> {
        return specialtyRef
            .whereArrayContains("searchKeys", SearchKeysGenerator.formatInput(text))
            .getDataFlow { snapshot -> snapshot.toMaps(::SpecialtyMap) }
            .map { maps ->
                specialtyLocalDataSource.upsert(maps.mapsToSpecialEntities())
                maps.mapsToDomains()
            }
    }

    private val groupsRef: CollectionReference = firestore.collection("Groups")
    private val specialtyRef: CollectionReference = firestore.collection("Specialties")

    fun observe(id: String): Flow<Specialty?> {
        coroutineScope.launch {
            specialtyRef.document(id)
                .get()
                .await().apply {
                    coroutineScope.launch(dispatcher) {
                        specialtyLocalDataSource.upsert(
                            SpecialtyMap(data!!).mapToSpecialtyEntity()
                        )
                    }
                }
        }
        return specialtyLocalDataSource.observe(id).map { entity ->
            entity?.let { entity.toDomain() }
        }
    }

    suspend fun add(specialty: Specialty) {
        requireAllowWriteData()
        specialtyRef.document(specialty.id).set(specialty.toMap()).await()
    }

    suspend fun update(specialty: Specialty) {
        requireAllowWriteData()
        val specialtyMap = specialty.toMap()
        val id = specialty.id
        groupsRef.whereEqualTo("specialty.id", id)
            .get()
            .await().apply {
                val batch = firestore.batch()
                if (!this.isEmpty)
                    for (groupDoc in toMaps(::GroupMap)) {
                        batch.update(
                            groupsRef.document(groupDoc.id), mapOf(
                                "specialty" to specialtyMap,
                                "timestamp" to FieldValue.serverTimestamp()
                            )
                        )
                    }
                batch[specialtyRef.document(id)] = specialtyMap
                batch.commit().await()
            }
    }

    suspend fun remove(specialty: Specialty) {
        requireAllowWriteData()
        specialtyRef.document(specialty.id).delete()
            .await()
    }

}