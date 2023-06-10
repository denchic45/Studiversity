package com.denchic45.studiversity.data.db.remote.source

import com.denchic45.studiversity.data.db.remote.model.GroupMap
import com.denchic45.studiversity.data.db.remote.model.SpecialtyMap
import com.denchic45.studiversity.util.*
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

actual class SpecialtyRemoteDataSource @Inject constructor(
    private val firestore: FirebaseFirestore,
) {

    private val specialtiesRef: CollectionReference = firestore.collection("Specialties")
    private val groupsRef = firestore.collection("Groups")

    actual fun findByContainsName(text: String): Flow<List<SpecialtyMap>> {
        return specialtiesRef
            .whereArrayContains("searchKeys", SearchKeysGenerator.formatInput(text))
            .getDataFlow { snapshot -> snapshot.toMaps(::SpecialtyMap) }
    }

    actual suspend fun findById(id: String): SpecialtyMap {
        return specialtiesRef.document(id).get().await().toMap(::SpecialtyMap)
    }

    actual suspend fun add(map: FireMap) {
        specialtiesRef.document(map["id"] as String).set(map).await()
    }

    actual suspend fun update(specialtyMap: FireMap) {
        val id = specialtyMap["id"] as String
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
                batch[specialtiesRef.document(id)] = specialtyMap
                batch.commit().await()
            }
    }

    actual suspend fun remove(specialtyId: String) {
        specialtiesRef.document(specialtyId).delete().await()
    }

    actual fun findAllSpecialties(): Flow<List<SpecialtyMap>> {
        return specialtiesRef.getQuerySnapshotFlow().map { it.toMaps(::SpecialtyMap) }
    }
}