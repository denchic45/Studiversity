package com.denchic45.kts.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.TypeConverters
import com.denchic45.kts.data.model.room.DateConverter
import com.denchic45.kts.data.model.room.EventEntity
import com.denchic45.kts.data.model.room.EventTaskSubjectTeachersEntities
import kotlinx.coroutines.flow.Flow
import java.util.*

@Dao
abstract class LessonDao : BaseDao<EventEntity>() {
    @Query("SELECT * FROM event WHERE event_id=:id")
    abstract fun get(id: String): EventEntity?

    @Transaction
    @Query("SELECT * FROM event WHERE date=:date AND group_id =:groupId ORDER BY `order`")
    abstract fun getLessonWithHomeWorkWithSubjectByDateAndGroupId(
        @TypeConverters(
            DateConverter::class
        ) date: Date, groupId: String
    ): Flow<List<EventTaskSubjectTeachersEntities>>

    @Transaction
    @Query("SELECT * FROM event l JOIN teacher_event tl ON l.event_id == tl.event_id WHERE date=:date AND tl.user_id =:teacherId ORDER BY `order`")
    abstract fun getLessonWithHomeWorkWithSubjectByDateAndTeacherId(
        @TypeConverters(
            DateConverter::class
        ) date: Date, teacherId: String
    ): Flow<List<EventTaskSubjectTeachersEntities>>

    @Query("DELETE FROM event WHERE date BETWEEN :start AND :end AND group_id =:groupId")
    abstract fun deleteByGroupAndDateRange(
        groupId: String, @TypeConverters(
            DateConverter::class
        ) start: Date, @TypeConverters(DateConverter::class) end: Date
    )

    @Query("DELETE FROM event WHERE date =:date AND group_id =:groupId")
    abstract fun deleteByDateAndGroup(
        @TypeConverters(
            DateConverter::class
        ) date: Date, groupId: String
    )

    @Transaction
    open suspend fun replaceByDateAndGroup(
        lessonEntities: List<EventEntity>,
        date: Date,
        groupId: String
    ) {
        deleteByDateAndGroup(date, groupId)
        insert(lessonEntities)
    }
}