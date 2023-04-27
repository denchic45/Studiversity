package com.denchic45.kts.ui.chooser

import com.arkivanov.decompose.ComponentContext
import com.denchic45.kts.domain.Resource
import com.denchic45.kts.domain.usecase.FindSubjectByContainsNameUseCase
import com.denchic45.kts.domain.usecase.FindUserByContainsNameUseCase
import com.denchic45.kts.ui.chooser.ChooserComponent
import com.denchic45.stuiversity.api.course.subject.model.SubjectResponse
import com.denchic45.stuiversity.api.user.model.UserResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class UserChooserComponent(
    private val findUserByContainsNameUseCase: FindUserByContainsNameUseCase,
    @Assisted
    override val onFinish: (UserResponse?) -> Unit,
    @Assisted
    val componentContext: ComponentContext,
) : ChooserComponent<UserResponse>(componentContext) {
    override fun search(query: String): Flow<Resource<List<UserResponse>>> {
        return flow { emit(findUserByContainsNameUseCase(query)) }
    }
}