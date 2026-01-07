package com.authservice.core.application.usecase

import com.authservice.core.application.dto.UpdateUserRolesRequest
import com.authservice.core.application.dto.UserResponse
import com.authservice.core.domain.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UpdateUserRolesUseCase(
    private val userRepository: UserRepository
) {
    @Transactional
    fun execute(request: UpdateUserRolesRequest): UserResponse {
        val user = userRepository.findById(request.userId)
            ?: throw IllegalArgumentException("User not found with ID: \${request.userId}")

        val updatedUser = user.copy(
            roles = request.roles,
            updatedAt = java.time.LocalDateTime.now()
        )
        
        val savedUser = userRepository.save(updatedUser)

        return UserResponse(
            id = savedUser.id,
            email = savedUser.email,
            roles = savedUser.roles
        )
    }
}
