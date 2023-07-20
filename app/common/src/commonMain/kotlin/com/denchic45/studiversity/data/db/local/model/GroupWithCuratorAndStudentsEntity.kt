package com.denchic45.studiversity.data.db.local.model

import com.denchic45.studiversity.entity.StudyGroup
import com.denchic45.studiversity.entity.User

class GroupWithCuratorAndStudentsEntity(
    val groupEntity: StudyGroup,
    val curatorEntity: User,
    val studentEntities: List<User>
)