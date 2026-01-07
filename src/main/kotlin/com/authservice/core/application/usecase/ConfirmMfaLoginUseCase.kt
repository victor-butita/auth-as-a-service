package com.authservice.core.application.usecase

import com.authservice.core.application.dto.LoginResponse
import com.authservice.core.application.dto.MfaVerifyRequest
import com.authservice.core.application.dto.UserResponse
import com.authservice.core.domain.repository.UserMfaRepository
import com.authservice.core.domain.repository.UserRepository
import com.authservice.core.domain.service.MfaService
import com.authservice.core.domain.service.TokenService
import org.springframework.stereotype.Service

@Service
class ConfirmMfaLoginUseCase(
    private val userRepository: UserRepository,
    private val userMfaRepository: UserMfaRepository,
    private val mfaService: MfaService,
    private val tokenService: TokenService,
    private val eventRepository: com.authservice.core.domain.repository.EventRepository
) {
    fun execute(request: MfaVerifyRequest, ipAddress: String? = null, userAgent: String? = null): LoginResponse {
        val user = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("User not found")

        val mfa = userMfaRepository.findByUserId(user.id)
            ?: throw IllegalArgumentException("MFA not set up")

        if (!mfa.enabled) {
            throw IllegalStateException("MFA is not enabled for this user")
        }

        if (!mfaService.verifyCode(mfa.secret, request.code)) {
            throw IllegalArgumentException("Invalid MFA code")
        }

        // MFA Verified, issuing final tokens
        val accessToken = tokenService.generateAccessToken(user)
        val refreshToken = tokenService.generateRefreshToken(user)

        eventRepository.save(
            com.authservice.core.domain.model.Event(
                applicationId = user.applicationId,
                type = com.authservice.core.domain.model.EventType.USER_LOGGED_IN,
                ipAddress = ipAddress,
                userAgent = userAgent,
                metadata = mapOf("userId" to user.id.toString(), "mfa" to "true")
            )
        )

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            user = UserResponse(
                id = user.id,
                email = user.email,
                roles = user.roles
            )
        )
    }
}
