package com.authservice.core.application.usecase

import com.authservice.core.application.dto.ApplicationResponse
import com.authservice.core.application.dto.CreateApplicationRequest
import com.authservice.core.domain.model.Application
import com.authservice.core.domain.repository.ApplicationRepository
import com.authservice.core.domain.repository.TenantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CreateApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val tenantRepository: TenantRepository
) {
    @Transactional
    fun execute(request: CreateApplicationRequest): ApplicationResponse {
        tenantRepository.findById(request.tenantId)
            ?: throw IllegalArgumentException("Tenant not found with ID: \${request.tenantId}")

        val application = Application(
            tenantId = request.tenantId,
            name = request.name,
            redirectUris = request.redirectUris,
            roles = request.roles,
            roleRedirects = request.roleRedirects
        )

        val savedApp = applicationRepository.save(application)

        return ApplicationResponse(
            id = savedApp.id,
            tenantId = savedApp.tenantId,
            name = savedApp.name,
            clientId = savedApp.clientId,
            clientSecret = savedApp.clientSecret,
            redirectUris = savedApp.redirectUris,
            roles = savedApp.roles,
            roleRedirects = savedApp.roleRedirects
        )
    }
}
