package com.acon.core.data.session

import com.acon.acon.core.analytics.amplitude.AconAmplitude
import com.acon.acon.core.common.IODispatcher
import com.acon.acon.core.model.type.SignInStatus
import com.acon.core.data.datasource.local.TokenLocalDataSource
import com.acon.core.data.dto.response.SignInResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

interface SessionHandler {
    suspend fun clearSession()
    suspend fun completeSignIn(accessToken: String, refreshToken: String)
    fun getUserType(): Flow<SignInStatus>

    suspend fun onSignInResponse(response: SignInResponse)
}

class SessionHandlerImpl @Inject constructor(
    private val tokenLocalDataSource: TokenLocalDataSource,
    @IODispatcher scope: CoroutineScope
) : SessionHandler {

    private val _signInStatus = MutableStateFlow(SignInStatus.GUEST)
    private val userType = _signInStatus.asStateFlow()

    init {
        scope.launch {
            val accessToken = tokenLocalDataSource.getAccessToken()
            if (accessToken.isNullOrEmpty())
                _signInStatus.emit(SignInStatus.GUEST)
            else
                _signInStatus.emit(SignInStatus.USER)
        }
    }

    override fun getUserType(): Flow<SignInStatus> {
        return userType
    }

    override suspend fun clearSession() {
        tokenLocalDataSource.removeAllTokens()
        _signInStatus.value = SignInStatus.GUEST
        AconAmplitude.clearUserId()
    }

    override suspend fun completeSignIn(accessToken: String, refreshToken: String) {
        _signInStatus.value = SignInStatus.USER
        tokenLocalDataSource.saveAccessToken(accessToken)
        tokenLocalDataSource.saveRefreshToken(refreshToken)
    }

    override suspend fun onSignInResponse(response: SignInResponse) {
        val accessToken = response.accessToken ?: throw IllegalStateException("Access token is null")
        val refreshToken = response.refreshToken ?: throw IllegalStateException("Refresh token is null")
        tokenLocalDataSource.saveAccessToken(accessToken)
        tokenLocalDataSource.saveRefreshToken(refreshToken)
    }
}
