package com.denchic45.studiversity.feature.course.subject.usecase

import com.denchic45.studiversity.feature.course.subject.SubjectIconStorage
import java.io.InputStream

class FindSubjectIconUseCase(
    private val subjectIconStorage: SubjectIconStorage
) {
    operator fun invoke(iconName: String): InputStream {
        return subjectIconStorage.getStreamByName(iconName)
    }
}