package com.authservice.core.application.usecase

import com.authservice.core.application.dto.ApplicationResponse
import com.authservice.core.application.dto.IdentityProviderConfigResponse
import com.authservice.core.domain.repository.ApplicationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListApplicationsUseCase(
    private val applicationRepository: ApplicationRepository,
    private val identityProviderRepository: com.authservice.core.domain.repository.IdentityProviderRepository
) {
    fun execute(tenantId: UUID): List<ApplicationResponse> {
        return applicationRepository.findByTenantId(tenantId).map { app ->
            val allConfigs = identityProviderRepository.findByApplicationId(app.id)
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

            ApplicationResponse(
                id = app.id,
                tenantId = app.tenantId,
                name = app.name,
                clientId = app.clientId,
                clientSecret = app.clientSecret,
                redirectUris = app.redirectUris.toSet(),
                roles = app.roles.toSet(),
                roleRedirects = app.roleRedirects,
                identityProviders = providers,
                providerConfigs = providerConfigs
            )
        }
    }
}
