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
    private val applicationRepository: com.authservice.core.domain.repository.ApplicationRepository,
    private val tokenService: TokenService,
    private val eventRepository: com.authservice.core.domain.repository.EventRepository,
    private val googleAuthService: com.authservice.core.infrastructure.security.GoogleAuthService,
    private val zohoAuthService: com.authservice.core.infrastructure.security.ZohoAuthService
) {
    @Transactional
    fun execute(request: SsoCallbackRequest, ipAddress: String? = null, userAgent: String? = null): LoginResponse {
        var externalId = request.externalId
        var email = request.email

        val application = applicationRepository.findById(request.applicationId)
            ?: throw IllegalArgumentException("Application not found")

        // 1. Verify provider is enabled for this application
        val config = identityProviderRepository.findByApplicationIdAndProviderName(request.applicationId, request.provider)
            ?: throw IllegalArgumentException("SSO Provider '${request.provider}' not configured for this application")
        
        if (!config.enabled) {
            throw IllegalStateException("SSO Provider '${request.provider}' is currently disabled")
        }

        // 2. Real Token Verification (if applicable)
        if (request.provider.lowercase(Locale.getDefault()) == "google" && !request.idToken.isNullOrBlank()) {
            val idToken = googleAuthService.verifyToken(request.idToken, config.clientId)
                ?: throw IllegalArgumentException("Invalid Google ID Token")
            
            externalId = idToken.payload.subject
            email = idToken.payload.email
        } else if (request.provider.lowercase(Locale.getDefault()) == "zoho" && !request.idToken.isNullOrBlank()) {
            val zohoUser = zohoAuthService.verifyCode(
                code = request.idToken,
                clientId = config.clientId,
                clientSecret = config.clientSecret,
                redirectUri = request.redirectUri ?: "http://localhost:5173/playground" // Fallback for safety
            ) ?: throw IllegalArgumentException("Invalid Zoho Auth Code or Credentials")
            
            externalId = zohoUser.id
            email = zohoUser.email
        }

        if (externalId.isNullOrBlank() || email.isNullOrBlank()) {
            throw IllegalArgumentException("Could not resolve identity from provider")
        }

        // 3. Find existing identity
        var externalIden = externalIdentityRepository.findByProviderAndExternalId(request.provider, externalId!!)
        
        val user: User
        if (externalIden == null) {
            // 3. Auto-register if user doesn't exist
            val existingUser = userRepository.findByEmail(email!!)
            if (existingUser != null) {
                // Link to existing user if email matches (Trust the IDP)
                user = existingUser
            } else {
                // Create new user
                user = userRepository.save(User(
                    applicationId = request.applicationId,
                    tenantId = application.tenantId,
                    email = email!!,
                    passwordHash = "SSO_USER_${UUID.randomUUID()}", // Placeholder, they won't use it
                    roles = setOf("USER")
                ))
            }

            externalIden = externalIdentityRepository.save(ExternalIdentity(
                userId = user.id,
                provider = request.provider,
                externalId = externalId!!,
                externalEmail = email!!
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
