package com.authservice.core.application.usecase
 
import java.util.UUID

import com.authservice.core.application.dto.LoginRequest
import com.authservice.core.application.dto.LoginResponse
import com.authservice.core.application.dto.UserResponse
import com.authservice.core.domain.repository.UserRepository
import com.authservice.core.domain.service.PasswordHasher
import com.authservice.core.domain.service.TokenService
import org.springframework.stereotype.Service

@Service
class LoginUseCase(
    private val userRepository: UserRepository,
    private val passwordHasher: PasswordHasher,
    private val tokenService: TokenService,
    private val eventRepository: com.authservice.core.domain.repository.EventRepository,
    private val userMfaRepository: com.authservice.core.domain.repository.UserMfaRepository,
    private val applicationRepository: com.authservice.core.domain.repository.ApplicationRepository
) {
    fun execute(request: LoginRequest, ipAddress: String? = null, userAgent: String? = null): LoginResponse {
        val foundUser = userRepository.findByEmail(request.email)
            ?: throw IllegalArgumentException("Invalid credentials")

        if (!passwordHasher.verify(request.password, foundUser.passwordHash)) {
            throw IllegalArgumentException("Invalid credentials")
        }

        val mfa = userMfaRepository.findByUserId(foundUser.id)
        if (mfa != null && mfa.enabled) {
            // Generate a temporary token that expires in 5 minutes
            val mfaToken = tokenService.generateAccessToken(foundUser) // We'll assume this token is used to identify the user for MFA

            return LoginResponse(
                mfaRequired = true,
                mfaToken = mfaToken,
                user = UserResponse(
                    id = foundUser.id,
                    email = foundUser.email,
                    roles = foundUser.roles
                )
            )
        }

        val app = applicationRepository.findById(request.applicationId ?: foundUser.applicationId)
        val redirectUrl = app?.redirectUris?.firstOrNull()

        val accessToken = tokenService.generateAccessToken(foundUser, redirectUrl)
        val refreshToken = tokenService.generateRefreshToken(foundUser)

        eventRepository.save(
            com.authservice.core.domain.model.Event(
                applicationId = app?.id ?: foundUser.applicationId,
                type = com.authservice.core.domain.model.EventType.USER_LOGGED_IN,
                ipAddress = ipAddress,
                userAgent = userAgent,
                metadata = mapOf("userId" to foundUser.id.toString())
            )
        )

        return LoginResponse(
            accessToken = accessToken,
            refreshToken = refreshToken,
            redirectUrl = redirectUrl,
            user = UserResponse(
                id = foundUser.id,
                email = foundUser.email,
                roles = foundUser.roles
            )
        )
    }
}
