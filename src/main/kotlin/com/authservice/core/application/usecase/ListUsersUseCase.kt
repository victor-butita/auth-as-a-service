package com.authservice.core.application.usecase

import com.authservice.core.application.dto.UserListResponse
import com.authservice.core.domain.repository.ExternalIdentityRepository
import com.authservice.core.domain.repository.UserRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListUsersUseCase(
    private val userRepository: UserRepository,
    private val externalIdentityRepository: ExternalIdentityRepository,
    private val applicationRepository: com.authservice.core.domain.repository.ApplicationRepository
) {
    fun execute(applicationId: UUID): List<UserListResponse> {
        val users = userRepository.findByApplicationId(applicationId)
        return users.map { user -> mapToResponse(user) }
    }

    fun executeGlobal(): List<UserListResponse> {
        val users = userRepository.findAll()
        return users.map { user -> mapToResponse(user) }
    }

    private fun mapToResponse(user: com.authservice.core.domain.model.User): UserListResponse {
        val identities = externalIdentityRepository.findByUserId(user.id)
        val providers = identities.map { it.provider }.toMutableList()
        // If they have no external identities, they are "DIRECT" (password)
        if (providers.isEmpty()) {
            providers.add("PASSWORD")
        }

        val application = applicationRepository.findById(user.applicationId)
        val applicationName = application?.name ?: "Unknown"

        return UserListResponse(
            id = user.id,
            email = user.email,
            roles = user.roles,
            providers = providers,
            createdAt = user.createdAt.toString(),
            applicationId = user.applicationId,
            applicationName = applicationName
        )
    }
}
