package com.authservice.core.application.usecase

import com.authservice.core.application.dto.ApplicationResponse
import com.authservice.core.application.dto.IdentityProviderConfigResponse
import com.authservice.core.domain.repository.ApplicationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class RotateClientSecretUseCase(
    private val applicationRepository: ApplicationRepository,
    private val identityProviderRepository: com.authservice.core.domain.repository.IdentityProviderRepository
) {
    @Transactional
    fun execute(id: UUID): ApplicationResponse {
        val application = applicationRepository.findById(id)
            ?: throw IllegalArgumentException("Application not found with ID: \${id}")

        val updatedApp = application.copy(clientSecret = UUID.randomUUID().toString())
        val savedApp = applicationRepository.save(updatedApp)

        val allConfigs = identityProviderRepository.findByApplicationId(savedApp.id)
        val providers = allConfigs
            .filter { it.enabled }
            .map { it.providerName }
        
        val providerConfigs = allConfigs.map { 
            IdentityProviderConfigResponse(
                providerName = it.providerName,
                clientId = it.clientId,
                clientSecret = it.clientSecret,
                discoveryUrl = it.discoveryUrl,
                enabled = it.enabled
            )
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
            identityProviders = providers,
            providerConfigs = providerConfigs
        )
    }
}
