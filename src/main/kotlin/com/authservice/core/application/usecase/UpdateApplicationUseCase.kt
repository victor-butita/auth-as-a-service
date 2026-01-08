package com.authservice.core.application.usecase

import com.authservice.core.application.dto.ApplicationResponse
import com.authservice.core.application.dto.IdentityProviderConfigResponse
import com.authservice.core.application.dto.UpdateApplicationRequest
import com.authservice.core.domain.model.IdentityProviderConfig
import com.authservice.core.domain.repository.ApplicationRepository
import com.authservice.core.domain.repository.IdentityProviderRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
class UpdateApplicationUseCase(
    private val applicationRepository: ApplicationRepository,
    private val identityProviderRepository: IdentityProviderRepository
) {
    @Transactional
    fun execute(id: UUID, request: UpdateApplicationRequest): ApplicationResponse {
        val application = applicationRepository.findById(id)
            ?: throw IllegalArgumentException("Application not found with ID: $id")

        val updatedApp = application.copy(
            name = request.name ?: application.name,
            redirectUris = request.redirectUris ?: application.redirectUris,
            roles = request.roles ?: application.roles,
            roleRedirects = request.roleRedirects ?: application.roleRedirects,
            registrationFields = request.registrationFields ?: application.registrationFields
        )

        val savedApp = applicationRepository.save(updatedApp)

        // Update Identity Providers
        if (request.identityProviders != null) {
            val existingConfigs = identityProviderRepository.findByApplicationId(id)
            val requestedProviders = request.identityProviders.map { it.lowercase(Locale.getDefault()) }

            // Disable or keep existing ones
            existingConfigs.forEach { config ->
                val shouldBeEnabled = requestedProviders.contains(config.providerName)
                if (config.enabled != shouldBeEnabled) {
                    identityProviderRepository.save(config.copy(enabled = shouldBeEnabled))
                }
            }

            // Create new ones
            requestedProviders.forEach { providerName ->
                if (existingConfigs.none { it.providerName == providerName }) {
                    identityProviderRepository.save(IdentityProviderConfig(
                        applicationId = id,
                        providerName = providerName,
                        clientId = "CLIENT_ID_FOR_${providerName.uppercase(Locale.getDefault())}",
                        clientSecret = "CLIENT_SECRET_FOR_${providerName.uppercase(Locale.getDefault())}",
                        enabled = true
                    ))
                }
            }
        }

        // Apply specific credential updates
        if (request.providerConfigs != null) {
            val configs = identityProviderRepository.findByApplicationId(id)
            request.providerConfigs.forEach { update ->
                configs.find { it.providerName == update.providerName.lowercase(Locale.getDefault()) }?.let { config ->
                    identityProviderRepository.save(config.copy(
                        clientId = update.clientId ?: config.clientId,
                        clientSecret = update.clientSecret ?: config.clientSecret,
                        discoveryUrl = update.discoveryUrl ?: config.discoveryUrl,
                        enabled = update.enabled ?: config.enabled
                    ))
                }
            }
        }

        val finalConfigs = identityProviderRepository.findByApplicationId(id)
        
        val finalProviders = finalConfigs
            .filter { it.enabled }
            .map { it.providerName }

        val providerConfigs = finalConfigs.map { 
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
            registrationFields = savedApp.registrationFields,
            identityProviders = finalProviders,
            providerConfigs = providerConfigs
        )
    }
}
