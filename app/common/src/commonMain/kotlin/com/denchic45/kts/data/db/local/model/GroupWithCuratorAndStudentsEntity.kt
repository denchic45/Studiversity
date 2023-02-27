package com.denchic45.kts.data.db.local.model

import com.denchic45.kts.StudyGroupEntity
import com.denchic45.kts.UserEntity

class GroupWithCuratorAndStudentsEntity(
    val groupEntity: StudyGroupEntity,
    val curatorEntity: UserEntity,
    val studentEntities: List<UserEntity>
)