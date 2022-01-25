package com.denchic45.kts.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.TypeConverters
import com.denchic45.kts.data.model.room.DateConverter
import com.denchic45.kts.data.model.room.DayEntity
import java.util.*

@Dao
abstract class DayDao : BaseDao<DayEntity>() {
    @Query("SELECT * FROM day WHERE day_id=:id")
    abstract operator fun get(id: String?): DayEntity

    @Query("SELECT day_id FROM day WHERE date =:date AND group_id =:groupId")
    abstract suspend fun getIdByDateAndGroupId(
        @TypeConverters(DateConverter::class) date: Date,
        groupId: String
    ): String?
}