package com.denchic45.studiversity.data.remote.db

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.WriteBatch

interface UpdateGroupsOfCourse {

    val groupsRef: CollectionReference

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