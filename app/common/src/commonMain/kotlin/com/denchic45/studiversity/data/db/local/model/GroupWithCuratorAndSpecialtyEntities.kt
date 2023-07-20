package com.denchic45.studiversity.data.db.local.model

import com.denchic45.studiversity.entity.Specialty
import com.denchic45.studiversity.entity.StudyGroup
import com.denchic45.studiversity.entity.User

class GroupWithCuratorAndSpecialtyEntities(
    val studyGroup: StudyGroup,
    val curatorEntity: User,
    val Specialty: Specialty
)