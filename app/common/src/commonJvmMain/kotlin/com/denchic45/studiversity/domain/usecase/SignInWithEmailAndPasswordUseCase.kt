package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.service.AuthService
import com.denchic45.studiversity.data.service.UserService
import com.denchic45.studiversity.domain.EmptyResource
import com.denchic45.studiversity.domain.Resource
import com.denchic45.studiversity.domain.emptyResource
import com.denchic45.studiversity.domain.mapResource
import com.denchic45.stuiversity.api.auth.model.SignInResponse
import me.tatarka.inject.annotations.Inject

@Inject
class SignInWithEmailAndPasswordUseCase(
    private val authService: AuthService,
    private val userService: UserService,
) {

    suspend operator fun invoke(email: String, password: String): EmptyResource {
        return authService.signInByEmailPassword(email, password).mapResource { emptyResource() }
//            .mapResource {
//                userService.findMe()
//            }
    }
}