package com.authservice.core.application.usecase

import com.authservice.core.application.dto.UserResponse
import com.authservice.core.domain.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class IdentifyUserUseCase(
    private val userRepository: UserRepository
) {
    fun execute(email: String): UserResponse {
        val foundUser = userRepository.findByEmail(email)
            ?: throw IllegalArgumentException("User not found via global lookup")

        return UserResponse(
            id = foundUser.id,
            email = foundUser.email,
            roles = foundUser.roles,
            metadata = foundUser.metadata
        )
    }
}
