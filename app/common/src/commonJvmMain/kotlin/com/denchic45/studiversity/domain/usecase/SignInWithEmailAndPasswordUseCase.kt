package com.denchic45.studiversity.domain.usecase

import com.denchic45.studiversity.data.service.AuthService
import com.denchic45.studiversity.data.service.UserService
import com.denchic45.studiversity.domain.resource.EmptyResource
import com.denchic45.studiversity.domain.resource.emptyResource
import com.denchic45.studiversity.domain.resource.mapResource
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