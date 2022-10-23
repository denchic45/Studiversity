package com.denchic45.kts.data.db.remote.source

import com.denchic45.kts.data.db.remote.model.DayMap
import com.denchic45.kts.util.getQuerySnapshotFlow
import com.denchic45.kts.util.toDateUTC
import com.denchic45.kts.util.toMutableMap
import com.denchic45.kts.util.toMutableMaps
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

actual class EventRemoteDataSource @Inject constructor(
    firestore: FirebaseFirestore,
) {

    private val groupsRef = firestore.collection("Groups")
    private val daysRef: Query = firestore.collectionGroup("Days")

    actual fun observeEventsOfGroupByDate(groupId: String, date: LocalDate): Flow<DayMap?> {
        return groupsRef
            .document(groupId)
            .collection("Days")
            .whereEqualTo("date", date.toDateUTC())
            .getQuerySnapshotFlow()
            .map {
                if (it.isEmpty) null
                else it.documents[0].toMutableMap(::DayMap)
            }
    }

    actual suspend fun findEventsOfGroupByDate(groupId: String, date: LocalDate): DayMap {
        return groupsRef
            .document(groupId)
            .collection("Days")
            .whereEqualTo("date", date.toDateUTC())
            .get()
            .await()
            .documents[0].toMutableMap(::DayMap)
    }

    actual fun observeEventsOfGroupByPreviousAndNextDates(
        groupId: String,
        previousMonday: Date,
        nextSaturday: Date,
    ): Flow<List<DayMap>> {
        return daysRef.whereGreaterThanOrEqualTo("date", previousMonday)
            .whereLessThanOrEqualTo("date", nextSaturday)
            .whereEqualTo("groupId", groupId)
            .getQuerySnapshotFlow()
            .map { it.toMutableMaps(::DayMap) }
    }

    actual suspend fun findEventsOfGroupByDateRange(
        groupId: String,
        previousMonday: Date,
        nextSaturday: Date,
    ): List<DayMap> {
        return daysRef.whereGreaterThanOrEqualTo("date", previousMonday)
            .whereLessThanOrEqualTo("date", nextSaturday)
            .whereEqualTo("groupId", groupId)
            .get().await()
            .toMutableMaps(::DayMap)
    }

    actual suspend fun setDay(dayMap: DayMap) {
        val dayRef = groupsRef.document(dayMap.groupId).collection("Days")
        dayRef.document(dayMap.id).set(dayMap, SetOptions.merge()).await()
    }

    actual fun observeEventsOfTeacherByDate(
        teacherId: String,
        date: LocalDate,
    ): Flow<List<DayMap>> {
        return daysRef
            .whereArrayContains("teacherIds", teacherId)
            .whereEqualTo("date", date.toDateUTC())
            .getQuerySnapshotFlow()
            .map { it.toMutableMaps(::DayMap) }
    }

    actual suspend fun updateEventsOfDay(dayMap: DayMap) {
        groupsRef.document(dayMap.groupId).collection("Days").document(dayMap.id)
            .update(
                "events",
                dayMap.events,
                "timestamp",
                FieldValue.serverTimestamp()
            ).await()
    }
}