package com.authservice.core.application.usecase

import com.authservice.core.application.dto.ApplicationResponse
import com.authservice.core.domain.repository.ApplicationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RotateClientSecretUseCase(
    private val applicationRepository: ApplicationRepository
) {
    @Transactional
    fun execute(id: UUID): ApplicationResponse {
        val application = applicationRepository.findById(id)
            ?: throw IllegalArgumentException("Application not found with ID: \${id}")

        val updatedApp = application.copy(clientSecret = UUID.randomUUID().toString())
        val savedApp = applicationRepository.save(updatedApp)

        return ApplicationResponse(
            id = savedApp.id,
            tenantId = savedApp.tenantId,
            name = savedApp.name,
            clientId = savedApp.clientId,
            clientSecret = savedApp.clientSecret,
            redirectUris = savedApp.redirectUris
        )
    }
}
