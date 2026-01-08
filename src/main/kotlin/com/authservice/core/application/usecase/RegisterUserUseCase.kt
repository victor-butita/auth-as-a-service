package com.authservice.core.application.usecase

import com.authservice.core.application.dto.RegisterUserRequest
import com.authservice.core.application.dto.UserResponse
import com.authservice.core.domain.model.User
import com.authservice.core.domain.repository.UserRepository
import com.authservice.core.domain.service.PasswordHasher
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RegisterUserUseCase(
    private val userRepository: UserRepository,
    private val applicationRepository: com.authservice.core.domain.repository.ApplicationRepository,
    private val passwordHasher: PasswordHasher,
    private val eventRepository: com.authservice.core.domain.repository.EventRepository
) {
    @Transactional
    fun execute(request: RegisterUserRequest, ipAddress: String? = null, userAgent: String? = null): UserResponse {
        val application = applicationRepository.findById(request.applicationId)
            ?: throw IllegalArgumentException("Application not found")

        if (userRepository.findByEmail(request.email) != null) {
            throw IllegalStateException("User already exists with email: ${request.email}")
        }

        val hashedPassword = passwordHasher.hash(request.password)
        
        val user = User(
            applicationId = request.applicationId,
            tenantId = application.tenantId, // Set tenantId from application
            email = request.email,
            passwordHash = hashedPassword,
            roles = request.roles ?: setOf("USER"),
            metadata = request.metadata ?: emptyMap() // Set metadata from request
        )

        val savedUser = userRepository.save(user)

        eventRepository.save(
            com.authservice.core.domain.model.Event(
                applicationId = savedUser.applicationId,
                type = com.authservice.core.domain.model.EventType.USER_REGISTERED,
                ipAddress = ipAddress,
                userAgent = userAgent,
                metadata = mapOf("userId" to savedUser.id.toString())
            )
        )

        return UserResponse(
            id = savedUser.id,
            email = savedUser.email,
            roles = savedUser.roles,
            metadata = savedUser.metadata
        )
    }
}
