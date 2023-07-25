package com.denchic45.studiversity.ui.auth

import com.arkivanov.decompose.ComponentContext
import com.denchic45.studiversity.data.preference.AppPreferences
import com.denchic45.studiversity.domain.resource.Cause
import com.denchic45.studiversity.domain.resource.ClientError
import com.denchic45.studiversity.domain.resource.Forbidden
import com.denchic45.studiversity.domain.resource.NoConnection
import com.denchic45.studiversity.domain.resource.NotFound
import com.denchic45.studiversity.domain.resource.ServerError
import com.denchic45.studiversity.domain.resource.onFailure
import com.denchic45.studiversity.domain.resource.onSuccess
import com.denchic45.studiversity.domain.usecase.CheckDomainUseCase
import com.denchic45.studiversity.util.componentScope
import com.denchic45.stuiversity.api.OrganizationResponse
import io.ktor.client.call.NoTransformationFoundException
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import java.net.ConnectException

@Inject
class WelcomeComponent(
    private val appPreferences: AppPreferences,
    private val checkDomainUseCase: CheckDomainUseCase,
    @Assisted
    private val onSuccess: () -> Unit,
    @Assisted
    componentContext: ComponentContext,
) : ComponentContext by componentContext {
    private val componentScope = componentScope()

    val url = MutableStateFlow("")
    val state = MutableStateFlow<State>(State.Idle)


    fun onDomainType(text: String) {
        url.value = text
    }

    fun onCheckDomainClick() {
        componentScope.launch {
            state.value = State.Loading
            checkDomainUseCase(url.value).onSuccess {
                state.value = State.Success(it.organization)
                appPreferences.url = url.value
                withContext(Dispatchers.Main) {
                    onSuccess()
                }
            }.onFailure {
                when (val failure = it) {
                    is Cause -> {
                        state.value = when (failure.throwable) {
                            is NoTransformationFoundException -> State.WrongDomain
                            is ConnectTimeoutException -> State.Timeout
                            is ConnectException -> State.FailedConnect
                            else -> State.UnknownError
                        }
                    }

                    NoConnection -> state.value = State.NoConnection
                    Forbidden,
                    is ClientError,
                    NotFound,
                    ServerError,
                    -> state.value = State.UnknownError
                }
            }
        }
    }


    sealed class State(val isError: Boolean = false) {

        object Idle : State()
        object Loading : State()
        data class Success(private val organization: OrganizationResponse) : State()
        object NoConnection : State(true)
        object FailedConnect : State(true)
        object WrongDomain : State(true)
        object Timeout : State(true)
        object UnknownError : State(true)
    }
}