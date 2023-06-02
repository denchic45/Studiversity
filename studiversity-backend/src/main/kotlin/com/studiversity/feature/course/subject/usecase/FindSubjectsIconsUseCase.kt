package com.studiversity.feature.course.subject.usecase

import com.studiversity.feature.course.subject.SubjectRepository

class FindSubjectsIconsUseCase(private val subjectRepository: SubjectRepository) {

    suspend operator fun invoke(): List<String> {
        return subjectRepository.findIconsUrls()
    }
}