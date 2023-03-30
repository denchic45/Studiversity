package com.denchic45.kts.ui.adminPanel.finder

import com.denchic45.kts.domain.Interactor
import com.denchic45.kts.domain.model.Subject
import com.denchic45.kts.domain.model.User
import com.denchic45.kts.domain.model.User.Companion.isStudent
import com.denchic45.kts.domain.model.User.Companion.isTeacher
import com.denchic45.kts.data.repository.*
import javax.inject.Inject

class FinderInteractor @Inject constructor(
    private val subjectRepository: SubjectRepository,
    private val userRepository: UserRepository,
    private val studyGroupRepository: StudyGroupRepository,
    private val specialtyRepository: SpecialtyRepository
) : Interactor {

    override fun removeListeners() {
        userRepository.removeListeners()
        studyGroupRepository.removeListeners()
        subjectRepository.removeListeners()
        specialtyRepository.removeListeners()
    }

   suspend fun removeUser(user: User) {
        userRepository.remove(user.id)
    }

    suspend fun removeSubject(subject: Subjec) {
        return subjectRepository.remove(subject)
    }
}