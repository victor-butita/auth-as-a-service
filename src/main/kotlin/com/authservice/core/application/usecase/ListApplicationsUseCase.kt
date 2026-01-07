package com.authservice.core.application.usecase

import com.authservice.core.application.dto.ApplicationResponse
import com.authservice.core.domain.repository.ApplicationRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ListApplicationsUseCase(
    private val applicationRepository: ApplicationRepository
) {
    fun execute(tenantId: UUID): List<ApplicationResponse> {
        return applicationRepository.findByTenantId(tenantId).map {
            ApplicationResponse(
                id = it.id,
                tenantId = it.tenantId,
                name = it.name,
                clientId = it.clientId,
                clientSecret = it.clientSecret,
                redirectUris = it.redirectUris
            )
        }
    }
}
