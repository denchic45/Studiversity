package com.denchic45.kts.data.model.room

import androidx.room.*
import com.denchic45.kts.data.domain.model.EventType
import com.denchic45.kts.data.domain.model.EntityModel

@Entity(
    tableName = "event", foreignKeys = [
        ForeignKey(
            entity = DayEntity::class,
            parentColumns = ["day_id"],
            childColumns = ["day_id"],
            onDelete = ForeignKey.CASCADE,
            onUpdate = ForeignKey.CASCADE
        )
    ]
)
data class EventEntity(
    @PrimaryKey
    @ColumnInfo(name = "event_id")
    var id: String,
    @ColumnInfo(name = "day_id")
    var dayId: String,
    var position:Int,
    var room: String?,
    @ColumnInfo(name = "subject_id")
    var subjectId: String?,
    @field:TypeConverters(ListConverter::class)
    @ColumnInfo(name = "teacher_ids")
    var teacherIds: List<String>?,
    @ColumnInfo(name = "group_id")
    var groupId: String,
    var eventType: EventType,
    var name: String?,
    @ColumnInfo(name = "icon_url")
    var iconUrl: String?,
    var color: String?,
) : EntityModel {

}

