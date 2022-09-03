package com.denchic45.kts.data.model.room

import androidx.room.*
import com.denchic45.kts.data.domain.model.UserRole
import com.denchic45.kts.data.domain.model.EntityModel
import java.util.*

@Entity(tableName = "user")
data class UserEntity(
    @PrimaryKey
    @ColumnInfo(name = "user_id")
    val id: String,
    val firstName: String,
    val surname: String,
    @ColumnInfo(name = "user_group_id")
    val groupId: String? = null,
    val role: UserRole,
    val email: String? = null,
    val photoUrl: String,
    val gender: Int = 0,
    val admin: Boolean = false,
    val patronymic: String? = null,
    val generatedAvatar: Boolean = false,
    @ColumnInfo(name = "user_timestamp")
    @field:TypeConverters(TimestampConverter::class) val timestamp: Date
) : EntityModel {

    @get:Ignore
    val fullName: String
        get() = firstName + surname
}