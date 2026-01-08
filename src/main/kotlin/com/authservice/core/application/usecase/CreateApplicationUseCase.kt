package com.authservice.core.application.usecase

import com.authservice.core.application.dto.ApplicationResponse
import com.authservice.core.application.dto.IdentityProviderConfigResponse
import com.authservice.core.application.dto.CreateApplicationRequest
import com.authservice.core.domain.model.Application
import com.authservice.core.domain.repository.ApplicationRepository
import com.authservice.core.domain.repository.TenantRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class CreateApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val tenantRepository: TenantRepository,
    private val identityProviderRepository: com.authservice.core.domain.repository.IdentityProviderRepository
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
            roleRedirects = request.roleRedirects,
            registrationFields = request.registrationFields
        )

        val savedApp = applicationRepository.save(application)

        // Save Identity Providers
        request.identityProviders.forEach { provider ->
            identityProviderRepository.save(com.authservice.core.domain.model.IdentityProviderConfig(
                applicationId = savedApp.id,
                providerName = provider.lowercase(Locale.getDefault()),
                clientId = "CLIENT_ID_FOR_${provider.uppercase(Locale.getDefault())}",
                clientSecret = "CLIENT_SECRET_FOR_${provider.uppercase(Locale.getDefault())}",
                enabled = true
            ))
        }

        return ApplicationResponse(
            id = savedApp.id,
            tenantId = savedApp.tenantId,
            name = savedApp.name,
            clientId = savedApp.clientId,
            clientSecret = savedApp.clientSecret,
            redirectUris = savedApp.redirectUris.toSet(),
            roles = savedApp.roles.toSet(),
            roleRedirects = savedApp.roleRedirects,
            registrationFields = savedApp.registrationFields,
            identityProviders = request.identityProviders,
            providerConfigs = request.identityProviders.map { 
                IdentityProviderConfigResponse(
                    providerName = it.lowercase(Locale.getDefault()),
                    clientId = "CLIENT_ID_FOR_${it.uppercase(Locale.getDefault())}",
                    clientSecret = "CLIENT_SECRET_FOR_${it.uppercase(Locale.getDefault())}",
                    discoveryUrl = null,
                    enabled = true
                )
            }
        )
    }
}
