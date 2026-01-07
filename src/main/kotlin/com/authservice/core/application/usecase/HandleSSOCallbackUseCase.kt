package com.authservice.core.application.usecase

import com.authservice.core.application.dto.LoginResponse
import com.authservice.core.application.dto.SsoCallbackRequest
import com.authservice.core.application.dto.UserResponse
import com.authservice.core.domain.model.ExternalIdentity
import com.authservice.core.domain.model.User
import com.authservice.core.domain.repository.ExternalIdentityRepository
import com.authservice.core.domain.repository.IdentityProviderRepository
import com.authservice.core.domain.repository.UserRepository
import com.authservice.core.domain.service.TokenService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class HandleSSOCallbackUseCase(
    private val externalIdentityRepository: ExternalIdentityRepository,
    private val userRepository: UserRepository,
    private val identityProviderRepository: IdentityProviderRepository,
    private val tokenService: TokenService,
    private val eventRepository: com.authservice.core.domain.repository.EventRepository
) {
    @Transactional
    fun execute(request: SsoCallbackRequest, ipAddress: String? = null, userAgent: String? = null): LoginResponse {
        // 1. Verify provider is enabled for this application
        val config = identityProviderRepository.findByApplicationIdAndProviderName(request.applicationId, request.provider)
            ?: throw IllegalArgumentException("SSO Provider '${request.provider}' not configured for this application")
        
        if (!config.enabled) {
            throw IllegalStateException("SSO Provider '${request.provider}' is currently disabled")
        }

        // 2. Find existing identity
        var externalIden = externalIdentityRepository.findByProviderAndExternalId(request.provider, request.externalId)
        
        val user: User
        if (externalIden == null) {
            // 3. Auto-register if user doesn't exist
            val existingUser = userRepository.findByEmail(request.email)
            if (existingUser != null) {
                // Link to existing user if email matches (Trust the IDP)
                user = existingUser
            } else {
                // Create new user
                user = userRepository.save(User(
                    applicationId = request.applicationId,
                    email = request.email,
                    passwordHash = "SSO_USER_${UUID.randomUUID()}", // Placeholder, they won't use it
                    roles = setOf("USER")
                ))
            }

            externalIden = externalIdentityRepository.save(ExternalIdentity(
                userId = user.id,
                provider = request.provider,
                externalId = request.externalId,
                externalEmail = request.email
            ))
        } else {
            user = userRepository.findById(externalIden.userId) 
                ?: throw IllegalStateException("Linked user not found for external identity")
        }

        // 4. Generate Tokens
        val accessToken = tokenService.generateAccessToken(user)
        val refreshToken = tokenService.generateRefreshToken(user)

        // 5. Audit
        eventRepository.save(
            com.authservice.core.domain.model.Event(
                applicationId = user.applicationId,
                type = com.authservice.core.domain.model.EventType.USER_LOGGED_IN,
                ipAddress = ipAddress,
                userAgent = userAgent,
                metadata = mapOf(
                    "userId" to user.id.toString(), 
                    "method" to "SSO", 
                    "provider" to request.provider
                )
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
